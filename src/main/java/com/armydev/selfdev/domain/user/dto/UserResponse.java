package com.armydev.selfdev.domain.user.dto;

import com.armydev.selfdev.domain.user.GoalCategory;
import com.armydev.selfdev.domain.user.User;
import com.armydev.selfdev.common.util.DateUtil;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class UserResponse {
    private final Long id;
    private final String email;
    private final String nickname;
    private final LocalDate dischargeDate;
    private final long dDay;
    private final int dailyMinutes;
    private final List<GoalCategory> goalPriorities;
    private final int currentStreak;
    private final int maxStreak;
    private final LocalDateTime createdAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.dischargeDate = user.getDischargeDate();
        this.dDay = DateUtil.dDayUntil(user.getDischargeDate());
        this.dailyMinutes = user.getDailyMinutes();
        this.goalPriorities = user.getGoalPriorities();
        this.currentStreak = user.getCurrentStreak();
        this.maxStreak = user.getMaxStreak();
        this.createdAt = user.getCreatedAt();
    }
}
