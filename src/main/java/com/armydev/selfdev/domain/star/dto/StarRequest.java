package com.armydev.selfdev.domain.star.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StarRequest {

    @NotBlank
    private String situation;

    @NotBlank
    private String taskDesc;

    @NotBlank
    private String action;

    @NotBlank
    private String result;
}
