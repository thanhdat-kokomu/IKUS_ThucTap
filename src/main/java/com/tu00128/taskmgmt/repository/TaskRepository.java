package com.tu00128.taskmgmt.repository;

import com.tu00128.taskmgmt.entity.TaskEntity;
import com.tu00128.taskmgmt.entity.unum.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    List<TaskEntity> findByUserId(Long userId);

    List<TaskEntity> findByProjectId(Long projectId);

    List<TaskEntity> findByStatus(TaskStatus status);
}