package com.armydev.selfdev.domain.plan.dto;

import com.armydev.selfdev.domain.plan.WeeklyPlan;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class PlanResponse {
    private final Long id;
    private final LocalDate weekStart;
    private final LocalDate weekEnd;
    private final String memo;
    private final LocalDateTime createdAt;

    public PlanResponse(WeeklyPlan plan) {
        this.id = plan.getId();
        this.weekStart = plan.getWeekStart();
        this.weekEnd = plan.getWeekStart().plusDays(6);
        this.memo = plan.getMemo();
        this.createdAt = plan.getCreatedAt();
    }
}
