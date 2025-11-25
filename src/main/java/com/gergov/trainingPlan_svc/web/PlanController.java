package com.gergov.trainingPlan_svc.web;

import com.gergov.trainingPlan_svc.plan.model.Plan;
import com.gergov.trainingPlan_svc.plan.service.PlanService;
import com.gergov.trainingPlan_svc.web.dto.CreatePlanRequest;
import com.gergov.trainingPlan_svc.web.dto.CreatePlanResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/plans")
public class PlanController {

    private final PlanService planService;

    @Autowired
    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping
    public ResponseEntity<CreatePlanResponse> createPlan(@RequestBody CreatePlanRequest request) {
        CreatePlanResponse response = planService.createPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plan> getPlan(@PathVariable UUID id) {
        Plan plan = planService.getPlan(id).orElse(null);
        if (plan == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(plan);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }

    // Get all plans for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Plan>> getUserPlans(@PathVariable UUID userId) {
        List<Plan> plans = planService.getPlansByUser(userId);
        return ResponseEntity.ok(plans);
    }

    // Get specific user's plan
    @GetMapping("/user/{userId}/plan/{planId}")
    public ResponseEntity<Plan> getUserPlan(
            @PathVariable UUID userId,
            @PathVariable UUID planId) {
        Optional<Plan> plan = planService.getUserPlan(planId, userId);
        return plan.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get user's plan count
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getUserPlanCount(@PathVariable UUID userId) {
        long count = planService.getUserPlanCount(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/say-hello")
    public String hello() {
        return "Hello from Training Plan Microservice!";
    }
}

