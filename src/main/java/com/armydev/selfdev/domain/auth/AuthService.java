package com.armydev.selfdev.domain.auth;

import com.armydev.selfdev.common.exception.BusinessException;
import com.armydev.selfdev.common.exception.ErrorCode;
import com.armydev.selfdev.config.JwtConfig;
import com.armydev.selfdev.domain.auth.dto.LoginRequest;
import com.armydev.selfdev.domain.auth.dto.RefreshRequest;
import com.armydev.selfdev.domain.auth.dto.RegisterRequest;
import com.armydev.selfdev.domain.auth.dto.TokenResponse;
import com.armydev.selfdev.domain.user.User;
import com.armydev.selfdev.domain.user.UserRepository;
import com.armydev.selfdev.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .nickname(request.getNickname())
            .dischargeDate(request.getDischargeDate())
            .dailyMinutes(request.getDailyMinutes())
            .goalPriorities(request.getGoalPriorities())
            .build();

        user = userRepository.save(user);
        return issueTokens(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        return issueTokens(user);
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        String tokenHash = hashToken(request.getRefreshToken());

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID));

        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return issueTokens(refreshToken.getUser());
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String rawRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        RefreshToken rt = RefreshToken.builder()
            .user(user)
            .tokenHash(hashToken(rawRefreshToken))
            .expiresAt(LocalDateTime.now().plusSeconds(jwtConfig.getRefreshExpiry() / 1000))
            .build();
        refreshTokenRepository.save(rt);

        return new TokenResponse(accessToken, rawRefreshToken, jwtConfig.getAccessExpiry() / 1000);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
