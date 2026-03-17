package com.tu00128.taskmgmt.config;

import com.tu00128.taskmgmt.entity.TaskEntity;
import com.tu00128.taskmgmt.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("taskSecurity")
public class TaskSecurity {

    @Autowired
    private TaskRepository taskRepository;

    public boolean isTaskOwner(Long userId, String username) {
        // Kiểm tra xem user có phải là chủ của task không
        return taskRepository.findByUserId(userId)
                .stream()
                .anyMatch(task -> task.getUser() != null &&
                        task.getUser().getUsername().equals(username));
    }
}