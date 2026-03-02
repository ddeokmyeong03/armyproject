package com.armydev.selfdev.domain.star;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StarRepository extends JpaRepository<Star, Long> {
    Optional<Star> findByRecordId(Long recordId);
    boolean existsByRecordId(Long recordId);
}
