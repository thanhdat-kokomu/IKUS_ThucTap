package com.tu00128.taskmgmt.entity;

import com.tu00128.taskmgmt.entity.unum.ProjectStatus;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @OneToMany(mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<TaskEntity> tasks = new ArrayList<>();

    // Constructor mặc định
    protected ProjectEntity() {}

    // Constructor có tham số
    public ProjectEntity(String name) {
        this.name = name;
        this.status = ProjectStatus.PLANNING;
    }

    // ===== GETTERS =====
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public List<TaskEntity> getTasks() {
        return tasks;
    }

    // ===== SETTERS =====
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {   // <-- THÊM method này
        this.name = name;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public void setTasks(List<TaskEntity> tasks) {
        this.tasks = tasks;
    }

    // ===== HELPER METHODS =====
    public void addTask(TaskEntity task) {
        tasks.add(task);
        task.setProject(this);
    }

    public void removeTask(TaskEntity task) {
        tasks.remove(task);
        task.setProject(null);
    }
}