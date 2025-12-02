package com.gergov.trainingPlan_svc;

import com.gergov.trainingPlan_svc.plan.model.Plan;
import com.gergov.trainingPlan_svc.plan.model.PlanLevel;
import com.gergov.trainingPlan_svc.plan.repository.PlanRepository;
import com.gergov.trainingPlan_svc.plan.service.PlanService;
import com.gergov.trainingPlan_svc.web.dto.CreatePlanRequest;
import com.gergov.trainingPlan_svc.web.dto.CreatePlanResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class CreatePlanITest {

    @Autowired
    private PlanService planService;

    @Autowired
    private PlanRepository planRepository;

    @Test
    void createPlan_ShouldPersistPlanAndReturnResponse() {
        UUID userId = UUID.randomUUID();

        CreatePlanRequest request = new CreatePlanRequest(
                userId,
                10.0,
                String.valueOf(PlanLevel.ELITE),
                5
        );

        // Create plan
        CreatePlanResponse response = planService.createPlan(request);

        // Verify response
        assertNotNull(response);
        assertEquals(userId, response.userId());
        assertNotNull(response.planId());
        assertEquals("Training plan successfully created!", response.summary());
        assertNotNull(response.planJson());

        // Verify that plan exists in repository
        Optional<Plan> planFromDb = planRepository.findById(response.planId());
        assertTrue(planFromDb.isPresent());
        Plan plan = planFromDb.get();
        assertEquals(userId, plan.getUserId());
        assertEquals(10.0, plan.getDistanceKm());
        assertEquals(5, plan.getDaysPerWeek());
        assertEquals(PlanLevel.ELITE, plan.getPlanLevel());
        assertEquals(response.planJson(), plan.getPlanJson());
    }

    @Test
    void getPlansByUser_ShouldReturnListOfPlans() {
        UUID userId = UUID.randomUUID();

        // Create multiple plans
        planService.createPlan(new CreatePlanRequest(userId, 5.0, "RECREATIONAL", 4));
        planService.createPlan(new CreatePlanRequest(userId, 8.0, "COMPETITIVE", 6));

        List<Plan> plans = planService.getPlansByUser(userId);

        assertEquals(2, plans.size());
        assertTrue(plans.stream().anyMatch(p -> p.getDistanceKm() == 5.0));
        assertTrue(plans.stream().anyMatch(p -> p.getDistanceKm() == 8.0));
    }

    @Test
    void deletePlan_ShouldRemovePlanFromRepository() {
        UUID userId = UUID.randomUUID();

        CreatePlanResponse response = planService.createPlan(new CreatePlanRequest(userId, 10.0, "ELITE", 5));

        UUID planId = response.planId();

        assertTrue(planRepository.findById(planId).isPresent());

        planService.deletePlan(planId);

        assertFalse(planRepository.findById(planId).isPresent());
    }

    @Test
    void getUserPlanCount_ShouldReturnCorrectCount() {
        UUID userId = UUID.randomUUID();

        planService.createPlan(new CreatePlanRequest(userId, 5.0, "ELITE", 6));
        planService.createPlan(new CreatePlanRequest(userId, 8.0, "COMPETITIVE", 4));

        long count = planService.getUserPlanCount(userId);

        assertEquals(2, count);
    }
}

