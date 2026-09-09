package com.taskmonitor.module.task.controller;

import com.taskmonitor.common.response.ApiResponse;
import com.taskmonitor.common.response.PageResponse;
import com.taskmonitor.module.task.domain.Priority;
import com.taskmonitor.module.task.domain.TaskStatus;
import com.taskmonitor.module.task.dto.*;
import com.taskmonitor.module.task.service.TaskService;
import com.taskmonitor.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Task management, filtering, check-ins, and completion")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateTaskRequest request) {
        TaskResponse response = taskService.createTask(principal.getId(), request);
        return new ResponseEntity<>(ApiResponse.ok(response, "Task created successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get filtered and paginated tasks")
    public ResponseEntity<ApiResponse<PageResponse<TaskResponse>>> getTasks(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Priority priority,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var page = taskService.getTasks(principal.getId(), status, categoryId, priority, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(page)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single task by ID")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        TaskResponse response = taskService.getTaskById(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task details")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskResponse response = taskService.updateTask(principal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Task updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        taskService.deleteTask(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Task deleted successfully"));
    }

    @PostMapping("/{id}/check-in")
    @Operation(summary = "Log actual duration or progress on a task")
    public ResponseEntity<ApiResponse<TaskResponse>> checkIn(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody CheckInRequest request) {
        TaskResponse response = taskService.checkIn(principal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Check-in logged successfully"));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Mark task completed and calculate gamification points/streak")
    public ResponseEntity<ApiResponse<TaskCompletionResponse>> completeTask(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        TaskCompletionResponse response = taskService.completeTask(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(response, "Task completed successfully!"));
    }
}
