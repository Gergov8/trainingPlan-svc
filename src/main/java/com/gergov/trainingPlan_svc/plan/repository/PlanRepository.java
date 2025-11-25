package com.gergov.trainingPlan_svc.plan.repository;

import com.gergov.trainingPlan_svc.plan.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {

    // Find all plans for a specific user
    List<Plan> findByUserId(UUID userId);

    // Find a specific plan for a specific user
    Optional<Plan> findByIdAndUserId(UUID id, UUID userId);

    // Count plans for a user
    long countByUserId(UUID userId);

    // Delete all plans for a user
    void deleteByUserId(UUID userId);
}

