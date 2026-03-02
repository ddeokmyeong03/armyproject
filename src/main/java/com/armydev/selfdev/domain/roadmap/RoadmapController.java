package com.armydev.selfdev.domain.roadmap;

import com.armydev.selfdev.common.response.ApiResponse;
import com.armydev.selfdev.domain.roadmap.dto.ApplyRoadmapRequest;
import com.armydev.selfdev.domain.roadmap.dto.RoadmapResponse;
import com.armydev.selfdev.domain.roadmap.dto.UserRoadmapResponse;
import com.armydev.selfdev.domain.user.GoalCategory;
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
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Roadmaps", description = "로드맵 API")
public class RoadmapController {

    private final RoadmapService roadmapService;

    @GetMapping("/api/v1/roadmaps")
    @Operation(summary = "공식 로드맵 목록")
    public ResponseEntity<ApiResponse<List<RoadmapResponse>>> getRoadmaps(
            @RequestParam(required = false) GoalCategory category) {
        List<RoadmapResponse> roadmaps = roadmapService.getRoadmaps(category);
        return ResponseEntity.ok(ApiResponse.success(roadmaps));
    }

    @GetMapping("/api/v1/roadmaps/{roadmapId}")
    @Operation(summary = "로드맵 상세")
    public ResponseEntity<ApiResponse<RoadmapResponse>> getRoadmap(@PathVariable Long roadmapId) {
        RoadmapResponse roadmap = roadmapService.getRoadmap(roadmapId);
        return ResponseEntity.ok(ApiResponse.success(roadmap));
    }

    @GetMapping("/api/v1/me/roadmaps")
    @Operation(summary = "내 로드맵 목록")
    public ResponseEntity<ApiResponse<List<UserRoadmapResponse>>> getMyRoadmaps(
            @AuthenticationPrincipal SecurityUser securityUser) {
        List<UserRoadmapResponse> roadmaps = roadmapService.getMyRoadmaps(securityUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(roadmaps));
    }

    @PostMapping("/api/v1/me/roadmaps")
    @Operation(summary = "로드맵 적용")
    public ResponseEntity<ApiResponse<UserRoadmapResponse>> applyRoadmap(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Valid @RequestBody ApplyRoadmapRequest request) {
        UserRoadmapResponse response = roadmapService.applyRoadmap(securityUser.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PatchMapping("/api/v1/me/roadmaps/{userRoadmapId}")
    @Operation(summary = "로드맵 상태 변경")
    public ResponseEntity<ApiResponse<UserRoadmapResponse>> updateUserRoadmap(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long userRoadmapId,
            @RequestBody Map<String, String> body) {
        UserRoadmapStatus status = UserRoadmapStatus.valueOf(body.get("status"));
        UserRoadmapResponse response = roadmapService.updateUserRoadmap(
            securityUser.getUserId(), userRoadmapId, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
