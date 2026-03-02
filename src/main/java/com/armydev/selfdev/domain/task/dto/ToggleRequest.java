package com.armydev.selfdev.domain.task.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToggleRequest {
    @NotNull
    private Boolean done;
}
