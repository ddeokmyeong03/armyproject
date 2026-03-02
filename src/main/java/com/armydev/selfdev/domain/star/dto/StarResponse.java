package com.armydev.selfdev.domain.star.dto;

import com.armydev.selfdev.domain.star.Star;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StarResponse {
    private final Long id;
    private final Long recordId;
    private final String situation;
    private final String taskDesc;
    private final String action;
    private final String result;
    private final String generatedText;
    private final LocalDateTime createdAt;

    public StarResponse(Star star) {
        this.id = star.getId();
        this.recordId = star.getRecord().getId();
        this.situation = star.getSituation();
        this.taskDesc = star.getTaskDesc();
        this.action = star.getAction();
        this.result = star.getResult();
        this.generatedText = star.getGeneratedText();
        this.createdAt = star.getCreatedAt();
    }
}
