package com.tu00128.taskmgmt.entity;

import com.tu00128.taskmgmt.entity.unum.TaskStatus;

public class Task {

    private Long id;
    private String title;
    private TaskStatus status;
    private Project project;
    private User assignedUser;

    // Constructor
    public Task(Long id, String title, Project project) {
        if (id == null) {
            throw new IllegalArgumentException("Task id cannot be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        if (project == null) {
            throw new IllegalArgumentException("Task must belong to a project");
        }

        this.id = id;
        this.title = title;
        this.project = project;
        this.status = TaskStatus.TODO;
    }

    // Getter
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Project getProject() {
        return project;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    // Assign user
    public void assignUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        this.assignedUser = user;
    }

    // Change status with validation
    public void changeStatus(TaskStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Task status cannot be null");
        }

        if (status == TaskStatus.DONE || status == TaskStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change terminal task status");
        }

        if (status == TaskStatus.TODO && newStatus == TaskStatus.IN_PROGRESS ||
                status == TaskStatus.TODO && newStatus == TaskStatus.CANCELLED ||
                status == TaskStatus.IN_PROGRESS && newStatus == TaskStatus.DONE ||
                status == TaskStatus.IN_PROGRESS && newStatus == TaskStatus.CANCELLED) {

            this.status = newStatus;
            return;
        }

        throw new IllegalStateException("Invalid task status transition");
    }
}

