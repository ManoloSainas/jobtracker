package com.manolo.jobtracker.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manolo.jobtracker.dto.request.JobApplicationRequestDto;
import com.manolo.jobtracker.dto.request.JobApplicationPatchDto;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class JobApplicationIntegrationTest {

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

    private long createTagAsAdmin(String name) throws Exception {
        String adminToken = loginAndGetAccessToken("admin@test.com", "adminpass");
        var dto = new com.manolo.jobtracker.dto.request.TagRequestDto(name);

        MvcResult r = mockMvc.perform(post("/api/tags")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(r.getResponse().getContentAsString());
        return body.get("id").asLong();
    }

    private JsonNode createUserAndLogin(String email, String password) throws Exception {
        var userDto = new com.manolo.jobtracker.dto.request.UserRequestDto(email, password);
        MvcResult r = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = objectMapper.readTree(r.getResponse().getContentAsString());
        return created;
    }

    @Test
    @DisplayName("POST /api/applications create and retrieve application as owner")
    void createAndGetApplication() throws Exception {
        // create tag
        long tagId = createTagAsAdmin("aws");

        // create user and login
        JsonNode user = createUserAndLogin("appuser@test.com", "AppPass1!");
        String token = loginAndGetAccessToken("appuser@test.com", "AppPass1!");

        JobApplicationRequestDto req = new JobApplicationRequestDto();
        req.setStatus(com.manolo.jobtracker.enums.Status.APPLIED);
        req.setCompany("Acme");
        req.setPosition("Engineer");
        req.setTagsIds(List.of(tagId));

        MvcResult r = mockMvc.perform(post("/api/applications")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company").value("Acme"))
                .andReturn();

        JsonNode created = objectMapper.readTree(r.getResponse().getContentAsString());
        long appId = created.get("id").asLong();

        // owner can get by id
        mockMvc.perform(get("/api/applications/" + appId)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appId));
    }

    @Test
    @DisplayName("PATCH /api/applications/{id} allows owner to update status and tags")
    void patchUpdatesStatus() throws Exception {
        long tagId = createTagAsAdmin("docker");
        JsonNode user = createUserAndLogin("patchuser@test.com", "PatchPass1!");
        String token = loginAndGetAccessToken("patchuser@test.com", "PatchPass1!");

        JobApplicationRequestDto req = new JobApplicationRequestDto();
        req.setStatus(com.manolo.jobtracker.enums.Status.APPLIED);
        req.setCompany("BetaCorp");
        req.setPosition("DevOps");
        req.setTagsIds(List.of(tagId));

        MvcResult cr = mockMvc.perform(post("/api/applications")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        long appId = objectMapper.readTree(cr.getResponse().getContentAsString()).get("id").asLong();

        JobApplicationPatchDto patch = new JobApplicationPatchDto();
        patch.setStatus(com.manolo.jobtracker.enums.Status.INTERVIEW);

        mockMvc.perform(patch("/api/applications/" + appId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INTERVIEW"));
    }

    @Test
    @DisplayName("DELETE /api/applications/{id} allows owner to delete")
    void deleteApplication() throws Exception {
        long tagId = createTagAsAdmin("gcp");
        createUserAndLogin("deluser@test.com", "DelPass1!");
        String token = loginAndGetAccessToken("deluser@test.com", "DelPass1!");

        JobApplicationRequestDto req = new JobApplicationRequestDto();
        req.setStatus(com.manolo.jobtracker.enums.Status.APPLIED);
        req.setCompany("DeleteCorp");
        req.setPosition("Contractor");
        req.setTagsIds(List.of(tagId));

        MvcResult cr = mockMvc.perform(post("/api/applications")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        long appId = objectMapper.readTree(cr.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/applications/" + appId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}