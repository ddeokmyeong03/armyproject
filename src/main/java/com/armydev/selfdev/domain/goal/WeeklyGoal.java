package com.armydev.selfdev.domain.goal;

import com.armydev.selfdev.domain.plan.WeeklyPlan;
import com.armydev.selfdev.domain.user.GoalCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "weekly_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private WeeklyPlan plan;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private GoalCategory category = GoalCategory.ETC;

    @Column(name = "target_count", nullable = false)
    @Builder.Default
    private int targetCount = 1;

    @Column(name = "done_count", nullable = false)
    @Builder.Default
    private int doneCount = 0;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
