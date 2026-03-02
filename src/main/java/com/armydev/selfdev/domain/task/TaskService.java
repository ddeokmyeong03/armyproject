package com.armydev.selfdev.domain.task;

import com.armydev.selfdev.common.exception.BusinessException;
import com.armydev.selfdev.common.exception.ErrorCode;
import com.armydev.selfdev.domain.goal.WeeklyGoal;
import com.armydev.selfdev.domain.goal.WeeklyGoalRepository;
import com.armydev.selfdev.domain.plan.WeeklyPlan;
import com.armydev.selfdev.domain.plan.WeeklyPlanService;
import com.armydev.selfdev.domain.task.dto.TaskRequest;
import com.armydev.selfdev.domain.task.dto.TaskResponse;
import com.armydev.selfdev.domain.task.dto.ToggleRequest;
import com.armydev.selfdev.domain.user.User;
import com.armydev.selfdev.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final WeeklyPlanService planService;
    private final WeeklyGoalRepository goalRepository;
    private final UserRepository userRepository;
    private final StreakService streakService;

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(Long userId, Long planId, LocalDate date) {
        planService.findPlan(userId, planId);
        List<Task> tasks = date != null
            ? taskRepository.findByPlanIdAndScheduledDateOrderBySortOrder(planId, date)
            : taskRepository.findByPlanIdOrderBySortOrder(planId);
        return tasks.stream().map(TaskResponse::new).toList();
    }

    @Transactional
    public TaskResponse createTask(Long userId, Long planId, TaskRequest request) {
        WeeklyPlan plan = planService.findPlan(userId, planId);

        // Validate scheduledDate is within the week
        LocalDate weekEnd = plan.getWeekStart().plusDays(6);
        if (request.getScheduledDate().isBefore(plan.getWeekStart()) ||
            request.getScheduledDate().isAfter(weekEnd)) {
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);
        }

        WeeklyGoal goal = null;
        if (request.getGoalId() != null) {
            goal = goalRepository.findByIdAndPlanId(request.getGoalId(), planId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GOAL_NOT_FOUND));
        }

        Task task = Task.builder()
            .plan(plan)
            .goal(goal)
            .title(request.getTitle())
            .scheduledDate(request.getScheduledDate())
            .build();

        return new TaskResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTask(Long userId, Long planId, Long taskId, TaskRequest request) {
        planService.findPlan(userId, planId);
        Task task = findTask(planId, taskId);

        task.setTitle(request.getTitle());
        task.setScheduledDate(request.getScheduledDate());

        if (request.getGoalId() != null) {
            WeeklyGoal goal = goalRepository.findByIdAndPlanId(request.getGoalId(), planId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GOAL_NOT_FOUND));
            task.setGoal(goal);
        } else {
            task.setGoal(null);
        }

        return new TaskResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long userId, Long planId, Long taskId) {
        planService.findPlan(userId, planId);
        Task task = findTask(planId, taskId);
        taskRepository.delete(task);
    }

    @Transactional
    public Map<String, Object> toggleTask(Long userId, Long planId, Long taskId, ToggleRequest request) {
        planService.findPlan(userId, planId);
        Task task = findTask(planId, taskId);

        task.setDone(request.getDone());
        if (request.getDone()) {
            task.setDoneAt(LocalDateTime.now());
        } else {
            task.setDoneAt(null);
        }
        taskRepository.save(task);

        // Update streak if task is completed
        User user = task.getPlan().getUser();
        if (request.getDone()) {
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
            streakService.updateStreak(user, today);
            userRepository.save(user);
        }

        return Map.of(
            "task", new TaskResponse(task),
            "streak", Map.of("current", user.getCurrentStreak(), "max", user.getMaxStreak())
        );
    }

    private Task findTask(Long planId, Long taskId) {
        return taskRepository.findByIdAndPlanId(taskId, planId)
            .orElseThrow(() -> new BusinessException(ErrorCode.TASK_NOT_FOUND));
    }
}
