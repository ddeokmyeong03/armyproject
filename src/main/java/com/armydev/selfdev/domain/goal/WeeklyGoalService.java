package com.armydev.selfdev.domain.goal;

import com.armydev.selfdev.common.exception.BusinessException;
import com.armydev.selfdev.common.exception.ErrorCode;
import com.armydev.selfdev.domain.goal.dto.GoalRequest;
import com.armydev.selfdev.domain.goal.dto.GoalResponse;
import com.armydev.selfdev.domain.plan.WeeklyPlan;
import com.armydev.selfdev.domain.plan.WeeklyPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WeeklyGoalService {

    private final WeeklyGoalRepository goalRepository;
    private final WeeklyPlanService planService;

    @Transactional(readOnly = true)
    public List<GoalResponse> getGoals(Long userId, Long planId) {
        planService.findPlan(userId, planId); // ownership check
        return goalRepository.findByPlanIdOrderBySortOrder(planId)
            .stream().map(GoalResponse::new).toList();
    }

    @Transactional
    public GoalResponse createGoal(Long userId, Long planId, GoalRequest request) {
        WeeklyPlan plan = planService.findPlan(userId, planId);

        WeeklyGoal goal = WeeklyGoal.builder()
            .plan(plan)
            .title(request.getTitle())
            .category(request.getCategory())
            .targetCount(request.getTargetCount())
            .build();

        return new GoalResponse(goalRepository.save(goal));
    }

    @Transactional
    public GoalResponse updateGoal(Long userId, Long planId, Long goalId, GoalRequest request) {
        planService.findPlan(userId, planId);
        WeeklyGoal goal = findGoal(planId, goalId);

        goal.setTitle(request.getTitle());
        goal.setCategory(request.getCategory());
        goal.setTargetCount(request.getTargetCount());

        return new GoalResponse(goalRepository.save(goal));
    }

    @Transactional
    public void deleteGoal(Long userId, Long planId, Long goalId) {
        planService.findPlan(userId, planId);
        WeeklyGoal goal = findGoal(planId, goalId);
        goalRepository.delete(goal);
    }

    public WeeklyGoal findGoal(Long planId, Long goalId) {
        return goalRepository.findByIdAndPlanId(goalId, planId)
            .orElseThrow(() -> new BusinessException(ErrorCode.GOAL_NOT_FOUND));
    }
}
