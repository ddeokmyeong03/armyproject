package com.armydev.selfdev.domain.plan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeeklyPlanRepository extends JpaRepository<WeeklyPlan, Long> {
    Optional<WeeklyPlan> findByUserIdAndWeekStart(Long userId, LocalDate weekStart);
    boolean existsByUserIdAndWeekStart(Long userId, LocalDate weekStart);
    List<WeeklyPlan> findByUserIdOrderByWeekStartDesc(Long userId);
    Optional<WeeklyPlan> findByIdAndUserId(Long id, Long userId);
}
