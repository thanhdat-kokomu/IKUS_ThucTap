package com.tu00128.taskmgmt.service;

import com.tu00128.taskmgmt.dto.request.CreateProjectRequest;
import com.tu00128.taskmgmt.dto.response.ProjectResponse;
import com.tu00128.taskmgmt.entity.ProjectEntity;
import com.tu00128.taskmgmt.entity.unum.ProjectStatus;
import com.tu00128.taskmgmt.exception.BusinessException;
import com.tu00128.taskmgmt.exception.ErrorCode;
import com.tu00128.taskmgmt.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Transactional
    @PreAuthorize("hasRole('MANAGER')")  // Chỉ MANAGER mới tạo project
    public ProjectResponse createProject(CreateProjectRequest request) {
        log.info("Creating new project: {}", request.getName());

        ProjectEntity project = new ProjectEntity(request.getName());

        if (request.getStatus() != null) {
            try {
                project.setStatus(ProjectStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Invalid project status");
            }
        }

        ProjectEntity savedProject = projectRepository.save(project);
        log.info("Project created with ID: {}", savedProject.getId());

        return mapToResponse(savedProject);
    }

    // MANAGER xem tất cả projects
    @PreAuthorize("hasRole('MANAGER')")
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // MANAGER xem project theo ID
    @PreAuthorize("hasRole('MANAGER')")
    public ProjectResponse getProjectById(Long id) {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));
        return mapToResponse(project);
    }

    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    public ProjectResponse updateProject(Long id, CreateProjectRequest request) {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        project.setName(request.getName());
        if (request.getStatus() != null) {
            try {
                project.setStatus(ProjectStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Invalid project status");
            }
        }

        ProjectEntity updatedProject = projectRepository.save(project);
        return mapToResponse(updatedProject);
    }

    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }
        projectRepository.deleteById(id);
        log.info("Project deleted: {}", id);
    }

    private ProjectResponse mapToResponse(ProjectEntity project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getStatus().name(),
                project.getTasks() != null ? project.getTasks().size() : 0
        );
    }
}