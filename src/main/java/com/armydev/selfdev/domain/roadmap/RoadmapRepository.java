package com.armydev.selfdev.domain.roadmap;

import com.armydev.selfdev.domain.user.GoalCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    List<Roadmap> findByOfficialTrue();
    List<Roadmap> findByOfficialTrueAndCategory(GoalCategory category);
}
