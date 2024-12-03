package vn.ndm.jobdatabase.constans;

public enum SQLConstants {
    // SQL chỉ lấy ra các bảng đã phân quyền cho schemal
    TABLE("select table_name, dbms_metadata.get_ddl('TABLE', table_name)  AS ddl from user_tables"),
    // SQL lấy ra all bảng của schemal
    TABLE_V2("SELECT table_name FROM all_tables WHERE owner = ?"),
    VIEW("select view_name, dbms_metadata.get_ddl('VIEW', view_name)  AS ddl from user_views"),
    INDEX("select index_name, dbms_metadata.get_ddl('INDEX', index_name)  AS ddl from user_indexes"),
    PACKAGE("SELECT object_name, dbms_metadata.get_ddl('PACKAGE', object_name) AS ddl FROM user_objects WHERE object_type = 'PACKAGE'"),
    FUNCTION("SELECT object_name, dbms_metadata.get_ddl('FUNCTION', object_name) AS ddl FROM user_objects WHERE object_type = 'FUNCTION'"),
    PROCEDURE("SELECT object_name, dbms_metadata.get_ddl('PROCEDURE', object_name) AS ddl FROM user_objects WHERE object_type = 'PROCEDURE'"),
    SEQUENCE("SELECT sequence_name, dbms_metadata.get_ddl('SEQUENCE', sequence_name) AS ddl FROM user_sequences"),
    TRIGGER("SELECT trigger_name, dbms_metadata.get_ddl('TRIGGER', trigger_name) AS ddl FROM user_triggers"),
    JOB("SELECT job_name, dbms_metadata.get_ddl('PROCOBJ', job_name) AS ddl FROM user_scheduler_jobs"),
    DBMS_JOB("SELECT job, dbms_metadata.get_ddl('PROCOBJ', job, 'JOB') AS ddl FROM user_scheduler_jobs"),
    QUEUE_TABLE("SELECT queue_table, dbms_metadata.get_ddl('QUEUE_TABLE', queue_table) AS ddl FROM user_queue_tables"),
    QUEUE("SELECT queue_name, dbms_metadata.get_ddl('QUEUE', queue_name) AS ddl FROM user_queues"),
    COLUMN_TABLE("SELECT COLUMN_NAME FROM ALL_TAB_COLUMNS WHERE TABLE_NAME = ? AND OWNER = ?"),
    COUNT_TABLE("SELECT COUNT(*) as table_count FROM all_tables WHERE owner = ?");

    private final String value;

    SQLConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
