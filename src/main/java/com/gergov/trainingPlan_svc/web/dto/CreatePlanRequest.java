package com.gergov.trainingPlan_svc.web.dto;

import com.gergov.trainingPlan_svc.plan.model.PlanLevel;

import java.util.UUID;

public record CreatePlanRequest(
        UUID userId,
        Double distanceKm,
        PlanLevel planLevel,
        int daysPerWeek
) {}
