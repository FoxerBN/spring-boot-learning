package com.spring.demo.domain;

public enum TaskStatus {
    NEW,
    DONE,
    IN_PROGRESS;

    public static TaskStatus fromString(String status){
        return switch(status){
            case "NEW" -> NEW;
            case "DONE" -> DONE;
            case "IN_PROGRESS" -> IN_PROGRESS;
            default -> throw new IllegalArgumentException("Unknown status: " + status);
        };
    }
}
