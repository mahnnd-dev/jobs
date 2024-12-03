package dev.m.exception;

public class ApiException extends RuntimeException {
    private String status;

    public ApiException(String status, String message) {
        super(message);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
