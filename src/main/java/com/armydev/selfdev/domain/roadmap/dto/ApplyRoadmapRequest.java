package com.armydev.selfdev.domain.roadmap.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ApplyRoadmapRequest {

    @NotNull
    private Long roadmapId;

    @NotNull
    private LocalDate startedAt;

    private boolean seedCurrentWeek = false;
}
