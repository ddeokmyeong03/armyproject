package com.armydev.selfdev.domain.roadmap;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoadmapRepository extends JpaRepository<UserRoadmap, Long> {
    List<UserRoadmap> findByUserId(Long userId);
    List<UserRoadmap> findByUserIdAndStatus(Long userId, UserRoadmapStatus status);
    Optional<UserRoadmap> findByIdAndUserId(Long id, Long userId);
}
