package com.armydev.selfdev.domain.record;

import com.armydev.selfdev.common.exception.BusinessException;
import com.armydev.selfdev.common.exception.ErrorCode;
import com.armydev.selfdev.common.response.PageResponse;
import com.armydev.selfdev.domain.record.dto.RecordRequest;
import com.armydev.selfdev.domain.record.dto.RecordResponse;
import com.armydev.selfdev.domain.user.GoalCategory;
import com.armydev.selfdev.domain.user.User;
import com.armydev.selfdev.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<RecordResponse> getRecords(Long userId, GoalCategory category,
                                                    Long tagId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size,
            Sort.by(Sort.Direction.DESC, "activityDate"));
        Page<Record> records;

        if (tagId != null) {
            records = recordRepository.findByUserIdAndTagId(userId, tagId, pageRequest);
        } else if (category != null) {
            records = recordRepository.findByUserIdAndCategory(userId, category, pageRequest);
        } else {
            records = recordRepository.findByUserId(userId, pageRequest);
        }

        List<RecordResponse> data = records.getContent().stream()
            .map(RecordResponse::new).toList();
        return new PageResponse<>(data, records.getTotalElements(), page, size);
    }

    @Transactional
    public RecordResponse createRecord(Long userId, RecordRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Record record = Record.builder()
            .user(user)
            .title(request.getTitle())
            .content(request.getContent())
            .activityDate(request.getActivityDate())
            .category(request.getCategory())
            .build();

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            linkTags(record, userId, request.getTagIds());
        }

        return new RecordResponse(recordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public RecordResponse getRecord(Long userId, Long recordId) {
        Record record = findRecord(userId, recordId);
        return new RecordResponse(record);
    }

    @Transactional
    public RecordResponse updateRecord(Long userId, Long recordId, RecordRequest request) {
        Record record = findRecord(userId, recordId);

        record.setTitle(request.getTitle());
        record.setContent(request.getContent());
        record.setActivityDate(request.getActivityDate());
        record.setCategory(request.getCategory());
        record.getRecordTags().clear();

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            linkTags(record, userId, request.getTagIds());
        }

        return new RecordResponse(recordRepository.save(record));
    }

    @Transactional
    public void deleteRecord(Long userId, Long recordId) {
        Record record = findRecord(userId, recordId);
        recordRepository.delete(record);
    }

    public Record findRecord(Long userId, Long recordId) {
        return recordRepository.findByIdAndUserId(recordId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RECORD_NOT_FOUND));
    }

    private void linkTags(Record record, Long userId, List<Long> tagIds) {
        List<RecordTag> recordTags = new ArrayList<>();
        for (Long tagId : tagIds) {
            Tag tag = tagRepository.findByIdAndUserId(tagId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TAG_NOT_FOUND));
            recordTags.add(RecordTag.builder().record(record).tag(tag).build());
        }
        record.getRecordTags().addAll(recordTags);
    }
}
