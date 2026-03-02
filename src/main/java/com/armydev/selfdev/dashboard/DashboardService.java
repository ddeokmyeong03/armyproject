package com.armydev.selfdev.dashboard;

import com.armydev.selfdev.common.exception.BusinessException;
import com.armydev.selfdev.common.exception.ErrorCode;
import com.armydev.selfdev.common.util.DateUtil;
import com.armydev.selfdev.domain.goal.WeeklyGoalRepository;
import com.armydev.selfdev.domain.record.RecordRepository;
import com.armydev.selfdev.domain.task.Task;
import com.armydev.selfdev.domain.task.TaskRepository;
import com.armydev.selfdev.domain.user.User;
import com.armydev.selfdev.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final WeeklyGoalRepository goalRepository;
    private final RecordRepository recordRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboard(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate monday = DateUtil.getMondayOfCurrentWeek();
        LocalDate sunday = DateUtil.getSundayOfWeek(monday);

        Map<String, Object> result = new HashMap<>();

        // D-day
        result.put("dDay", DateUtil.dDayUntil(user.getDischargeDate()));
        result.put("dischargeDate", user.getDischargeDate());

        // Streak
        result.put("streak", Map.of(
            "current", user.getCurrentStreak(),
            "max", user.getMaxStreak()
        ));

        // Weekly completion rate
        result.put("weeklyCompletionRate", getWeeklyCompletionRate(userId, monday, sunday));

        // Today's tasks
        List<Task> todayTasks = taskRepository.findTodayTasksByUserId(userId, today);
        result.put("todayTasks", todayTasks.stream().map(t -> Map.of(
            "id", t.getId(),
            "title", t.getTitle(),
            "done", t.isDone(),
            "scheduledDate", t.getScheduledDate()
        )).toList());

        // Weekly goals
        result.put("weeklyGoals", goalRepository.findByPlanUserIdAndPlanWeekStart(userId, monday)
            .stream().map(g -> Map.of(
                "id", g.getId(),
                "title", g.getTitle(),
                "category", g.getCategory(),
                "targetCount", g.getTargetCount(),
                "doneCount", g.getDoneCount()
            )).toList());

        // Recent records (last 3)
        result.put("recentRecords", recordRepository
            .findTop3ByUserIdOrderByActivityDateDesc(userId, PageRequest.of(0, 3))
            .stream().map(r -> Map.of(
                "id", r.getId(),
                "title", r.getTitle(),
                "activityDate", r.getActivityDate(),
                "category", r.getCategory()
            )).toList());

        return result;
    }

    private double getWeeklyCompletionRate(Long userId, LocalDate monday, LocalDate sunday) {
        var goals = goalRepository.findByPlanUserIdAndPlanWeekStart(userId, monday);

        if (goals.isEmpty()) {
            long total = taskRepository.countByPlanUserIdAndScheduledDateBetween(userId, monday, sunday);
            if (total == 0) return 0.0;
            long done = taskRepository.countDoneByPlanUserIdAndScheduledDateBetween(userId, monday, sunday);
            return Math.min(1.0, (double) done / total);
        }

        return goals.stream()
            .mapToDouble(g -> Math.min(1.0, (double) g.getDoneCount() / g.getTargetCount()))
            .average()
            .orElse(0.0);
    }
}
