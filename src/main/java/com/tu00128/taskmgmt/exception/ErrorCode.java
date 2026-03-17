
package com.tu00128.taskmgmt.exception;

public enum ErrorCode {
    // User errors
    USER_NOT_FOUND(1001, "User not found"),
    USER_ALREADY_EXISTS(1002, "User already exists"),

    // Project errors
    PROJECT_NOT_FOUND(2001, "Project not found"),
    PROJECT_ARCHIVED(2002, "Project is archived"),

    // Task errors
    TASK_NOT_FOUND(3001, "Task not found"),
    TASK_DONE_CANNOT_MODIFY(3002, "Cannot modify completed task"),
    INVALID_TASK_STATUS_TRANSITION(3003, "Invalid status transition"),
    TASK_TITLE_EXISTS_IN_PROJECT(3004, "Task title already exists in project"),

    ACCESS_DENIED(4030, "Access denied"),
    // Validation errors
    VALIDATION_ERROR(4001, "Validation error"),

    // Authorization errors
    USER_NOT_IN_PROJECT(5001, "User does not belong to this project"),

    // System errors
    INTERNAL_ERROR(9999, "Internal server error");



    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}