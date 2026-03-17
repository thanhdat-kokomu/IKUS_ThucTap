package com.tu00128.taskmgmt.service;

import com.tu00128.taskmgmt.entity.Project;
import com.tu00128.taskmgmt.entity.Task;
import com.tu00128.taskmgmt.entity.User;
import com.tu00128.taskmgmt.entity.unum.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private final List<Project> projects = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    // ===== USER =====
    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        boolean duplicated = users.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));

        if (duplicated) {
            throw new IllegalArgumentException("Email already exists");
        }

        users.add(user);
    }

    public User findUserById(Long id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found: " + id));
    }

    // ===== PROJECT =====
    public void addProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }
        projects.add(project);
    }

    public Project findProjectById(Long id) {
        return projects.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Project not found: " + id));
    }

    // ===== TASK =====
    public Task createTask(Long taskId, String title, Long projectId) {
        Project project = findProjectById(projectId);

        Task task = new Task(taskId, title, project);
        project.addTask(task);

        return task;
    }

    public void deleteTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        task.getProject().removeTask(task);
    }

    public void assignTaskToUser(Task task, Long userId) {
        User user = findUserById(userId);
        task.assignUser(user);
    }

    public void changeTaskStatus(Task task, TaskStatus newStatus) {
        task.changeStatus(newStatus);
    }
}

