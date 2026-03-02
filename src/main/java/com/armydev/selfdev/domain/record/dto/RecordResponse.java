package com.armydev.selfdev.domain.record.dto;

import com.armydev.selfdev.domain.record.Record;
import com.armydev.selfdev.domain.user.GoalCategory;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RecordResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final LocalDate activityDate;
    private final GoalCategory category;
    private final List<TagInfo> tags;
    private final boolean hasStar;
    private final LocalDateTime createdAt;

    public RecordResponse(Record record) {
        this.id = record.getId();
        this.title = record.getTitle();
        this.content = record.getContent();
        this.activityDate = record.getActivityDate();
        this.category = record.getCategory();
        this.tags = record.getRecordTags().stream()
            .map(rt -> new TagInfo(rt.getTag().getId(), rt.getTag().getName()))
            .toList();
        this.hasStar = record.getStar() != null;
        this.createdAt = record.getCreatedAt();
    }

    @Getter
    public static class TagInfo {
        private final Long id;
        private final String name;

        public TagInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
