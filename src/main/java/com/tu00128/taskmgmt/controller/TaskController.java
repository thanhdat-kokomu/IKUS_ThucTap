package com.tu00128.taskmgmt.controller;

import com.tu00128.taskmgmt.dto.request.CreateTaskRequest;
import com.tu00128.taskmgmt.dto.request.UpdateTaskStatusRequest;
import com.tu00128.taskmgmt.dto.response.ApiResponse;
import com.tu00128.taskmgmt.dto.response.TaskResponseDTO;
import com.tu00128.taskmgmt.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MANAGER')")  // Chỉ MANAGER mới tạo task
    public ApiResponse<TaskResponseDTO> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskResponseDTO task = taskService.createTask(
                request.getUserId(),
                request.getProjectId(),
                request.getTitle()
        );
        return ApiResponse.success("Task created successfully", task);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('MANAGER') or @taskSecurity.isTaskOwner(#userId, authentication.name)")  // USER chỉ xem task của mình
    public ApiResponse<List<TaskResponseDTO>> getByUser(@PathVariable Long userId) {
        List<TaskResponseDTO> tasks = taskService.getTasksByUser(userId);
        return ApiResponse.success(tasks);
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('MANAGER')")  // Chỉ MANAGER xem task theo project
    public ApiResponse<List<TaskResponseDTO>> getByProject(@PathVariable Long projectId) {
        List<TaskResponseDTO> tasks = taskService.getTasksByProject(projectId);
        return ApiResponse.success(tasks);
    }

    @PutMapping("/{taskId}/assign/{userId}")
    @PreAuthorize("hasRole('MANAGER')")  // Chỉ MANAGER assign task
    public ApiResponse<TaskResponseDTO> assignTask(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        TaskResponseDTO task = taskService.assignTask(taskId, userId);
        return ApiResponse.success("Task assigned successfully", task);
    }

    @PatchMapping("/{taskId}/status")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER')")  // Cả USER và MANAGER đều update status
    public ApiResponse<TaskResponseDTO> updateStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {  // Lấy thông tin user đang login
        TaskResponseDTO task = taskService.updateStatus(taskId, request.getStatus(), currentUser.getUsername());
        return ApiResponse.success("Task status updated successfully", task);
    }
}