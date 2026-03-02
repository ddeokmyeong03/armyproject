package com.armydev.selfdev.domain.roadmap;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "roadmap_weeks",
    uniqueConstraints = @UniqueConstraint(columnNames = {"roadmap_id", "week_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapWeek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

    @Column(name = "week_number", nullable = false)
    private int weekNumber;

    @Column(name = "goal_title", nullable = false, length = 200)
    private String goalTitle;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "task_titles", nullable = false, columnDefinition = "JSON")
    private List<String> taskTitles;
}
