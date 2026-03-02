package com.armydev.selfdev.domain.plan;

import com.armydev.selfdev.common.exception.BusinessException;
import com.armydev.selfdev.common.exception.ErrorCode;
import com.armydev.selfdev.common.util.DateUtil;
import com.armydev.selfdev.domain.plan.dto.PlanRequest;
import com.armydev.selfdev.domain.plan.dto.PlanResponse;
import com.armydev.selfdev.domain.user.User;
import com.armydev.selfdev.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeeklyPlanService {

    private final WeeklyPlanRepository planRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PlanResponse> getPlans(Long userId) {
        return planRepository.findByUserIdOrderByWeekStartDesc(userId)
            .stream().map(PlanResponse::new).toList();
    }

    @Transactional
    public PlanResponse createPlan(Long userId, PlanRequest request) {
        if (!DateUtil.isMonday(request.getWeekStart())) {
            throw new BusinessException(ErrorCode.INVALID_WEEK_START);
        }
        if (planRepository.existsByUserIdAndWeekStart(userId, request.getWeekStart())) {
            throw new BusinessException(ErrorCode.PLAN_ALREADY_EXISTS);
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        WeeklyPlan plan = WeeklyPlan.builder()
            .user(user)
            .weekStart(request.getWeekStart())
            .memo(request.getMemo())
            .build();

        return new PlanResponse(planRepository.save(plan));
    }

    @Transactional(readOnly = true)
    public PlanResponse getPlan(Long userId, Long planId) {
        WeeklyPlan plan = findPlan(userId, planId);
        return new PlanResponse(plan);
    }

    @Transactional
    public PlanResponse updatePlan(Long userId, Long planId, PlanRequest request) {
        WeeklyPlan plan = findPlan(userId, planId);
        plan.setMemo(request.getMemo());
        return new PlanResponse(planRepository.save(plan));
    }

    @Transactional
    public void deletePlan(Long userId, Long planId) {
        WeeklyPlan plan = findPlan(userId, planId);
        planRepository.delete(plan);
    }

    public WeeklyPlan findPlan(Long userId, Long planId) {
        return planRepository.findByIdAndUserId(planId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public WeeklyPlan findOrCreateCurrentWeekPlan(Long userId) {
        LocalDate monday = DateUtil.getMondayOfCurrentWeek();
        return planRepository.findByUserIdAndWeekStart(userId, monday)
            .orElse(null);
    }
}
