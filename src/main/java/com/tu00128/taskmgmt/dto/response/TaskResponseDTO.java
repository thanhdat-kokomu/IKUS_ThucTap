package com.tu00128.taskmgmt.dto.response;

import com.tu00128.taskmgmt.entity.TaskEntity;

public class TaskResponseDTO {

    private Long id;
    private String title;
    private String status;
    private String projectName;
    private String username;

    public TaskResponseDTO(Long id, String title, String status,
                           String projectName, String username) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.projectName = projectName;
        this.username = username;
    }
    private TaskResponseDTO mapToDTO(TaskEntity task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getStatus().name(),
                task.getProject().getName(),
                task.getUser() != null ? task.getUser().getUsername() : null
        );
    }
    public static TaskResponseDTO fromEntity(TaskEntity task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getStatus().name(),
                task.getProject() != null ? task.getProject().getName() : null,
                task.getUser() != null ? task.getUser().getUsername() : null
        );
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public String getProjectName() { return projectName; }
    public String getUsername() { return username; }
}
