package com.armydev.selfdev.domain.goal.dto;

import com.armydev.selfdev.domain.user.GoalCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoalRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotNull
    private GoalCategory category;

    @Min(1)
    private int targetCount = 1;
}
