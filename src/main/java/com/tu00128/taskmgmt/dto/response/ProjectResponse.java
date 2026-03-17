package com.tu00128.taskmgmt.dto.response;

public class ProjectResponse {
    private Long id;
    private String name;
    private String status;
    private int taskCount;

    public ProjectResponse(Long id, String name, String status, int taskCount) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.taskCount = taskCount;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public int getTaskCount() { return taskCount; }
}
