package com.armydev.selfdev.domain.auth;

import com.armydev.selfdev.common.response.ApiResponse;
import com.armydev.selfdev.domain.auth.dto.LoginRequest;
import com.armydev.selfdev.domain.auth.dto.RefreshRequest;
import com.armydev.selfdev.domain.auth.dto.RegisterRequest;
import com.armydev.selfdev.domain.auth.dto.TokenResponse;
import com.armydev.selfdev.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "회원가입")
    public ResponseEntity<ApiResponse<TokenResponse>> register(@Valid @RequestBody RegisterRequest request) {
        TokenResponse token = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(token));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(token));
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        TokenResponse token = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success(token));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal SecurityUser securityUser) {
        authService.logout(securityUser.getUserId());
        return ResponseEntity.noContent().build();
    }
}
