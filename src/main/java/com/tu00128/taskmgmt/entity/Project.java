package com.tu00128.taskmgmt.entity;


import com.tu00128.taskmgmt.entity.unum.ProjectStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Project {

    private Long id;
    private String name;
    private ProjectStatus status;
    private List<Task> tasks;

    // Constructor
    public Project(Long id, String name) {
        if (id == null) {
            throw new IllegalArgumentException("Project id cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }

        this.id = id;
        this.name = name;
        this.status = ProjectStatus.PLANNING;
        this.tasks = new ArrayList<>();
    }

    // Getter
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    // Change project status
    public void changeStatus(ProjectStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Project status cannot be null");
        }
        this.status = newStatus;
    }

    // Add task to project
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        if (this.status == ProjectStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot add task to archived project");
        }

        boolean duplicated = tasks.stream()
                .anyMatch(t -> t.getTitle().equalsIgnoreCase(task.getTitle()));

        if (duplicated) {
            throw new IllegalArgumentException(
                    "Task title already exists in this project"
            );
        }

        tasks.add(task);
    }

    // Remove task
    public void removeTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        tasks.remove(task);
    }
}

