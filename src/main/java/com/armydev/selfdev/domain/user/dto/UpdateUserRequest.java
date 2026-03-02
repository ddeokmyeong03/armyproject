package com.armydev.selfdev.domain.user.dto;

import com.armydev.selfdev.domain.user.GoalCategory;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UpdateUserRequest {

    @Size(max = 50)
    private String nickname;

    @Min(10)
    @Max(480)
    private Integer dailyMinutes;

    @Size(max = 6)
    private List<@NotNull GoalCategory> goalPriorities;

    private LocalDate dischargeDate;
}
