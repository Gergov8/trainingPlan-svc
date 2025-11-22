package com.gergov.trainingPlan_svc.plan.service;

import com.gergov.trainingPlan_svc.ai.AiPlanGenerator;
import com.gergov.trainingPlan_svc.plan.model.Plan;
import com.gergov.trainingPlan_svc.plan.repository.PlanRepository;
import com.gergov.trainingPlan_svc.web.dto.CreatePlanRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlanService {
    private final PlanRepository repository;
    private final AiPlanGenerator aiGenerator;

    public PlanService(PlanRepository repository, AiPlanGenerator aiGenerator) {
        this.repository = repository;
        this.aiGenerator = aiGenerator;
    }

    public Plan createPlan(CreatePlanRequest request) {
        String planJson = aiGenerator.generatePlan(request.distanceKm(), request.daysPerWeek(), request.planLevel());
        Plan plan = new Plan(request.userId(), request.distanceKm(), request.daysPerWeek(), request.planLevel(), LocalDate.now(), LocalDate.now(), planJson);
        return repository.save(plan);
    }

    public Plan regeneratePlan(UUID id, CreatePlanRequest req) {
        Plan existing = repository.findById(id).orElseThrow();
        String planJson = aiGenerator.generatePlan(req.distanceKm(), req.daysPerWeek(), req.planLevel());
        existing.setPlanJson(planJson);
        existing.setUpdatedAt(LocalDate.now());
        existing.setDistanceKm(req.distanceKm());
        existing.setDaysPerWeek(req.daysPerWeek());
        return repository.save(existing);
    }

    public void deletePlan(UUID id) {
        repository.deleteById(id);
    }

    public Optional<Plan> getPlan(UUID id) {
        return repository.findById(id);
    }
}

