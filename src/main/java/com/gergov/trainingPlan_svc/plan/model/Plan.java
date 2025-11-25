package com.gergov.trainingPlan_svc.plan.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Reference to monolith user
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    private Double distanceKm;

    private int daysPerWeek;

    @Enumerated(EnumType.STRING)
    private PlanLevel planLevel;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    @Lob
    private String planJson;
}


