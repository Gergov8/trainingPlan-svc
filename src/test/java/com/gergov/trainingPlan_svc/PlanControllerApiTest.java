package com.gergov.trainingPlan_svc;

import com.gergov.trainingPlan_svc.plan.model.Plan;
import com.gergov.trainingPlan_svc.plan.model.PlanLevel;
import com.gergov.trainingPlan_svc.plan.service.PlanService;
import com.gergov.trainingPlan_svc.web.PlanController;
import com.gergov.trainingPlan_svc.web.dto.CreatePlanRequest;
import com.gergov.trainingPlan_svc.web.dto.CreatePlanResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PlanController.class)
class PlanControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlanService planService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPlan_ShouldReturnCreatedPlan() throws Exception {
        UUID userId = UUID.randomUUID();
        CreatePlanRequest request = new CreatePlanRequest(userId, 10.0, String.valueOf(PlanLevel.ELITE), 7);
        CreatePlanResponse response = new CreatePlanResponse(UUID.randomUUID(), userId, "Training plan successfully created!", "{\"day1\":\"run\"}");

        when(planService.createPlan(any(CreatePlanRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.planId").value(response.planId().toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.planJson").value("{\"day1\":\"run\"}"))
                .andExpect(jsonPath("$.summary").value("Training plan successfully created!"));

        verify(planService, times(1)).createPlan(any(CreatePlanRequest.class));
    }

    @Test
    void getPlan_ShouldReturnPlan_WhenFound() throws Exception {
        UUID planId = UUID.randomUUID();
        Plan plan = Plan.builder().id(planId).distanceKm(10.0).daysPerWeek(7).planLevel(PlanLevel.ELITE).build();

        when(planService.getPlan(planId)).thenReturn(Optional.of(plan));

        mockMvc.perform(get("/api/v1/plans/" + planId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(planId.toString()))
                .andExpect(jsonPath("$.distanceKm").value(10.0))
                .andExpect(jsonPath("$.daysPerWeek").value(7))
                .andExpect(jsonPath("$.planLevel").value("ELITE"));
    }

    @Test
    void getPlan_ShouldReturnNotFound_WhenPlanDoesNotExist() throws Exception {
        UUID planId = UUID.randomUUID();
        when(planService.getPlan(planId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/plans/" + planId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePlan_ShouldReturnNoContent() throws Exception {
        UUID planId = UUID.randomUUID();
        doNothing().when(planService).deletePlan(planId);

        mockMvc.perform(delete("/api/v1/plans/" + planId))
                .andExpect(status().isNoContent());

        verify(planService, times(1)).deletePlan(planId);
    }

    @Test
    void getUserPlans_ShouldReturnListOfPlans() throws Exception {
        UUID userId = UUID.randomUUID();
        Plan plan = Plan.builder().id(UUID.randomUUID()).userId(userId).distanceKm(5.0).daysPerWeek(3).planLevel(PlanLevel.COMPETITIVE).build();
        when(planService.getPlansByUser(userId)).thenReturn(List.of(plan));

        mockMvc.perform(get("/api/v1/plans/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(plan.getId().toString()))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));
    }

    @Test
    void getUserPlan_ShouldReturnPlan_WhenFound() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();
        Plan plan = Plan.builder().id(planId).userId(userId).distanceKm(8.0).daysPerWeek(5).planLevel(PlanLevel.COMPETITIVE).build();

        when(planService.getUserPlan(planId, userId)).thenReturn(Optional.of(plan));

        mockMvc.perform(get("/api/v1/plans/user/" + userId + "/plan/" + planId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(planId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    @Test
    void getUserPlan_ShouldReturnNotFound_WhenNotExist() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();

        when(planService.getUserPlan(planId, userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/plans/user/" + userId + "/plan/" + planId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserPlanCount_ShouldReturnCount() throws Exception {
        UUID userId = UUID.randomUUID();
        when(planService.getUserPlanCount(userId)).thenReturn(5L);

        mockMvc.perform(get("/api/v1/plans/user/" + userId + "/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void hello_ShouldReturnHelloString() throws Exception {
        mockMvc.perform(get("/api/v1/plans/say-hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from Training Plan Microservice!"));
    }
}
