package com.tu00128.taskmgmt.repository;

import com.tu00128.taskmgmt.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
}