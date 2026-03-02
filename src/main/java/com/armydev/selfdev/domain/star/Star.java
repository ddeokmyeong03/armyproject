package com.armydev.selfdev.domain.star;

import com.armydev.selfdev.domain.record.Record;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Star {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false, unique = true)
    private Record record;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String situation;

    @Column(name = "task_desc", nullable = false, columnDefinition = "TEXT")
    private String taskDesc;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String action;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String result;

    @Column(name = "generated_text", nullable = false, columnDefinition = "TEXT")
    private String generatedText;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
