package vn.ndm.jobdatabase.obj;

public class DataBaseOBJ {
    private String column;
    private String sql;
    private String folder;

    public DataBaseOBJ() {
    }

    public DataBaseOBJ(String column, String sql, String folder) {
        this.column = column;
        this.sql = sql;
        this.folder = folder;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
