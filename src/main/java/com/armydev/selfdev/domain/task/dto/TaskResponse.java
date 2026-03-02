package com.armydev.selfdev.domain.task.dto;

import com.armydev.selfdev.domain.task.Task;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class TaskResponse {
    private final Long id;
    private final Long planId;
    private final Long goalId;
    private final String title;
    private final LocalDate scheduledDate;
    private final boolean done;
    private final LocalDateTime doneAt;
    private final int sortOrder;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.planId = task.getPlan().getId();
        this.goalId = task.getGoal() != null ? task.getGoal().getId() : null;
        this.title = task.getTitle();
        this.scheduledDate = task.getScheduledDate();
        this.done = task.isDone();
        this.doneAt = task.getDoneAt();
        this.sortOrder = task.getSortOrder();
    }
}
