package com.armydev.selfdev.domain.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeeklyGoalRepository extends JpaRepository<WeeklyGoal, Long> {
    List<WeeklyGoal> findByPlanIdOrderBySortOrder(Long planId);
    Optional<WeeklyGoal> findByIdAndPlanId(Long id, Long planId);

    @Query("SELECT g FROM WeeklyGoal g WHERE g.plan.user.id = :userId AND g.plan.weekStart = :weekStart ORDER BY g.sortOrder")
    List<WeeklyGoal> findByPlanUserIdAndPlanWeekStart(Long userId, LocalDate weekStart);
}
