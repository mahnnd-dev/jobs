package vn.ndm.jobdatabase.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.ndm.jobdatabase.constans.SQLConstants;
import vn.ndm.jobdatabase.utils.FileUtils;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

@Slf4j
@Service
public class GenerateDataService {
    private final List<DataSource> dataSourceMap;
    private static final String SLASH = "/";
    private static final String EXPORT_DIR = "Data/";

    @Value("#{'${detection.module.table}'.split(',')}")
    private List<String> tables;

    public GenerateDataService(@Qualifier("mysql") List<DataSource> dataSourceMapVgo) {
        this.dataSourceMap = dataSourceMapVgo;
    }

    public List<String> getAllTableNames(DataSource db) {
        List<String> tableNames = new ArrayList<>();
        if (!tables.isEmpty()) {
            log.info("Generate tables config");
            return tables;
        }
        log.info("Generate all tables");
        try (Connection connection = db.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQLConstants.TABLE_V2.getValue())){
            preparedStatement.setString(1, connection.getSchema());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String tableName = resultSet.getString("table_name");
                tableNames.add(tableName.toUpperCase());
            }
        } catch (SQLException e) {
            log.error("Error while getting all table names: {}", e.getMessage());
        }
        return tableNames;
    }

    public void generateTable() {
        log.info("#generateTable");
        for (DataSource db : dataSourceMap) {
            List<String> tableNames = getAllTableNames(db);
            for (String tb : tableNames) {
                String tableName = tb.toUpperCase();
                try (Connection connection = db.getConnection()){
                    List<String> columnNames = getAllColumns(connection, tableName);
                    List<Map<String, Object>> data = getAllData(connection, tableName);
                    FileUtils.folderIsExist(EXPORT_DIR + tableName);
                    String path = EXPORT_DIR + tableName + SLASH + tableName + ".txt";
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))){
                        if (!data.isEmpty()) {
                            for (Map<String, Object> row : data) {
                                String values = buildValuesString(tableName, row, columnNames);
                                writer.write(values);
                            }
                        }
                    }
                    log.info("Table {} generated successfully!", tableName);
                } catch (IOException | SQLException e) {
                    log.error("Error while generating table {}: {}", tableName, e.getMessage());
                }
            }
        }
    }

    private String buildValuesString(String table, Map<String, Object> row, List<String> columns) {
        StringBuilder sb = new StringBuilder();
        String columnList = String.join(", ", columns);
        sb.append("INSERT INTO ").append(table).append(" (").append(columnList).append(") VALUES (");
        for (int i = 0; i < columns.size(); i++) {
            Object value = row.get(columns.get(i));
            if (value != null) {
                if (value instanceof Date) { // kiểm tra xem giá trị có phải kiểu Date không
                    sb.append("TO_DATE('");
                    sb.append(new SimpleDateFormat("dd/MM/yyyy").format(value)); // chuyển định dạng của Date thành chuỗi
                    sb.append("', 'DD/MM/YYYY')");
                }else{
                    sb.append("'");
                    sb.append(value);
                    sb.append("'");
                }
            }else{
                sb.append("NULL");
            }
            if (i < columns.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(");").append("\n");
        return sb.toString();
    }


    private List<String> getAllColumns(Connection connection, String tableName) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQLConstants.COLUMN_TABLE.getValue())){
            preparedStatement.setString(1, tableName);
            preparedStatement.setString(2, connection.getSchema());
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                List<String> columns = new ArrayList<>();
                while (resultSet.next()) {
                    columns.add(resultSet.getString("COLUMN_NAME"));
                }
                return columns;
            }
        } catch (SQLException e) {
            log.info("Error getAllColumns {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> getAllData(Connection connection, String tableName) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM ").append(tableName);
        try (PreparedStatement preparedStatement = connection.prepareStatement(builder.toString());
             ResultSet resultSet = preparedStatement.executeQuery()){
            List<Map<String, Object>> data = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                data.add(row);
            }
            return data;
        } catch (SQLException e) {
            log.info("Error getAllData {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
