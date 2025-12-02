package com.gergov.trainingPlan_svc;

import com.gergov.trainingPlan_svc.ai.AiPlanGenerator;
import com.gergov.trainingPlan_svc.plan.model.Plan;
import com.gergov.trainingPlan_svc.plan.model.PlanLevel;
import com.gergov.trainingPlan_svc.plan.repository.PlanRepository;
import com.gergov.trainingPlan_svc.plan.service.PlanService;
import com.gergov.trainingPlan_svc.web.dto.CreatePlanRequest;
import com.gergov.trainingPlan_svc.web.dto.CreatePlanResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.ai.retry.NonTransientAiException;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanServiceUTest {

    @Mock
    private PlanRepository repository;

    @Mock
    private AiPlanGenerator aiGenerator;

    @InjectMocks
    private PlanService planService;

    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
    }

    @Test
    void createPlan_ShouldReturnCreatedPlan() throws Exception {
        CreatePlanRequest request = new CreatePlanRequest(
                userId,
                10.0,
                String.valueOf(PlanLevel.ELITE),
                7
        );

        String fakePlanJson = "{\"day1\":\"run\"}";
        when(aiGenerator.generatePlan(10.0, 7, PlanLevel.ELITE)).thenReturn(fakePlanJson);

        Plan savedPlan = Plan.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .distanceKm(10.0)
                .daysPerWeek(7)
                .planLevel(PlanLevel.ELITE)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .planJson(fakePlanJson)
                .build();

        when(repository.save(any(Plan.class))).thenReturn(savedPlan);

        CreatePlanResponse response = planService.createPlan(request);

        assertNotNull(response);
        assertEquals(savedPlan.getId(), response.planId());
        assertEquals(userId, response.userId());
        assertEquals(fakePlanJson, response.planJson());
        assertEquals("Training plan successfully created!", response.summary());

        verify(aiGenerator).generatePlan(10.0, 7, PlanLevel.ELITE);
        verify(repository).save(any(Plan.class));
    }

    @Test
    void createPlan_ShouldThrow_WhenAiFails() throws Exception {
        CreatePlanRequest request = new CreatePlanRequest(
                userId,
                10.0,
                String.valueOf(PlanLevel.ELITE),
                7
        );

        when(aiGenerator.generatePlan(anyDouble(), anyInt(), any()))
                .thenThrow(new NonTransientAiException("AI down"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planService.createPlan(request));

        assertTrue(ex.getMessage().contains("AI provider error"));
        verify(repository, never()).save(any());
    }

    @Test
    void deletePlan_ShouldCallRepository() {
        UUID planId = UUID.randomUUID();

        planService.deletePlan(planId);

        verify(repository).deleteById(planId);
    }

    @Test
    void getPlan_ShouldReturnPlanIfExists() {
        UUID planId = UUID.randomUUID();
        Plan plan = new Plan();
        plan.setId(planId);

        when(repository.findById(planId)).thenReturn(Optional.of(plan));

        Optional<Plan> result = planService.getPlan(planId);

        assertTrue(result.isPresent());
        assertEquals(plan, result.get());
    }

    @Test
    void getPlan_ShouldReturnEmptyIfNotFound() {
        UUID planId = UUID.randomUUID();

        when(repository.findById(planId)).thenReturn(Optional.empty());

        Optional<Plan> result = planService.getPlan(planId);

        assertTrue(result.isEmpty());
    }
}

