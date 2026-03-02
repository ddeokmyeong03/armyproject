package com.armydev.selfdev.domain.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private static final String REGISTER_URL = "/api/v1/auth/register";

    @Test
    @DisplayName("회원가입 성공 시 201 응답과 토큰 반환")
    void 회원가입_성공() throws Exception {
        var request = Map.of(
            "email", "test@army.mil",
            "password", "Soldier1!",
            "nickname", "김병사",
            "dischargeDate", "2027-12-31",
            "dailyMinutes", 60,
            "goalPriorities", new String[]{"CERT", "ENGLISH"}
        );

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
            .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("중복 이메일 회원가입 시 409 응답")
    void 중복이메일_409() throws Exception {
        var request = Map.of(
            "email", "dup@army.mil",
            "password", "Soldier1!",
            "nickname", "중복유저",
            "dischargeDate", "2027-12-31",
            "dailyMinutes", 60,
            "goalPriorities", new String[]{"CERT"}
        );

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error.code").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    @DisplayName("잘못된 비밀번호 형식으로 회원가입 시 400 응답")
    void 잘못된비밀번호_400() throws Exception {
        var request = Map.of(
            "email", "weak@army.mil",
            "password", "weak", // too short, no special char
            "nickname", "유저",
            "dischargeDate", "2027-12-31",
            "dailyMinutes", 60,
            "goalPriorities", new String[]{"CERT"}
        );

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }
}
