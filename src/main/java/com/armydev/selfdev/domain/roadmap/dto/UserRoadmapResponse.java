package com.armydev.selfdev.domain.roadmap.dto;

import com.armydev.selfdev.domain.roadmap.UserRoadmap;
import com.armydev.selfdev.domain.roadmap.UserRoadmapStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserRoadmapResponse {
    private final Long id;
    private final Long roadmapId;
    private final String roadmapTitle;
    private final UserRoadmapStatus status;
    private final LocalDate startedAt;
    private final int currentWeek;
    private final int totalWeeks;

    public UserRoadmapResponse(UserRoadmap ur) {
        this.id = ur.getId();
        this.roadmapId = ur.getRoadmap().getId();
        this.roadmapTitle = ur.getRoadmap().getTitle();
        this.status = ur.getStatus();
        this.startedAt = ur.getStartedAt();
        this.currentWeek = ur.getCurrentWeek();
        this.totalWeeks = ur.getRoadmap().getDurationWeeks();
    }
}
