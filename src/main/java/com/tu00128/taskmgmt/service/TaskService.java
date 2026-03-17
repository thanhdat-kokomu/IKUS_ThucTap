package com.tu00128.taskmgmt.service;

import com.tu00128.taskmgmt.dto.response.TaskResponseDTO;
import com.tu00128.taskmgmt.entity.ProjectEntity;
import com.tu00128.taskmgmt.entity.TaskEntity;
import com.tu00128.taskmgmt.entity.UserEntity;
import com.tu00128.taskmgmt.entity.unum.TaskStatus;
import com.tu00128.taskmgmt.exception.BusinessException;
import com.tu00128.taskmgmt.exception.ErrorCode;
import com.tu00128.taskmgmt.repository.ProjectRepository;
import com.tu00128.taskmgmt.repository.TaskRepository;
import com.tu00128.taskmgmt.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public TaskResponseDTO createTask(Long userId, Long projectId, String title) {
        // Thêm validation
        if (title == null || title.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Title cannot be empty");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        // Check if task title already exists in project
        boolean taskExists = taskRepository.findByProjectId(projectId)
                .stream()
                .anyMatch(t -> t.getTitle().equalsIgnoreCase(title));

        if (taskExists) {
            throw new BusinessException(ErrorCode.TASK_TITLE_EXISTS_IN_PROJECT);
        }

        TaskEntity task = new TaskEntity(title, user, project);
        task = taskRepository.save(task);

        return TaskResponseDTO.fromEntity(task);
    }

    public List<TaskResponseDTO> getTasksByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return taskRepository.findByUserId(userId)
                .stream()
                .map(TaskResponseDTO::fromEntity)
                .toList();
    }

    public List<TaskResponseDTO> getTasksByProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(TaskResponseDTO::fromEntity)
                .toList();
    }

    public TaskResponseDTO assignTask(Long taskId, Long userId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TASK_NOT_FOUND));

        // Kiểm tra task có project không
        if (task.getProject() == null) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND, "Task has no project");
        }

        if (task.getStatus() == TaskStatus.DONE || task.getStatus() == TaskStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.TASK_DONE_CANNOT_MODIFY,
                    "Cannot modify completed or cancelled task");
        }

        if (userId == null) {
            task.assignUser(null);
            task = taskRepository.save(task);
            return TaskResponseDTO.fromEntity(task);
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getProject() == null || !user.getProject().getId().equals(task.getProject().getId())) {
            throw new BusinessException(ErrorCode.USER_NOT_IN_PROJECT);
        }

        task.assignUser(user);
        task = taskRepository.save(task);

        return TaskResponseDTO.fromEntity(task);
    }

    // CÁCH 2: Thêm method mới với 3 tham số (có kiểm tra username)
    public TaskResponseDTO updateStatus(Long taskId, TaskStatus newStatus, String username) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TASK_NOT_FOUND));

        // Kiểm tra: USER chỉ được update task của mình
        if (username != null && task.getUser() != null) {
            if (!task.getUser().getUsername().equals(username)) {
                throw new BusinessException(ErrorCode.USER_NOT_IN_PROJECT,
                        "You can only update your own tasks");
            }
        }

        // Kiểm tra status transition
        TaskStatus current = task.getStatus();

        if (current == TaskStatus.DONE) {
            throw new BusinessException(ErrorCode.TASK_DONE_CANNOT_MODIFY);
        }

        if (current == TaskStatus.TODO && newStatus == TaskStatus.DONE) {
            throw new BusinessException(ErrorCode.INVALID_TASK_STATUS_TRANSITION,
                    "Must go through IN_PROGRESS first");
        }

        if (current == TaskStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.INVALID_TASK_STATUS_TRANSITION,
                    "Cannot update cancelled task");
        }

        task.setStatus(newStatus);
        task = taskRepository.save(task);

        return TaskResponseDTO.fromEntity(task);
    }

    // Giữ method cũ cho MANAGER (nếu cần)
    public TaskResponseDTO updateStatus(Long taskId, TaskStatus newStatus) {
        return updateStatus(taskId, newStatus, null);
    }
}