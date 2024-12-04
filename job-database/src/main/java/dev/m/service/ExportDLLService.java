package dev.m.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import dev.m.constans.SQLConstants;
import dev.m.obj.DataBaseOBJ;
import dev.m.utils.FileUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ExportDLLService {

    private final DataSource dataSourceMap;
    private static final String SLASH = "/";
    private static final String EXPORT_DIR = "DLL/";
    private final List<DataBaseOBJ> listConfig = new ArrayList<>();
    private final ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    public ExportDLLService(DataSource dataSourceMapVgo, ThreadPoolTaskExecutor taskExecutor) {
        this.dataSourceMap = dataSourceMapVgo;
        this.taskExecutor = taskExecutor;
    }

    public void processExport() {
        log.info("#processExport");
        setMapConfig();
        taskExecutor.execute(() -> {
            try (Connection conn = dataSourceMap.getConnection()) {
                listConfig.forEach(v -> exportObjectsToFile(conn, v.getColumn(), v.getSql(), v.getFolder()));
            } catch (Exception e) {
                e.printStackTrace();

            }
        });

    }

    public void setMapConfig() {
        String objectName = "object_name";
        listConfig.add(new DataBaseOBJ(objectName, SQLConstants.PACKAGE.getValue(), "Package"));
        listConfig.add(new DataBaseOBJ(objectName, SQLConstants.FUNCTION.getValue(), "Function"));
        listConfig.add(new DataBaseOBJ(objectName, SQLConstants.PROCEDURE.getValue(), "Procedure"));
        listConfig.add(new DataBaseOBJ("sequence_name", SQLConstants.SEQUENCE.getValue(), "Sequence"));
        listConfig.add(new DataBaseOBJ("trigger_name", SQLConstants.TRIGGER.getValue(), "Trigger"));
        listConfig.add(new DataBaseOBJ("table_name", SQLConstants.TABLE.getValue(), "Table"));
        listConfig.add(new DataBaseOBJ("view_name", SQLConstants.VIEW.getValue(), "View"));
        listConfig.add(new DataBaseOBJ("index_name", SQLConstants.INDEX.getValue(), "Index"));
//        listConfig.add(new DataBaseOBJ("job_name", SQLConstants.JOB.getValue(), "Job"));
//        listConfig.add(new DataBaseOBJ("job", SQLConstants.DBMS_JOB.getValue(), "DBMS_Job"));
        listConfig.add(new DataBaseOBJ("queue_table", SQLConstants.QUEUE_TABLE.getValue(), "Queue_Table"));
        listConfig.add(new DataBaseOBJ("queue_name", SQLConstants.QUEUE.getValue(), "Queue"));
    }

    public void exportObjectsToFile(Connection connection, String colunm, String query, String directoryPath) {
        // Tạo đối tượng PreparedStatement để truy vấn danh sách các đối tượng cần xuất
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             // Thực thi truy vấn và lấy ResultSet
             ResultSet resultSet = preparedStatement.executeQuery()) {
            // Lặp qua ResultSet và xuất các đối tượng
            FileUtils.folderIsExist(EXPORT_DIR + connection.getSchema() + SLASH + directoryPath);
            while (resultSet.next()) {
                String col = resultSet.getString(colunm);
                String dll = resultSet.getString("ddl");
                // Tạo đường dẫn và tên tệp xuất tương ứng với loại đối tượng
                String path = EXPORT_DIR + connection.getSchema() + SLASH + directoryPath + SLASH + col + ".sql";
                // Ghi dữ liệu xuất ra tệp
                FileUtils.writeToFile(path, dll);
                log.info("Export Success file: {}", path);
            }
        } catch (Exception e) {
            log.info("Error export: {}", e.getMessage());
        }
    }
}
