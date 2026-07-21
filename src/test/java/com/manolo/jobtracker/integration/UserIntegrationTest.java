package com.manolo.jobtracker.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manolo.jobtracker.dto.request.UserRequestDto;
import com.manolo.jobtracker.dto.request.UserRoleUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserIntegrationTest {

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    private JsonNode createUser(String email, String password) throws Exception {
        UserRequestDto dto = new UserRequestDto();
        dto.setEmail(email);
        dto.setPassword(password);

        MvcResult r = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(r.getResponse().getContentAsString());
    }

    private String loginAndGetAccessToken(String email, String password) throws Exception {
        var login = new com.manolo.jobtracker.dto.request.LoginRequestDto(email, password);

        MvcResult r = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(r.getResponse().getContentAsString());
        return body.get("accessToken").asText();
    }

    @Test
    @DisplayName("POST /api/users registers a new user and returns 201")
    void registerUser_success() throws Exception {
        String email = "user1@test.com";
        String password = "Password1!";

        JsonNode created = createUser(email, password);

        assertThat(created.get("email").asText()).isEqualTo(email);
        assertThat(created.get("role").asText()).isEqualTo("USER");
        assertThat(created.get("id").asLong()).isGreaterThan(0);
    }

    @Test
    @DisplayName("GET /api/users is allowed for ADMIN and returns a paged result")
    void getAll_requiresAdmin() throws Exception {
        // ensure a regular user exists
        createUser("user2@test.com", "Password2!");

        String adminToken = loginAndGetAccessToken("admin@test.com", "adminpass");

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/users/{id} allowed for owner")
    void getById_ownerAccess() throws Exception {
        JsonNode created = createUser("owner@test.com", "OwnerPass1!");
        long id = created.get("id").asLong();

        String token = loginAndGetAccessToken("owner@test.com", "OwnerPass1!");

        mockMvc.perform(get("/api/users/" + id)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("owner@test.com"));
    }

    @Test
    @DisplayName("PATCH /api/users/{id}/role updates role when performed by ADMIN")
    void updateRole_adminOnly() throws Exception {
        JsonNode created = createUser("promote@test.com", "Promote1!");
        long id = created.get("id").asLong();

        String adminToken = loginAndGetAccessToken("admin@test.com", "adminpass");

        UserRoleUpdateDto dto = new UserRoleUpdateDto();
        dto.setRole(com.manolo.jobtracker.enums.Role.ADMIN);

        mockMvc.perform(patch("/api/users/" + id + "/role")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /api/users returns 400 for invalid payload")
    void registerValidationError() throws Exception {
        UserRequestDto dto = new UserRequestDto();
        dto.setEmail("bad-email");
        dto.setPassword("pw");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='email')]").exists());
    }
}