package com.armydev.selfdev.domain.plan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PlanRequest {

    @NotNull
    private LocalDate weekStart;

    private String memo;
}
