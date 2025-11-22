package com.gergov.trainingPlan_svc.plan.repository;

import com.gergov.trainingPlan_svc.plan.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {
}

