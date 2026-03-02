package com.armydev.selfdev.domain.goal;

import com.armydev.selfdev.common.response.ApiResponse;
import com.armydev.selfdev.domain.goal.dto.GoalRequest;
import com.armydev.selfdev.domain.goal.dto.GoalResponse;
import com.armydev.selfdev.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plans/{planId}/goals")
@RequiredArgsConstructor
@Tag(name = "Goals", description = "주간 목표 API")
public class WeeklyGoalController {

    private final WeeklyGoalService goalService;

    @GetMapping
    @Operation(summary = "목표 목록")
    public ResponseEntity<ApiResponse<List<GoalResponse>>> getGoals(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long planId) {
        List<GoalResponse> goals = goalService.getGoals(securityUser.getUserId(), planId);
        return ResponseEntity.ok(ApiResponse.success(goals));
    }

    @PostMapping
    @Operation(summary = "목표 생성")
    public ResponseEntity<ApiResponse<GoalResponse>> createGoal(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long planId,
            @Valid @RequestBody GoalRequest request) {
        GoalResponse goal = goalService.createGoal(securityUser.getUserId(), planId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(goal));
    }

    @PatchMapping("/{goalId}")
    @Operation(summary = "목표 수정")
    public ResponseEntity<ApiResponse<GoalResponse>> updateGoal(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long planId,
            @PathVariable Long goalId,
            @Valid @RequestBody GoalRequest request) {
        GoalResponse goal = goalService.updateGoal(securityUser.getUserId(), planId, goalId, request);
        return ResponseEntity.ok(ApiResponse.success(goal));
    }

    @DeleteMapping("/{goalId}")
    @Operation(summary = "목표 삭제")
    public ResponseEntity<Void> deleteGoal(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long planId,
            @PathVariable Long goalId) {
        goalService.deleteGoal(securityUser.getUserId(), planId, goalId);
        return ResponseEntity.noContent().build();
    }
}
