package com.manolo.jobtracker.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manolo.jobtracker.dto.request.TagRequestDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TagIntegrationTest {

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
    @DisplayName("POST /api/tags creates tag when performed by ADMIN and normalizes name")
    void createTag_adminOnly() throws Exception {
        String adminToken = loginAndGetAccessToken("admin@test.com", "adminpass");

        TagRequestDto dto = new TagRequestDto();
        dto.setName("Java");

        MvcResult r = mockMvc.perform(post("/api/tags")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("java"))
                .andReturn();

        JsonNode body = objectMapper.readTree(r.getResponse().getContentAsString());
        assertThat(body.get("id").asLong()).isGreaterThan(0);
    }

    @Test
    @DisplayName("POST /api/tags returns 403 for non-admin users")
    void createTag_forbiddenForUser() throws Exception {
        // register a normal user
        var user = new com.manolo.jobtracker.dto.request.UserRequestDto("taguser@test.com", "TagPass1!");
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());

        String token = loginAndGetAccessToken("taguser@test.com", "TagPass1!");

        TagRequestDto dto = new TagRequestDto();
        dto.setName("Spring");

        mockMvc.perform(post("/api/tags")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/tags returns paged content when authenticated")
    void getAllTags_authenticated() throws Exception {
        // ensure at least one tag exists
        String adminToken = loginAndGetAccessToken("admin@test.com", "adminpass");
        TagRequestDto dto = new TagRequestDto();
        dto.setName("Kotlin");
        mockMvc.perform(post("/api/tags")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tags")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}