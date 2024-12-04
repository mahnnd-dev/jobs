package dev.m.constant;


public enum StatusSessionConstant {
    USE("0", "USE"),

    UNUSE("1", "UNUSE"),

    EXPIRE("2", "EXPIRE");

    private final String status;
    private final String message;

    StatusSessionConstant(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
