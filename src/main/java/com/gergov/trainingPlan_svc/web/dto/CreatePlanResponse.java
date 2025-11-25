package com.gergov.trainingPlan_svc.web.dto;

import java.util.UUID;

public record CreatePlanResponse(
        UUID planId,
        UUID userId,
        String summary,
        String planJson) {}
