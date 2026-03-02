package com.armydev.selfdev.domain.record;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByUserIdOrderByName(Long userId);
    Optional<Tag> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserIdAndName(Long userId, String name);
    Optional<Tag> findByUserIdAndName(Long userId, String name);
}
