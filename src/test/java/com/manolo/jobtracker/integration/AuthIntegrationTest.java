package com.manolo.jobtracker.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manolo.jobtracker.dto.request.LoginRequestDto;
import com.manolo.jobtracker.dto.request.RefreshTokenRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthIntegrationTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    @DisplayName("POST /api/auth/login returns access and refresh tokens for seeded admin")
    void login_returns_tokens() throws Exception {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("admin@test.com");
        dto.setPassword("adminpass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/auth/refresh rotates token and returns new tokens")
    void refresh_rotates_and_returns() throws Exception {
        // login first
        LoginRequestDto login = new LoginRequestDto();
        login.setEmail("admin@test.com");
        login.setPassword("adminpass");

        var mvcResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        String refreshToken = objectMapper.readTree(content).get("refreshToken").asText();

        RefreshTokenRequestDto refreshDto = new RefreshTokenRequestDto();
        refreshDto.setRefreshToken(refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/auth/logout revokes token and returns message")
    void logout_revokes_token() throws Exception {
        // login first
        LoginRequestDto login = new LoginRequestDto();
        login.setEmail("admin@test.com");
        login.setPassword("adminpass");

        var mvcResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        String refreshToken = objectMapper.readTree(content).get("refreshToken").asText();

        RefreshTokenRequestDto refreshDto = new RefreshTokenRequestDto();
        refreshDto.setRefreshToken(refreshToken);

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout effettuato correttamente"));
    }
}
