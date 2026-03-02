package com.armydev.selfdev.domain.roadmap;

import com.armydev.selfdev.common.exception.BusinessException;
import com.armydev.selfdev.common.exception.ErrorCode;
import com.armydev.selfdev.common.util.DateUtil;
import com.armydev.selfdev.domain.goal.WeeklyGoal;
import com.armydev.selfdev.domain.goal.WeeklyGoalRepository;
import com.armydev.selfdev.domain.plan.WeeklyPlan;
import com.armydev.selfdev.domain.plan.WeeklyPlanRepository;
import com.armydev.selfdev.domain.roadmap.dto.*;
import com.armydev.selfdev.domain.task.Task;
import com.armydev.selfdev.domain.task.TaskRepository;
import com.armydev.selfdev.domain.user.GoalCategory;
import com.armydev.selfdev.domain.user.User;
import com.armydev.selfdev.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapWeekRepository roadmapWeekRepository;
    private final UserRoadmapRepository userRoadmapRepository;
    private final UserRepository userRepository;
    private final WeeklyPlanRepository planRepository;
    private final WeeklyGoalRepository goalRepository;
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public List<RoadmapResponse> getRoadmaps(GoalCategory category) {
        List<Roadmap> roadmaps = category != null
            ? roadmapRepository.findByOfficialTrueAndCategory(category)
            : roadmapRepository.findByOfficialTrue();
        return roadmaps.stream().map(RoadmapResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public RoadmapResponse getRoadmap(Long roadmapId) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ROADMAP_NOT_FOUND));
        List<RoadmapWeekResponse> weeks = roadmapWeekRepository
            .findByRoadmapIdOrderByWeekNumber(roadmapId)
            .stream().map(RoadmapWeekResponse::new).toList();
        return new RoadmapResponse(roadmap, weeks);
    }

    @Transactional(readOnly = true)
    public List<UserRoadmapResponse> getMyRoadmaps(Long userId) {
        return userRoadmapRepository.findByUserId(userId)
            .stream().map(UserRoadmapResponse::new).toList();
    }

    @Transactional
    public UserRoadmapResponse applyRoadmap(Long userId, ApplyRoadmapRequest request) {
        Roadmap roadmap = roadmapRepository.findById(request.getRoadmapId())
            .orElseThrow(() -> new BusinessException(ErrorCode.ROADMAP_NOT_FOUND));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        UserRoadmap userRoadmap = UserRoadmap.builder()
            .user(user)
            .roadmap(roadmap)
            .startedAt(request.getStartedAt())
            .build();
        userRoadmap = userRoadmapRepository.save(userRoadmap);

        if (request.isSeedCurrentWeek()) {
            seedCurrentWeek(user, roadmap, userRoadmap);
        }

        return new UserRoadmapResponse(userRoadmap);
    }

    @Transactional
    public UserRoadmapResponse updateUserRoadmap(Long userId, Long userRoadmapId,
                                                  UserRoadmapStatus status) {
        UserRoadmap ur = userRoadmapRepository.findByIdAndUserId(userRoadmapId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_ROADMAP_NOT_FOUND));
        ur.setStatus(status);
        return new UserRoadmapResponse(userRoadmapRepository.save(ur));
    }

    private void seedCurrentWeek(User user, Roadmap roadmap, UserRoadmap userRoadmap) {
        LocalDate monday = DateUtil.getMondayOfCurrentWeek();

        RoadmapWeek rw = roadmapWeekRepository
            .findByRoadmapIdAndWeekNumber(roadmap.getId(), userRoadmap.getCurrentWeek())
            .orElse(null);
        if (rw == null) return;

        WeeklyPlan plan = planRepository.findByUserIdAndWeekStart(user.getId(), monday)
            .orElseGet(() -> planRepository.save(WeeklyPlan.builder()
                .user(user).weekStart(monday).build()));

        WeeklyGoal goal = WeeklyGoal.builder()
            .plan(plan)
            .title(rw.getGoalTitle())
            .category(roadmap.getCategory())
            .targetCount(rw.getTaskTitles().size())
            .build();
        goal = goalRepository.save(goal);

        for (String taskTitle : rw.getTaskTitles()) {
            taskRepository.save(Task.builder()
                .plan(plan)
                .goal(goal)
                .title(taskTitle)
                .scheduledDate(monday)
                .build());
        }
    }
}
