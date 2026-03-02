package com.armydev.selfdev.domain.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByPlanIdOrderBySortOrder(Long planId);
    List<Task> findByPlanIdAndScheduledDateOrderBySortOrder(Long planId, LocalDate scheduledDate);
    Optional<Task> findByIdAndPlanId(Long id, Long planId);

    @Query("SELECT t FROM Task t WHERE t.plan.user.id = :userId AND t.scheduledDate = :date ORDER BY t.sortOrder")
    List<Task> findTodayTasksByUserId(Long userId, LocalDate date);

    long countByPlanUserIdAndScheduledDateBetween(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.plan.user.id = :userId AND t.scheduledDate BETWEEN :start AND :end AND t.done = true")
    long countDoneByPlanUserIdAndScheduledDateBetween(Long userId, LocalDate start, LocalDate end);
}
