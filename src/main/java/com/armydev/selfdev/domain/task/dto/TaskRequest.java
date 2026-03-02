package com.armydev.selfdev.domain.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotNull
    private LocalDate scheduledDate;

    private Long goalId;
}
