package vn.ndm.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class SQLUtils {

    public static Set<String> getColunm(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Set<String> columnNames = new HashSet<>();
        for (int i = 1; i <= columnCount; ++i) {
            String columnName = "";
            try {
                columnName = rsmd.getColumnLabel(i);
            } catch (Exception var7) {
                columnName = rsmd.getColumnName(i);
            }
            columnNames.add(columnName);
        }
        return columnNames;
    }

    public static List<Map<String, String>> toListMap(ResultSet rs) throws SQLException {
        List<Map<String, String>> result = new ArrayList<>();
        Set<String> columnNames = getColunm(rs);

        while (rs.next()) {
            Map<String, String> row = new HashMap<>();
            for (String columnName : columnNames) {
                row.put(columnName, rs.getString(columnName));
            }
            result.add(row);
        }
        return result;
    }

    public static Map<String, String> toMap(ResultSet rs) throws SQLException {
        if (rs.next()) {
            Set<String> columnNames = getColunm(rs);
            return toMap(columnNames, rs);
        } else {
            return null;
        }
    }

    public static Map<String, String> toMap(Set<String> columnNames, ResultSet rs) throws SQLException {
        Map<String, String> row = new HashMap<>();
        for (String columnName : columnNames) {
            row.put(columnName, rs.getString(columnName));
        }
        return row;
    }
}
