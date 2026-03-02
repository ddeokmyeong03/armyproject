package com.armydev.selfdev.domain.goal.dto;

import com.armydev.selfdev.domain.goal.WeeklyGoal;
import com.armydev.selfdev.domain.user.GoalCategory;
import lombok.Getter;

@Getter
public class GoalResponse {
    private final Long id;
    private final Long planId;
    private final String title;
    private final GoalCategory category;
    private final int targetCount;
    private final int doneCount;
    private final int sortOrder;

    public GoalResponse(WeeklyGoal goal) {
        this.id = goal.getId();
        this.planId = goal.getPlan().getId();
        this.title = goal.getTitle();
        this.category = goal.getCategory();
        this.targetCount = goal.getTargetCount();
        this.doneCount = goal.getDoneCount();
        this.sortOrder = goal.getSortOrder();
    }
}
