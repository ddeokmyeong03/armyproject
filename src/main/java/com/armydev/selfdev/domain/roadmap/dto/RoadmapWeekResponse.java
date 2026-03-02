package com.armydev.selfdev.domain.roadmap.dto;

import com.armydev.selfdev.domain.roadmap.RoadmapWeek;
import lombok.Getter;

import java.util.List;

@Getter
public class RoadmapWeekResponse {
    private final Long id;
    private final int weekNumber;
    private final String goalTitle;
    private final List<String> taskTitles;

    public RoadmapWeekResponse(RoadmapWeek week) {
        this.id = week.getId();
        this.weekNumber = week.getWeekNumber();
        this.goalTitle = week.getGoalTitle();
        this.taskTitles = week.getTaskTitles();
    }
}
