package vn.ndm.data.transfer.logging;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Component
public abstract class AbstractJobProcessLog {

    private final Logger logger = LoggerFactory.getLogger(AbstractJobProcessLog.class);
    private static final Logger logOfDay = LoggerFactory.getLogger("logs-of-day");

    @Value("${logging.job.path.wait}")
    private String pathWait;

    @Value("${logging.job.path.retry}")
    private String pathRetry;

    @Value("${logging.job.path.failed}")
    private String pathFailed;

    @Value("${logging.job.path.file-pattern}")
    private String filePattern;

    @Value("${logging.job.batch-size}")
    private int batchSize;

    @Value("${logging.job.max-retry}")
    private int maxRetry;

    protected int count = 0;
    private boolean isConnected = false;

    // Lưu trữ tên file và số lần retry
    private final Map<String, Integer> storeFileFail = new HashMap<>();
    private final HikariDataSource ds;
    private PreparedStatement pstmt = null;
    private Connection conn = null;

    @Autowired
    public AbstractJobProcessLog(@Qualifier("db-log") HikariDataSource ds) {
        this.ds = ds;
    }

    /**
     * Hàm xử lý chính, được chạy định kỳ theo cấu hình
     */
    @Scheduled(fixedDelayString = "${logging.job.path.time-read}")
    public void execute() {
        this.readFileLog(getPathWait(), false); // Đọc file mới
        this.readFileLog(getPathRetry(), true); // Đọc file cần retry
    }

    /**
     * Đọc tất cả file log trong thư mục
     *
     * @param path     Đường dẫn đến thư mục chứa file log
     * @param isReload Xác định đây là lần đọc đầu tiên hay là lần retry
     */
    public void readFileLog(String path, boolean isReload) {
        try {
            Files.createDirectories(Paths.get(path)); // Tạo thư mục nếu chưa tồn tại
            initConnect(); // Kết nối database
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path), getFilePattern())) {
                for (Path filePath : stream) {
                    readFileJava8(filePath, isReload); // Xử lý từng file
                }
            }
            disconnect(); // Ngắt kết nối database
        } catch (Exception e) {
            this.logger.error("readFileLog Exception: {}", e.getMessage());
        }
    }

    /**
     * Đọc nội dung của một file log
     *
     * @param filePath Đường dẫn đến file log
     * @param isReload Xác định đây là lần đọc đầu tiên hay là lần retry
     */
    public void readFileJava8(Path filePath, boolean isReload) {
        Instant start = Instant.now();
        try (Stream<String> lines = Files.lines(filePath, StandardCharsets.UTF_8)) {
            lines.forEach(line -> {
                if (!isReload) {
                    logOfDay.info(line); // Ghi log ra file
                }
                if (isConnected) {
                    processLog(line, start, count); // Xử lý từng dòng log
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("readFileJava8 Exception: {}", e.getMessage());
        } finally {
            handleFileAfterRead(filePath.toFile(), isReload); // Xử lý file sau khi đọc
            commitFinal(isConnected); // Commit cuối cùng nếu còn kết nối database
        }
    }

    /**
     * Xử lý từng dòng log, chèn dữ liệu vào batch
     *
     * @param content   Nội dung của dòng log
     * @param startTime Thời điểm bắt đầu xử lý file
     * @param count     Số lượng dòng đã được xử lý
     */
    private void processLog(String content, Instant startTime, int count) {
        if (count > getBatchSize()) {
            logger.info("count {}", count);
            this.commit(startTime); // Commit batch hiện tại
            count = 0;
            logger.info("reset batch size {}", count);
        }
        processData(content); // Xử lý dữ liệu của dòng log
    }

    /**
     * Xử lý file sau khi đọc: xóa file nếu commit thành công, di chuyển file nếu commit thất bại
     *
     * @param f        File log
     * @param isReload Xác định đây là lần đọc đầu tiên hay là lần retry
     */
    private void handleFileAfterRead(File f, boolean isReload) {
        try {
            Instant start = Instant.now();
            if (this.isConnected && this.commit(start)) {
                this.logger.info(">>>>>>>>>>>>>>>> delete file: {} status: {}", f.getAbsolutePath(), Files.deleteIfExists(f.toPath()));
            } else {
                handleFileRenameAndRetry(f, isReload); // Xử lý file cần retry
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("handleFileAfterRead Exception: {}", e.getMessage());
        }
    }

    /**
     * Xử lý file cần retry: di chuyển file vào thư mục retry hoặc failed
     *
     * @param f        File log
     * @param isReload Xác định đây là lần đọc đầu tiên hay là lần retry
     * @throws IOException
     */
    private void handleFileRenameAndRetry(File f, boolean isReload) throws IOException {
        if (getMaxRetry() == -1) {
            if (!isReload) {
                moveFile(getPathRetry(), f); // Di chuyển file vào thư mục retry
            }
        } else if (isReload) {
            handleReloadRetry(f); // Xử lý file retry
        } else {
            storeFileFailAndRetry(f); // Lưu trữ file và di chuyển vào thư mục retry
        }
    }

    /**
     * Xử lý file retry: tăng số lần retry, di chuyển file vào thư mục failed nếu vượt quá số lần retry
     *
     * @param f File log
     * @throws IOException
     */
    private void handleReloadRetry(File f) throws IOException {
        Integer current = this.storeFileFail.get(f.getName());
        if (current != null) {
            if (current < getMaxRetry()) {
                current = current + 1;
                this.storeFileFail.put(f.getName(), current);
            } else {
                moveFile(getPathFailed(), f); // Di chuyển file vào thư mục failed
                this.storeFileFail.remove(f.getName());
            }
        }
    }

    /**
     * Lưu trữ file và di chuyển vào thư mục retry
     *
     * @param f File log
     * @throws IOException
     */
    private void storeFileFailAndRetry(File f) throws IOException {
        this.storeFileFail.put(f.getName(), 0);
        moveFile(getPathRetry(), f); // Di chuyển file vào thư mục retry
    }

    /**
     * Commit cuối cùng nếu còn kết nối database
     *
     * @param isConnected Trạng thái kết nối database
     */
    private void commitFinal(boolean isConnected) {
        try {
            if (isConnected) {
                this.conn.commit(); // Commit transaction
            }
        } catch (SQLException throwables) {
            this.logger.error("storeFileFailAndRetry Exception: {}", throwables.getMessage());
        }
    }

    /**
     * Di chuyển file đến thư mục đích
     *
     * @param targetPath Đường dẫn đến thư mục đích
     * @param sourceFile File cần di chuyển
     * @throws IOException
     */
    private void moveFile(String targetPath, File sourceFile) throws IOException {
        Files.createDirectories(Paths.get(targetPath)); // Tạo thư mục nếu chưa tồn tại
        Path target = Paths.get(targetPath + "/" + sourceFile.getName());
        Files.move(sourceFile.toPath(), target);
        this.logger.error(">>>>>>>>>>>>>>>> move file, rename to: {} to {}", targetPath, sourceFile.toPath());
    }

    /**
     * Xử lý dữ liệu của dòng log, chuyển đổi JSON sang object và chèn vào batch
     *
     * @param data Dữ liệu của dòng log (JSON)
     */
    public abstract void processData(String data);

    /**
     * Commit batch hiện tại vào database
     *
     * @param startTime Thời điểm bắt đầu xử lý batch
     * @return true nếu commit thành công, false nếu commit thất bại
     */
    private boolean commit(Instant startTime) {
        if (!this.isConnected) {
            return false;
        } else {
            try {
                long a = getPstmt().executeBatch().length; // Thực thi batch update
                Instant end = Instant.now();
                long durationInMilliseconds = Duration.between(startTime, end).toMillis();
                this.logger.info("Commit successful num: {} rows; execute time: {} milliseconds", a, durationInMilliseconds);
                getPstmt().clearBatch();
                getPstmt().clearParameters();
                return true;
            } catch (Exception var6) {
                var6.printStackTrace();
                try {
                    this.conn.rollback(); // Rollback transaction nếu commit thất bại
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.logger.error("Commit Exception: {}", var6.getMessage());
                return false;
            }
        }
    }

    /**
     * Khởi tạo kết nối database
     */
    public void initConnect() {
        try {
            logger.info("Init connection: {}", ds.getConnection());
            if (this.isConnected) {
                return;
            }
            this.isConnected = true;
            this.conn = this.ds.getConnection(); // Lấy kết nối từ datasource
            this.conn.setAutoCommit(false); // Tắt auto commit
            this.pstmt = this.conn.prepareStatement(getSql()); // Tạo PreparedStatement
        } catch (Exception var2) {
            var2.printStackTrace();
            this.logger.error("connect database log fail");
            this.isConnected = false;
            this.logger.error("initConnect Exception: {}", var2.getMessage());
        }
    }

    /**
     * Ngắt kết nối database
     */
    public void disconnect() {
        this.isConnected = false;
        try {
            if (pstmt != null)
                this.pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (conn != null){
                logger.info("disconnect connection: {}", ds.getConnection());
                this.conn.close();}
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("disconnect Exception: {}", e.getMessage());
        }
    }

    public abstract String getSql();

    protected void incrementCount() {
        count++;
    }

    public PreparedStatement getPstmt() {
        return pstmt;
    }

    public String getPathWait() {
        return pathWait;
    }

    public void setPathWait(String pathWait) {
        this.pathWait = pathWait;
    }

    public String getPathRetry() {
        return pathRetry;
    }

    public void setPathRetry(String pathRetry) {
        this.pathRetry = pathRetry;
    }

    public String getPathFailed() {
        return pathFailed;
    }

    public void setPathFailed(String pathFailed) {
        this.pathFailed = pathFailed;
    }

    public String getFilePattern() {
        return filePattern;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public HikariDataSource getDs() {
        return ds;
    }

}
