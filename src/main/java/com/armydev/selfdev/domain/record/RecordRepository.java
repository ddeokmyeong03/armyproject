package com.armydev.selfdev.domain.record;

import com.armydev.selfdev.domain.user.GoalCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long> {
    Optional<Record> findByIdAndUserId(Long id, Long userId);

    Page<Record> findByUserId(Long userId, Pageable pageable);
    Page<Record> findByUserIdAndCategory(Long userId, GoalCategory category, Pageable pageable);

    @Query("SELECT r FROM Record r JOIN r.recordTags rt WHERE r.user.id = :userId AND rt.tag.id = :tagId ORDER BY r.activityDate DESC")
    Page<Record> findByUserIdAndTagId(Long userId, Long tagId, Pageable pageable);

    @Query("SELECT r FROM Record r WHERE r.user.id = :userId ORDER BY r.activityDate DESC")
    List<Record> findTop3ByUserIdOrderByActivityDateDesc(Long userId, Pageable pageable);
}
