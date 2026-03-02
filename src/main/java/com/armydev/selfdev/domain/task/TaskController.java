package com.armydev.selfdev.domain.task;

import com.armydev.selfdev.common.response.ApiResponse;
import com.armydev.selfdev.domain.task.dto.TaskRequest;
import com.armydev.selfdev.domain.task.dto.TaskResponse;
import com.armydev.selfdev.domain.task.dto.ToggleRequest;
import com.armydev.selfdev.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/plans/{planId}/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "태스크 API")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "태스크 목록")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasks(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long planId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TaskResponse> tasks = taskService.getTasks(securityUser.getUserId(), planId, date);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @PostMapping
    @Operation(summary = "태스크 생성")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long planId,
            @Valid @RequestBody TaskRequest request) {
        TaskResponse task = taskService.createTask(securityUser.getUserId(), planId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(task));
    }

    @PatchMapping("/{taskId}")
    @Operation(summary = "태스크 수정")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long planId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest request) {
        TaskResponse task = taskService.updateTask(securityUser.getUserId(), planId, taskId, request);
        return ResponseEntity.ok(ApiResponse.success(task));
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "태스크 삭제")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long planId,
            @PathVariable Long taskId) {
        taskService.deleteTask(securityUser.getUserId(), planId, taskId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/toggle")
    @Operation(summary = "태스크 완료 토글")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleTask(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long planId,
            @PathVariable Long taskId,
            @Valid @RequestBody ToggleRequest request) {
        Map<String, Object> result = taskService.toggleTask(securityUser.getUserId(), planId, taskId, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
