    package com.gergov.trainingPlan_svc.plan.service;

    import com.gergov.trainingPlan_svc.ai.AiPlanGenerator;
    import com.gergov.trainingPlan_svc.plan.model.Plan;
    import com.gergov.trainingPlan_svc.plan.model.PlanLevel;
    import com.gergov.trainingPlan_svc.plan.repository.PlanRepository;
    import com.gergov.trainingPlan_svc.web.dto.CreatePlanRequest;
    import com.gergov.trainingPlan_svc.web.dto.CreatePlanResponse;
    import org.springframework.ai.retry.NonTransientAiException;
    import org.springframework.stereotype.Service;

    import java.time.LocalDate;
    import java.util.List;
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


        public CreatePlanResponse createPlan(CreatePlanRequest request) {
            String planJson;
            try {
                planJson = aiGenerator.generatePlan(
                        request.distanceKm(),
                        request.daysPerWeek(),
                        PlanLevel.valueOf(request.planLevel())
                );
            } catch (NonTransientAiException ex) {
                throw new RuntimeException("AI provider error while generating plan: " + ex.getMessage(), ex);
            } catch (Exception ex) {
                throw new RuntimeException("Unexpected error while generating plan: " + ex.getMessage(), ex);
            }

            Plan plan = Plan.builder()
                    .userId(request.userId())
                    .distanceKm(request.distanceKm())
                    .daysPerWeek(request.daysPerWeek())
                    .planLevel(PlanLevel.valueOf(request.planLevel()))
                    .createdAt(LocalDate.now())
                    .updatedAt(LocalDate.now())
                    .planJson(planJson)
                    .build();

            Plan saved = repository.save(plan);

            return new CreatePlanResponse(
                    saved.getId(),
                    plan.getUserId(),
                    "Training plan successfully created!",
                    saved.getPlanJson()
            );
        }

    public void deletePlan(UUID id) {
        repository.deleteById(id);
    }

    public Optional<Plan> getPlan(UUID id) {
        return repository.findById(id);
    }

        public List<Plan> getPlansByUser(UUID userId) {
            return repository.findByUserId(userId);
        }

        public Optional<Plan> getUserPlan(UUID planId, UUID userId) {
            return repository.findByIdAndUserId(planId, userId);
        }

        public long getUserPlanCount(UUID userId) {
            return repository.countByUserId(userId);
        }

        public void deleteUserPlans(UUID userId) {
            repository.deleteByUserId(userId);
        }
}

