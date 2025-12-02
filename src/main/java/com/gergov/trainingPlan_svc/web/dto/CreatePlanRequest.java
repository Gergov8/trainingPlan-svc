package com.gergov.trainingPlan_svc.web.dto;

import java.util.UUID;

public record CreatePlanRequest(
        UUID userId,
        Double distanceKm,
        String planLevel,
        int daysPerWeek
) {}