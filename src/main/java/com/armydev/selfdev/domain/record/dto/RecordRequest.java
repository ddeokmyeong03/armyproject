package com.armydev.selfdev.domain.record.dto;

import com.armydev.selfdev.domain.user.GoalCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class RecordRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    @Size(max = 5000)
    private String content;

    @NotNull
    @PastOrPresent
    private LocalDate activityDate;

    @NotNull
    private GoalCategory category;

    private List<Long> tagIds;
}
