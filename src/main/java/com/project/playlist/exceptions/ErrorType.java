package com.project.playlist.exceptions;

public enum ErrorType {

    GENERAL_ERROR("10000", "Unclassified error"),
    INTERNAL_SERVER_ERROR("10001", "Internal server error"),
    NOT_FOUND("10002", "Resource not found"),
    CONFLICT("10003", "Resource already exists"),
    BAD_REQUEST("10004", "Invalid arguments");

    private final String code;
    private final String type;

    ErrorType(String code, String type) {
        this.code = code;
        this.type = type;
    }

    public String code() {
        return code;
    }

    public String type() {
        return type;
    }
}
