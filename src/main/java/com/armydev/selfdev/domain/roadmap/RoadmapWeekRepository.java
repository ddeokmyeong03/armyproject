package com.armydev.selfdev.domain.roadmap;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoadmapWeekRepository extends JpaRepository<RoadmapWeek, Long> {
    List<RoadmapWeek> findByRoadmapIdOrderByWeekNumber(Long roadmapId);
    Optional<RoadmapWeek> findByRoadmapIdAndWeekNumber(Long roadmapId, int weekNumber);
}
