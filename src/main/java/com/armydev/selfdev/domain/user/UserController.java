package com.armydev.selfdev.domain.user;

import com.armydev.selfdev.common.response.ApiResponse;
import com.armydev.selfdev.domain.user.dto.UpdateUserRequest;
import com.armydev.selfdev.domain.user.dto.UserResponse;
import com.armydev.selfdev.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
@Tag(name = "Me", description = "내 프로필 API")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "내 프로필 조회")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal SecurityUser securityUser) {
        UserResponse response = userService.getMe(securityUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping
    @Operation(summary = "내 프로필 수정")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateMe(securityUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/streak")
    @Operation(summary = "스트릭 조회")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStreak(
            @AuthenticationPrincipal SecurityUser securityUser) {
        User user = userService.findUser(securityUser.getUserId());
        Map<String, Object> streak = new HashMap<>();
        streak.put("current", user.getCurrentStreak());
        streak.put("max", user.getMaxStreak());
        streak.put("lastCompletedDate", user.getLastCompletedDate());
        return ResponseEntity.ok(ApiResponse.success(streak));
    }
}
