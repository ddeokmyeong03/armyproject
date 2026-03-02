package com.armydev.selfdev.domain.roadmap.dto;

import com.armydev.selfdev.domain.roadmap.Roadmap;
import com.armydev.selfdev.domain.user.GoalCategory;
import lombok.Getter;

import java.util.List;

@Getter
public class RoadmapResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final GoalCategory category;
    private final int durationWeeks;
    private final boolean isOfficial;
    private final List<RoadmapWeekResponse> weeks;

    public RoadmapResponse(Roadmap roadmap, List<RoadmapWeekResponse> weeks) {
        this.id = roadmap.getId();
        this.title = roadmap.getTitle();
        this.description = roadmap.getDescription();
        this.category = roadmap.getCategory();
        this.durationWeeks = roadmap.getDurationWeeks();
        this.isOfficial = roadmap.isOfficial(); // Lombok generates isOfficial() for 'official' boolean field
        this.weeks = weeks;
    }

    public RoadmapResponse(Roadmap roadmap) {
        this(roadmap, null);
    }
}
