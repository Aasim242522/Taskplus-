package com.taskmonitor.module.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String obtainAccessToken(String email, String password) throws Exception {
        String registerJson = String.format("""
            {
                "email": "%s",
                "password": "%s",
                "fullName": "Integration Tester",
                "timezone": "UTC"
            }
            """, email, password);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson));

        String loginJson = String.format("""
            {
                "email": "%s",
                "password": "%s"
            }
            """, email, password);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode responseNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return responseNode.path("data").path("accessToken").asText();
    }

    @Test
    @DisplayName("End-to-end task lifecycle: Create, Check-in, Complete with Gamification scoring")
    void testTaskLifecycleAndScoring() throws Exception {
        String uniqueEmail = "lifecycle_" + UUID.randomUUID() + "@example.com";
        String token = obtainAccessToken(uniqueEmail, "Password123!");

        // 1. Create a task
        String createTaskJson = """
            {
                "title": "Build Production API",
                "description": "Integration testing full flow",
                "priority": "HIGH",
                "difficultyLevel": 3,
                "estimatedDurationMinutes": 60
            }
            """;

        MvcResult createResult = mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTaskJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Build Production API"))
                .andReturn();

        JsonNode createdNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String taskId = createdNode.path("data").path("id").asText();
        assertNotNull(taskId);

        // 2. Check in duration
        String checkInJson = """
            {
                "additionalMinutes": 30
            }
            """;

        mockMvc.perform(post("/api/v1/tasks/" + taskId + "/check-in")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkInJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.actualDurationMinutes").value(30))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));

        // 3. Mark task completed -> triggers gamification scoring & streak
        MvcResult completeResult = mockMvc.perform(post("/api/v1/tasks/" + taskId + "/complete")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.currentStreak").value(1))
                .andReturn();

        JsonNode completeNode = objectMapper.readTree(completeResult.getResponse().getContentAsString());
        int pointsAwarded = completeNode.path("data").path("pointsAwarded").asInt();
        int newTotalPoints = completeNode.path("data").path("newTotalPoints").asInt();
        assertTrue(pointsAwarded >= 50, "Points awarded must include base and difficulty multiplier");
        assertTrue(newTotalPoints >= pointsAwarded, "New total points should be at least task points awarded");

        // 4. Verify points summary reflects the completion and unlocked badges
        mockMvc.perform(get("/api/v1/gamification/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentStreak").value(1))
                .andExpect(jsonPath("$.data.currentBalance").value(newTotalPoints));
    }

    @Test
    @DisplayName("Direct PUT status mutation to COMPLETED must be rejected with INVALID_STATUS_TRANSITION")
    void testDirectStatusUpdateToCompletedIsRejected() throws Exception {
        String uniqueEmail = "bypass_" + UUID.randomUUID() + "@example.com";
        String token = obtainAccessToken(uniqueEmail, "Password123!");

        // Create task
        String createTaskJson = """
            {
                "title": "Bypass Attempt Task",
                "priority": "MEDIUM",
                "difficultyLevel": 2
            }
            """;

        MvcResult createResult = mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTaskJson))
                .andExpect(status().isCreated())
                .andReturn();

        String taskId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // Attempt direct status update to COMPLETED via PUT
        String maliciousUpdateJson = """
            {
                "status": "COMPLETED"
            }
            """;

        mockMvc.perform(put("/api/v1/tasks/" + taskId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(maliciousUpdateJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Task completion must be performed via POST")));
    }
}
