package dev.m.tasklet.info;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Service;
import dev.m.constans.SQLConstants;
import dev.m.utils.FileUtils;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Service
public class DatabaseStatsTool implements Tasklet {

    private static final String SLASH = "/";
    private static final String EXPORT_DIR = "Excel/";


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        run();
        return RepeatStatus.FINISHED;
    }

    public void run() {
        log.info("#run");
        FileUtils.folderIsExist(EXPORT_DIR);
        for (int i = 0; i < vgoDataSources.size(); i++) {
            try (Connection connection = vgoDataSources.get(i).getConnection();
                 Connection connection1 = vgcDataSources.get(i).getConnection()) {
                getTableCount(connection, connection.getSchema());
                getTableCount(connection1, connection1.getSchema());
                compareTables(connection, connection1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void getTableCount(Connection connection, String databaseName) throws SQLException {
        int count = 0;
        try (PreparedStatement statement = connection.prepareStatement(SQLConstants.COUNT_TABLE.getSql())) {
            statement.setString(1, databaseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt("table_count");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            count = 0;
        }
        log.info("#Tổng số bảng trong database {}: {}", connection.getSchema(), count);
    }

    private static void compareTables(Connection db1Connection, Connection db2Connection) {
        int rowNum = 0;
        try (Workbook workbook = new HSSFWorkbook();
             FileOutputStream outputStream = new FileOutputStream(EXPORT_DIR + SLASH + db1Connection.getSchema() + ".xlsx");
             PreparedStatement preparedStatement1 = db1Connection.prepareStatement(SQLConstants.TABLE.getSql());
             PreparedStatement preparedStatement2 = db2Connection.prepareStatement(SQLConstants.TABLE.getSql())) {
            preparedStatement1.setString(1, db1Connection.getSchema());
            preparedStatement2.setString(1, db2Connection.getSchema());
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            ResultSet resultSet2 = preparedStatement2.executeQuery();
            Sheet sheet = workbook.createSheet(db1Connection.getSchema());
            String[] columnNames = {"Table Name", "Source Row Count", "Destination Row Count"};
            Row headerRow = sheet.createRow(rowNum++);
            for (int i = 0; i < columnNames.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnNames[i]);
            }
            int columnCount = sheet.getRow(0).getLastCellNum();
            for (int i = 0; i < columnCount; i++) {
                sheet.autoSizeColumn(i);
            }
            while (resultSet1.next() && resultSet2.next()) {
                String table = resultSet1.getString("table_name");
                int db1RecordCount = getRecordCount(db1Connection, table);
                int db2RecordCount = getRecordCount(db2Connection, table);
                Row row = sheet.createRow(rowNum);
                Cell cell1 = row.createCell(0);
                cell1.setCellValue(table);
                Cell cell2 = row.createCell(1);
                cell2.setCellValue(db1RecordCount);
                Cell cell3 = row.createCell(2);
                cell3.setCellValue(db2RecordCount);
                rowNum++;
            }
            workbook.write(outputStream);
            System.out.println("Data has been written to Excel file successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static int getRecordCount(Connection connection, String tableName) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) as record_count FROM " + tableName);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("record_count");
            }
            return 0;
        } catch (SQLException throwables) {
            log.info("Error exception {}", throwables.getMessage());
            return -1;
        }
    }
}
