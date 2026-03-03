package com.armydev.selfdev.domain.star;

import com.armydev.selfdev.common.exception.BusinessException;
import com.armydev.selfdev.common.exception.ErrorCode;
import com.armydev.selfdev.domain.record.Record;
import com.armydev.selfdev.domain.record.RecordService;
import com.armydev.selfdev.domain.star.dto.StarRequest;
import com.armydev.selfdev.domain.star.dto.StarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StarService {

    private final StarRepository starRepository;
    private final RecordService recordService;

    @Transactional
    public StarResponse createStar(Long userId, Long recordId, StarRequest request) {
        Record record = recordService.findRecord(userId, recordId);

        if (starRepository.existsByRecordId(recordId)) {
            throw new BusinessException(ErrorCode.STAR_ALREADY_EXISTS);
        }

        String generatedText = generateText(
            request.getSituation(), request.getTaskDesc(),
            request.getAction(), request.getResult()
        );

        Star star = Star.builder()
            .record(record)
            .situation(request.getSituation())
            .taskDesc(request.getTaskDesc())
            .action(request.getAction())
            .result(request.getResult())
            .generatedText(generatedText)
            .build();

        return new StarResponse(starRepository.save(star));
    }

    @Transactional(readOnly = true)
    public Optional<StarResponse> getStar(Long userId, Long recordId) {
        recordService.findRecord(userId, recordId);
        return starRepository.findByRecordId(recordId).map(StarResponse::new);
    }

    @Transactional
    public StarResponse updateStar(Long userId, Long recordId, StarRequest request) {
        recordService.findRecord(userId, recordId);
        Star star = starRepository.findByRecordId(recordId)
            .orElseThrow(() -> new BusinessException(ErrorCode.STAR_NOT_FOUND));

        star.setSituation(request.getSituation());
        star.setTaskDesc(request.getTaskDesc());
        star.setAction(request.getAction());
        star.setResult(request.getResult());
        star.setGeneratedText(generateText(
            request.getSituation(), request.getTaskDesc(),
            request.getAction(), request.getResult()
        ));

        return new StarResponse(starRepository.save(star));
    }

    public String generateText(String situation, String taskDesc, String action, String result) {
        return String.format(
            "%s이라는 상황에서, %s이라는 과제를 맡아, %s하는 행동을 취했습니다. 그 결과, %s이라는 성과를 이루었습니다.",
            situation, taskDesc, action, result
        );
    }
}
