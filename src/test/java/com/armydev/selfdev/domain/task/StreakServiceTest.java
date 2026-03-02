package com.armydev.selfdev.domain.task;

import com.armydev.selfdev.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StreakServiceTest {

    @InjectMocks
    StreakService streakService;

    @Test
    @DisplayName("첫 번째 완료 시 스트릭 1")
    void 첫번째_완료시_스트릭1() {
        User user = User.builder().currentStreak(0).maxStreak(0).build();
        streakService.updateStreak(user, LocalDate.of(2026, 3, 2));

        assertThat(user.getCurrentStreak()).isEqualTo(1);
        assertThat(user.getMaxStreak()).isEqualTo(1);
        assertThat(user.getLastCompletedDate()).isEqualTo(LocalDate.of(2026, 3, 2));
    }

    @Test
    @DisplayName("어제 완료 후 오늘 완료 시 스트릭 증가")
    void 연속완료시_스트릭증가() {
        User user = User.builder()
            .currentStreak(5).maxStreak(10)
            .lastCompletedDate(LocalDate.of(2026, 3, 1)).build();
        streakService.updateStreak(user, LocalDate.of(2026, 3, 2));

        assertThat(user.getCurrentStreak()).isEqualTo(6);
        assertThat(user.getMaxStreak()).isEqualTo(10); // max는 그대로
    }

    @Test
    @DisplayName("스트릭이 최대치 갱신")
    void 최대치_갱신() {
        User user = User.builder()
            .currentStreak(10).maxStreak(10)
            .lastCompletedDate(LocalDate.of(2026, 3, 1)).build();
        streakService.updateStreak(user, LocalDate.of(2026, 3, 2));

        assertThat(user.getCurrentStreak()).isEqualTo(11);
        assertThat(user.getMaxStreak()).isEqualTo(11);
    }

    @Test
    @DisplayName("오늘 이미 완료한 경우 스트릭 변경 없음")
    void 오늘_중복완료_불변() {
        User user = User.builder()
            .currentStreak(5).maxStreak(10)
            .lastCompletedDate(LocalDate.of(2026, 3, 2)).build();
        streakService.updateStreak(user, LocalDate.of(2026, 3, 2));

        assertThat(user.getCurrentStreak()).isEqualTo(5); // 변경 없음
    }

    @Test
    @DisplayName("이틀 이상 공백 시 스트릭 1로 초기화")
    void 연속_끊김시_스트릭초기화() {
        User user = User.builder()
            .currentStreak(5).maxStreak(10)
            .lastCompletedDate(LocalDate.of(2026, 2, 28)).build();
        streakService.updateStreak(user, LocalDate.of(2026, 3, 2)); // 2일 공백

        assertThat(user.getCurrentStreak()).isEqualTo(1);
        assertThat(user.getMaxStreak()).isEqualTo(10); // max는 유지
    }
}
