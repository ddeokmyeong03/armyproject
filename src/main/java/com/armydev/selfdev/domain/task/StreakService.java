package com.armydev.selfdev.domain.task;

import com.armydev.selfdev.domain.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class StreakService {

    public void updateStreak(User user, LocalDate today) {
        LocalDate last = user.getLastCompletedDate();

        if (last == null) {
            user.setCurrentStreak(1);
        } else if (last.equals(today)) {
            return; // 오늘 이미 처리됨 — 변경 없음
        } else if (last.equals(today.minusDays(1))) {
            user.setCurrentStreak(user.getCurrentStreak() + 1);
        } else {
            user.setCurrentStreak(1);
        }

        user.setLastCompletedDate(today);
        if (user.getCurrentStreak() > user.getMaxStreak()) {
            user.setMaxStreak(user.getCurrentStreak());
        }
    }
}
