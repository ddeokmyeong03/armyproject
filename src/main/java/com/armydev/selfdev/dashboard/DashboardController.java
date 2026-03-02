package com.armydev.selfdev.dashboard;

import com.armydev.selfdev.common.response.ApiResponse;
import com.armydev.selfdev.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "대시보드 API")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "대시보드 조회 (D-day, 스트릭, 주간완료율, 오늘 태스크, 주간목표, 최근 기록)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard(
            @AuthenticationPrincipal SecurityUser securityUser) {
        Map<String, Object> dashboard = dashboardService.getDashboard(securityUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
}
