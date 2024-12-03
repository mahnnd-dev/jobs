package dev.m.constant;

public enum ResponseConstants {
    SUCCESS("1", "SUCCESS");

    private final String status;
    private final String message;

    ResponseConstants(String status, String message) {
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
