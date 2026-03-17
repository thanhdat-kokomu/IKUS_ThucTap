package com.tu00128.taskmgmt.entity;

import com.tu00128.taskmgmt.entity.unum.TaskStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    // Constructor mặc định
    protected TaskEntity() {}

    // Constructor đầy đủ
    public TaskEntity(String title, UserEntity user, ProjectEntity project) {
        this.title = title;
        this.user = user;
        this.project = project;
        this.status = TaskStatus.TODO;
    }

    // Constructor chỉ với title và project (khi chưa có user)
    public TaskEntity(String title, ProjectEntity project) {
        this.title = title;
        this.project = project;
        this.status = TaskStatus.TODO;
    }

    // ===== GETTERS =====
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public UserEntity getUser() {
        return user;
    }

    public ProjectEntity getProject() {
        return project;
    }

    // ===== SETTERS =====
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    // THÊM method này - QUAN TRỌNG
    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    // Helper method
    public void assignUser(UserEntity user) {
        this.user = user;
    }
}