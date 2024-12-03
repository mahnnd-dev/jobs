package vn.ndm.tasklet.export;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import vn.ndm.constans.SQLConstants;
import vn.ndm.obj.DataBaseOBJ;
import vn.ndm.utils.FileUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ExportDLLService implements Tasklet {

    private final List<DataSource> dataSourceMap;
    private static final String SLASH = "/";
    private static final String EXPORT_DIR = "DLL/";
    private final List<DataBaseOBJ> listConfig = new ArrayList<>();

    public ExportDLLService(@Qualifier("eofficev2") List<DataSource> dataSourceMapVgo) {
        this.dataSourceMap = dataSourceMapVgo;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        processExport();
        return RepeatStatus.FINISHED;
    }

    public void processExport() {
        log.info("#processExport");
        setMapConfig();
        for (DataSource db : dataSourceMap) {
            try (Connection conn = db.getConnection()){
                listConfig.forEach(v -> exportObjectsToFile(conn, v.getColumn(), v.getSql(), v.getFolder()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setMapConfig() {
        String objectName = "object_name";
        listConfig.add(new DataBaseOBJ(objectName, SQLConstants.PACKAGE.getSql(), "Package"));
        listConfig.add(new DataBaseOBJ(objectName, SQLConstants.FUNCTION.getSql(), "Function"));
        listConfig.add(new DataBaseOBJ(objectName, SQLConstants.PROCEDURE.getSql(), "Procedure"));
        listConfig.add(new DataBaseOBJ("sequence_name", SQLConstants.SEQUENCE.getSql(), "Sequence"));
        listConfig.add(new DataBaseOBJ("trigger_name", SQLConstants.TRIGGER.getSql(), "Trigger"));
        listConfig.add(new DataBaseOBJ("table_name", SQLConstants.TABLE.getSql(), "Table"));
        listConfig.add(new DataBaseOBJ("view_name", SQLConstants.VIEW.getSql(), "View"));
        listConfig.add(new DataBaseOBJ("index_name", SQLConstants.INDEX.getSql(), "Index"));
        listConfig.add(new DataBaseOBJ("job_name", SQLConstants.JOB.getSql(), "Job"));
        listConfig.add(new DataBaseOBJ("job", SQLConstants.DBMS_JOB.getSql(), "DBMS_Job"));
        listConfig.add(new DataBaseOBJ("queue_table", SQLConstants.QUEUE_TABLE.getSql(), "Queue_Table"));
        listConfig.add(new DataBaseOBJ("queue_name", SQLConstants.QUEUE.getSql(), "Queue"));
    }

    public void exportObjectsToFile(Connection connection, String colunm, String query, String directoryPath) {
        // Tạo đối tượng PreparedStatement để truy vấn danh sách các đối tượng cần xuất
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             // Thực thi truy vấn và lấy ResultSet
             ResultSet resultSet = preparedStatement.executeQuery()){
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
