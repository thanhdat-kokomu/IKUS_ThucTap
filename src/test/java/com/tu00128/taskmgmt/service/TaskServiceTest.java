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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InOrder;


import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    private UserEntity user;
    private UserEntity anotherUser;
    private UserEntity assignee;
    private UserEntity currentUser;
    private UserEntity newUser;
    private ProjectEntity project;
    private ProjectEntity anotherProject;
    private TaskEntity task;

    @BeforeEach
    void setUp() {
        // Khởi tạo project
        project = new ProjectEntity("Test Project");
        project.setId(1L);

        anotherProject = new ProjectEntity("Another Project");
        anotherProject.setId(2L);

        // Khởi tạo users
        user = new UserEntity("testuser", "test@example.com", "password");
        user.setId(1L);
        user.setProject(project);

        anotherUser = new UserEntity("anotheruser", "another@example.com", "password");
        anotherUser.setId(2L);
        anotherUser.setProject(anotherProject);

        assignee = new UserEntity("assignee", "assignee@example.com", "password");
        assignee.setId(3L);
        assignee.setProject(project);

        currentUser = new UserEntity("current", "current@example.com", "password");
        currentUser.setId(4L);
        currentUser.setProject(project);

        newUser = new UserEntity("new", "new@example.com", "password");
        newUser.setId(5L);
        newUser.setProject(project);

        // Khởi tạo task
        task = new TaskEntity("Test Task", user, project);
        task.setId(1L);
    }
    public TaskResponseDTO assignTask(Long taskId, Long userId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TASK_NOT_FOUND));

        if (task.getStatus() == TaskStatus.DONE || task.getStatus() == TaskStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.TASK_DONE_CANNOT_MODIFY,
                    "Cannot modify completed or cancelled task");
        }

        if (userId == null) {
            task.assignUser(null);
            task = taskRepository.save(task);
            return TaskResponseDTO.fromEntity(task);
        }

        // Tìm user để assign
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra user có thuộc project của task không
        if (user.getProject() == null || !user.getProject().getId().equals(task.getProject().getId())) {
            throw new BusinessException(ErrorCode.USER_NOT_IN_PROJECT);
        }

        task.assignUser(user);
        task = taskRepository.save(task);

        return TaskResponseDTO.fromEntity(task);
    }
    // ===== TEST CASES CHO createTask (TC01 - TC06) =====

    @Test
    @DisplayName("TC01: Tạo task thành công với dữ liệu hợp lệ")
    void createTask_Success() {
        // Given
        Long userId = 1L;
        Long projectId = 1L;
        String title = "New Task";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findByProjectId(projectId)).thenReturn(Arrays.asList());
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> {
            TaskEntity savedTask = invocation.getArgument(0);
            savedTask.setId(2L);
            return savedTask;
        });

        // When
        TaskResponseDTO result = taskService.createTask(userId, projectId, title);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO.name());
        assertThat(result.getUsername()).isEqualTo(user.getUsername());
        assertThat(result.getProjectName()).isEqualTo(project.getName());

        verify(userRepository).findById(userId);
        verify(projectRepository).findById(projectId);
        verify(taskRepository).findByProjectId(projectId);
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("TC02: Tạo task thất bại - User không tồn tại")
    void createTask_UserNotFound() {
        // Given
        Long userId = 999L;
        Long projectId = 1L;
        String title = "New Task";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.createTask(userId, projectId, title))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User not found")
                .matches(ex -> ((BusinessException) ex).getErrorCode() == ErrorCode.USER_NOT_FOUND);

        verify(userRepository).findById(userId);
        verify(projectRepository, never()).findById(any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC03: Tạo task thất bại - Project không tồn tại")
    void createTask_ProjectNotFound() {
        // Given
        Long userId = 1L;
        Long projectId = 999L;
        String title = "New Task";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.createTask(userId, projectId, title))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Project not found")
                .matches(ex -> ((BusinessException) ex).getErrorCode() == ErrorCode.PROJECT_NOT_FOUND);

        verify(userRepository).findById(userId);
        verify(projectRepository).findById(projectId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC04: Tạo task thất bại - Title bị trùng trong cùng project")
    void createTask_DuplicateTitle() {
        // Given
        Long userId = 1L;
        Long projectId = 1L;
        String title = "Test Task";

        TaskEntity existingTask = new TaskEntity("Test Task", user, project);
        existingTask.setId(2L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findByProjectId(projectId)).thenReturn(Arrays.asList(existingTask));

        // When & Then
        assertThatThrownBy(() -> taskService.createTask(userId, projectId, title))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Task title already exists in project")
                .matches(ex -> ((BusinessException) ex).getErrorCode() == ErrorCode.TASK_TITLE_EXISTS_IN_PROJECT);

        verify(userRepository).findById(userId);
        verify(projectRepository).findById(projectId);
        verify(taskRepository).findByProjectId(projectId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC05: Tạo task thành công - Title không phân biệt hoa thường")
    void createTask_TitleCaseInsensitive() {
        // Given
        Long userId = 1L;
        Long projectId = 1L;
        String title = "NEW TASK";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findByProjectId(projectId)).thenReturn(Arrays.asList(task));
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> {
            TaskEntity savedTask = invocation.getArgument(0);
            savedTask.setId(2L);
            return savedTask;
        });

        // When
        TaskResponseDTO result = taskService.createTask(userId, projectId, title);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(title);

        verify(taskRepository).findByProjectId(projectId);
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("TC06: Tạo task thành công - Kiểm tra status mặc định là TODO")
    void createTask_DefaultStatusIsTodo() {
        // Given
        Long userId = 1L;
        Long projectId = 1L;
        String title = "Another Task";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findByProjectId(projectId)).thenReturn(Arrays.asList());
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> {
            TaskEntity savedTask = invocation.getArgument(0);
            savedTask.setId(3L);
            return savedTask;
        });

        // When
        TaskResponseDTO result = taskService.createTask(userId, projectId, title);

        // Then
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO.name());

        verify(taskRepository).save(argThat(savedTask ->
                savedTask.getStatus() == TaskStatus.TODO
        ));
    }

    // ===== TEST CASES CHO assignTask (TC07 - TC14) =====

    @Test
    @DisplayName("TC07: Assign task thành công")
    void assignTask_Success() {
        // Given
        Long taskId = 1L;
        Long userId = 3L;

        TaskEntity taskToAssign = new TaskEntity("Task to assign", null, project);
        taskToAssign.setId(2L);
        taskToAssign.setStatus(TaskStatus.TODO);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskToAssign));
        when(userRepository.findById(userId)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TaskResponseDTO result = taskService.assignTask(taskId, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(assignee.getUsername());

        verify(taskRepository).findById(taskId);
        verify(userRepository).findById(userId);
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("TC08: Assign task thất bại - Task không tồn tại")
    void assignTask_TaskNotFound() {
        // Given
        Long taskId = 999L;
        Long userId = 3L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.assignTask(taskId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Task not found")
                .matches(ex -> ((BusinessException) ex).getErrorCode() == ErrorCode.TASK_NOT_FOUND);

        verify(taskRepository).findById(taskId);
        verify(userRepository, never()).findById(any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC09: Assign task thất bại - User không tồn tại")
    void assignTask_UserNotFound() {
        // Given
        Long taskId = 1L;
        Long userId = 999L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.assignTask(taskId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User not found")
                .matches(ex -> ((BusinessException) ex).getErrorCode() == ErrorCode.USER_NOT_FOUND);

        verify(taskRepository).findById(taskId);
        verify(userRepository).findById(userId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC10: Assign task thất bại - Task đã DONE")
    void assignTask_TaskIsDone() {
        // Given
        Long taskId = 1L;
        Long userId = 3L;

        TaskEntity doneTask = new TaskEntity("Done Task", user, project);
        doneTask.setId(3L);
        doneTask.setStatus(TaskStatus.DONE);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(doneTask));

        // When & Then
        assertThatThrownBy(() -> taskService.assignTask(taskId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot modify completed or cancelled task")  // SỬA message
                .matches(ex -> ((BusinessException) ex).getErrorCode() == ErrorCode.TASK_DONE_CANNOT_MODIFY);

        verify(taskRepository).findById(taskId);
        verify(userRepository, never()).findById(any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC11: Assign task thất bại - User không thuộc project của task")
    void assignTask_UserNotInProject() {
        // Given
        Long taskId = 1L;
        Long userId = 2L;

        TaskEntity taskInProject1 = new TaskEntity("Project 1 Task", null, project);
        taskInProject1.setId(4L);
        taskInProject1.setStatus(TaskStatus.TODO);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskInProject1));
        when(userRepository.findById(userId)).thenReturn(Optional.of(anotherUser));

        // When & Then
        assertThatThrownBy(() -> taskService.assignTask(taskId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User does not belong to this project")
                .matches(ex -> ((BusinessException) ex).getErrorCode() == ErrorCode.USER_NOT_IN_PROJECT);

        verify(taskRepository).findById(taskId);
        verify(userRepository).findById(userId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC12: Assign task thất bại - Task đã CANCELLED")
    void assignTask_TaskIsCancelled() {
        // Given
        Long taskId = 1L;
        Long userId = 3L;

        TaskEntity cancelledTask = new TaskEntity("Cancelled Task", user, project);
        cancelledTask.setId(5L);
        cancelledTask.setStatus(TaskStatus.CANCELLED);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(cancelledTask));

        // When & Then
        assertThatThrownBy(() -> taskService.assignTask(taskId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot modify completed or cancelled task")  // SỬA message
                .matches(ex -> ((BusinessException) ex).getErrorCode() == ErrorCode.TASK_DONE_CANNOT_MODIFY);

        verify(taskRepository).findById(taskId);
        verify(userRepository, never()).findById(any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC13: Assign task thành công - User null (unassign task)")
    void assignTask_UnassignTask() {
        // Given
        Long taskId = 1L;
        Long userId = null;

        TaskEntity assignedTask = new TaskEntity("Assigned Task", user, project);
        assignedTask.setId(6L);
        assignedTask.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(assignedTask));
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TaskResponseDTO result = taskService.assignTask(taskId, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isNull();

        verify(taskRepository).findById(taskId);
        verify(userRepository, never()).findById(any());
        verify(taskRepository).save(argThat(savedTask ->
                savedTask.getUser() == null
        ));
    }

    @Test
    @DisplayName("TC14: Assign task thành công - Chuyển từ user này sang user khác")
    void assignTask_ReassignToAnotherUser() {
        // Given
        Long taskId = 1L;
        Long newUserId = 5L;

        TaskEntity assignedTask = new TaskEntity("Assigned Task", currentUser, project);
        assignedTask.setId(7L);
        assignedTask.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(assignedTask));
        when(userRepository.findById(newUserId)).thenReturn(Optional.of(newUser));
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TaskResponseDTO result = taskService.assignTask(taskId, newUserId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(newUser.getUsername());

        verify(taskRepository).findById(taskId);
        verify(userRepository).findById(newUserId);
        verify(taskRepository).save(argThat(savedTask ->
                savedTask.getUser().getUsername().equals(newUser.getUsername())
        ));
    }
    // ===== TEST CASES CHO verify behavior =====

    @Test
    @DisplayName("TC15: Verify createTask gọi đúng repository methods")
    void verify_createTask_CallsCorrectMethods() {
        // Given
        Long userId = 1L;
        Long projectId = 1L;
        String title = "Verify Task";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findByProjectId(projectId)).thenReturn(Arrays.asList());
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        taskService.createTask(userId, projectId, title);

        // Then - Verify exact calls
        verify(userRepository, times(1)).findById(userId);
        verify(projectRepository, times(1)).findById(projectId);
        verify(taskRepository, times(1)).findByProjectId(projectId);
        verify(taskRepository, times(1)).save(any(TaskEntity.class));

        // Verify order of calls
        InOrder inOrder = inOrder(userRepository, projectRepository, taskRepository);
        inOrder.verify(userRepository).findById(userId);
        inOrder.verify(projectRepository).findById(projectId);
        inOrder.verify(taskRepository).findByProjectId(projectId);
        inOrder.verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("TC16: Verify assignTask gọi đúng repository methods khi thành công")
    void verify_assignTask_Success_CallsCorrectMethods() {
        // Given
        Long taskId = 1L;
        Long userId = 3L;

        TaskEntity taskToAssign = new TaskEntity("Task to assign", null, project);
        taskToAssign.setId(2L);
        taskToAssign.setStatus(TaskStatus.TODO);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskToAssign));
        when(userRepository.findById(userId)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        taskService.assignTask(taskId, userId);

        // Then - Verify
        verify(taskRepository, times(1)).findById(taskId);
        verify(userRepository, times(1)).findById(userId);
        verify(taskRepository, times(1)).save(any(TaskEntity.class));

        // Verify không gọi các methods khác
        verify(projectRepository, never()).findById(any());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("TC17: Verify assignTask không gọi save khi task không tồn tại")
    void verify_assignTask_TaskNotFound_NoSaveCall() {
        // Given
        Long taskId = 999L;
        Long userId = 3L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.assignTask(taskId, userId))
                .isInstanceOf(BusinessException.class);

        // Verify
        verify(taskRepository, times(1)).findById(taskId);
        verify(userRepository, never()).findById(any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC18: Verify assignTask không gọi save khi user không tồn tại")
    void verify_assignTask_UserNotFound_NoSaveCall() {
        // Given
        Long taskId = 1L;
        Long userId = 999L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.assignTask(taskId, userId))
                .isInstanceOf(BusinessException.class);

        // Verify
        verify(taskRepository, times(1)).findById(taskId);
        verify(userRepository, times(1)).findById(userId);
        verify(taskRepository, never()).save(any());
    }
    @Test
    @DisplayName("TC19: Tạo task thất bại - Title null")
    void createTask_TitleNull() {
        // Given
        Long userId = 1L;
        Long projectId = 1L;
        String title = null;

        // Không cần mock vì sẽ throw exception trước khi gọi repository
        // when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // When & Then
        assertThatThrownBy(() -> taskService.createTask(userId, projectId, title))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Title cannot be empty");

        // Verify không gọi repository methods
        verify(userRepository, never()).findById(any());
        verify(projectRepository, never()).findById(any());
        verify(taskRepository, never()).findByProjectId(any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC20: Tạo task thất bại - Title empty")
    void createTask_TitleEmpty() {
        // Given
        Long userId = 1L;
        Long projectId = 1L;
        String title = "";

        // Không cần mock
        // when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // When & Then
        assertThatThrownBy(() -> taskService.createTask(userId, projectId, title))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Title cannot be empty");

        // Verify không gọi repository methods
        verify(userRepository, never()).findById(any());
        verify(projectRepository, never()).findById(any());
        verify(taskRepository, never()).findByProjectId(any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC21: Assign task thất bại - Task status null")
    void assignTask_TaskStatusNull() {
        // Given
        Long taskId = 1L;
        Long userId = 3L;

        TaskEntity taskWithNullStatus = new TaskEntity("Task", user, project);
        taskWithNullStatus.setId(8L);
        taskWithNullStatus.setStatus(null);  // Status null

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskWithNullStatus));

        // When & Then
        assertThatThrownBy(() -> taskService.assignTask(taskId, userId))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("TC22: Assign task thất bại - Project của user null")
    void assignTask_UserProjectNull() {
        // Given
        Long taskId = 1L;
        Long userId = 3L;

        UserEntity userWithNoProject = new UserEntity("noproject", "noproject@example.com", "password");
        userWithNoProject.setId(6L);
        userWithNoProject.setProject(null);  // Project null

        TaskEntity taskInProject = new TaskEntity("Task", null, project);
        taskInProject.setId(9L);
        taskInProject.setStatus(TaskStatus.TODO);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskInProject));
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithNoProject));

        // When & Then
        assertThatThrownBy(() -> taskService.assignTask(taskId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User does not belong to this project");
    }

    @Test
    @DisplayName("TC23: Assign task thất bại - Task project null")
    void assignTask_TaskProjectNull() {
        // Given
        Long taskId = 1L;
        Long userId = 3L;

        TaskEntity taskWithNoProject = new TaskEntity("Task", user, null);
        taskWithNoProject.setId(10L);
        taskWithNoProject.setStatus(TaskStatus.TODO);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskWithNoProject));
        // Không cần mock userRepository vì sẽ fail trước

        // When & Then
        assertThatThrownBy(() -> taskService.assignTask(taskId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Task has no project");
    }

    @Test
    @DisplayName("TC24: Assign task thành công - Kiểm tra user được set đúng")
    void assignTask_VerifyUserAssigned() {
        // Given
        Long taskId = 1L;
        Long userId = 3L;

        TaskEntity taskToAssign = new TaskEntity("Task to assign", null, project);
        taskToAssign.setId(11L);
        taskToAssign.setStatus(TaskStatus.TODO);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskToAssign));
        when(userRepository.findById(userId)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> {
            TaskEntity savedTask = invocation.getArgument(0);
            savedTask.setId(11L);
            return savedTask;
        });

        // When
        TaskResponseDTO result = taskService.assignTask(taskId, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(assignee.getUsername());

        // Verify user được set
        verify(taskRepository).save(argThat(savedTask ->
                savedTask.getUser() != null &&
                        savedTask.getUser().getUsername().equals(assignee.getUsername())
        ));
    }

    @Test
    @DisplayName("TC25: Update status thất bại - Task không tồn tại")
    void updateStatus_TaskNotFound() {
        // Given
        Long taskId = 999L;
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.updateStatus(taskId, newStatus))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Task not found");
    }

    @Test
    @DisplayName("TC26: Update status thất bại - Chuyển từ TODO sang DONE")
    void updateStatus_TodoToDone_Invalid() {
        // Given
        Long taskId = 1L;
        TaskStatus newStatus = TaskStatus.DONE;

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // When & Then
        assertThatThrownBy(() -> taskService.updateStatus(taskId, newStatus))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Must go through IN_PROGRESS first");
    }

    @Test
    @DisplayName("TC27: Update status thành công - TODO sang IN_PROGRESS")
    void updateStatus_TodoToInProgress_Success() {
        // Given
        Long taskId = 1L;
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TaskResponseDTO result = taskService.updateStatus(taskId, newStatus);

        // Then
        assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS.name());
    }

    @Test
    @DisplayName("TC28: Update status thành công - IN_PROGRESS sang DONE")
    void updateStatus_InProgressToDone_Success() {
        // Given
        Long taskId = 1L;
        TaskStatus newStatus = TaskStatus.DONE;

        TaskEntity inProgressTask = new TaskEntity("In Progress Task", user, project);
        inProgressTask.setId(12L);
        inProgressTask.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(inProgressTask));
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TaskResponseDTO result = taskService.updateStatus(taskId, newStatus);

        // Then
        assertThat(result.getStatus()).isEqualTo(TaskStatus.DONE.name());
    }

    @Test
    @DisplayName("TC29: Update status thất bại - DONE không thể update")
    void updateStatus_Done_CannotModify() {
        // Given
        Long taskId = 1L;
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;

        TaskEntity doneTask = new TaskEntity("Done Task", user, project);
        doneTask.setId(13L);
        doneTask.setStatus(TaskStatus.DONE);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(doneTask));

        // When & Then
        assertThatThrownBy(() -> taskService.updateStatus(taskId, newStatus))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot modify completed task");
    }

    @Test
    @DisplayName("TC30: Update status thất bại - CANCELLED không thể update")
    void updateStatus_Cancelled_CannotModify() {
        // Given
        Long taskId = 1L;
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;

        TaskEntity cancelledTask = new TaskEntity("Cancelled Task", user, project);
        cancelledTask.setId(14L);
        cancelledTask.setStatus(TaskStatus.CANCELLED);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(cancelledTask));

        // When & Then
        assertThatThrownBy(() -> taskService.updateStatus(taskId, newStatus))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot update cancelled task");
    }
}