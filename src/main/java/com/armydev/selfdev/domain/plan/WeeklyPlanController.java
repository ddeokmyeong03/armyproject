package com.armydev.selfdev.domain.plan;

import com.armydev.selfdev.common.response.ApiResponse;
import com.armydev.selfdev.domain.plan.dto.PlanRequest;
import com.armydev.selfdev.domain.plan.dto.PlanResponse;
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
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
@Tag(name = "Plans", description = "주간 플랜 API")
public class WeeklyPlanController {

    private final WeeklyPlanService planService;

    @GetMapping
    @Operation(summary = "주간 플랜 목록")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getPlans(
            @AuthenticationPrincipal SecurityUser securityUser) {
        List<PlanResponse> plans = planService.getPlans(securityUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    @PostMapping
    @Operation(summary = "주간 플랜 생성")
    public ResponseEntity<ApiResponse<PlanResponse>> createPlan(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Valid @RequestBody PlanRequest request) {
        PlanResponse plan = planService.createPlan(securityUser.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(plan));
    }

    @GetMapping("/{planId}")
    @Operation(summary = "주간 플랜 상세")
    public ResponseEntity<ApiResponse<PlanResponse>> getPlan(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long planId) {
        PlanResponse plan = planService.getPlan(securityUser.getUserId(), planId);
        return ResponseEntity.ok(ApiResponse.success(plan));
    }

    @PatchMapping("/{planId}")
    @Operation(summary = "주간 플랜 수정")
    public ResponseEntity<ApiResponse<PlanResponse>> updatePlan(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long planId,
            @RequestBody PlanRequest request) {
        PlanResponse plan = planService.updatePlan(securityUser.getUserId(), planId, request);
        return ResponseEntity.ok(ApiResponse.success(plan));
    }

    @DeleteMapping("/{planId}")
    @Operation(summary = "주간 플랜 삭제")
    public ResponseEntity<Void> deletePlan(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long planId) {
        planService.deletePlan(securityUser.getUserId(), planId);
        return ResponseEntity.noContent().build();
    }
}
