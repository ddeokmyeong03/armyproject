package com.armydev.selfdev.domain.record;

import com.armydev.selfdev.common.response.ApiResponse;
import com.armydev.selfdev.common.response.PageResponse;
import com.armydev.selfdev.domain.record.dto.RecordRequest;
import com.armydev.selfdev.domain.record.dto.RecordResponse;
import com.armydev.selfdev.domain.star.StarService;
import com.armydev.selfdev.domain.star.dto.StarRequest;
import com.armydev.selfdev.domain.star.dto.StarResponse;
import com.armydev.selfdev.domain.user.GoalCategory;
import com.armydev.selfdev.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@Tag(name = "Records", description = "활동 기록 API")
public class RecordController {

    private final RecordService recordService;
    private final StarService starService;

    @GetMapping
    @Operation(summary = "기록 목록")
    public ResponseEntity<PageResponse<RecordResponse>> getRecords(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestParam(required = false) GoalCategory category,
            @RequestParam(required = false) Long tagId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<RecordResponse> response = recordService.getRecords(
            securityUser.getUserId(), category, tagId, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "기록 생성")
    public ResponseEntity<ApiResponse<RecordResponse>> createRecord(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Valid @RequestBody RecordRequest request) {
        RecordResponse response = recordService.createRecord(securityUser.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{recordId}")
    @Operation(summary = "기록 상세")
    public ResponseEntity<ApiResponse<RecordResponse>> getRecord(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long recordId) {
        RecordResponse response = recordService.getRecord(securityUser.getUserId(), recordId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{recordId}")
    @Operation(summary = "기록 수정")
    public ResponseEntity<ApiResponse<RecordResponse>> updateRecord(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long recordId,
            @Valid @RequestBody RecordRequest request) {
        RecordResponse response = recordService.updateRecord(securityUser.getUserId(), recordId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{recordId}")
    @Operation(summary = "기록 삭제")
    public ResponseEntity<Void> deleteRecord(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long recordId) {
        recordService.deleteRecord(securityUser.getUserId(), recordId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{recordId}/star")
    @Operation(summary = "STAR 생성")
    public ResponseEntity<ApiResponse<StarResponse>> createStar(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long recordId,
            @Valid @RequestBody StarRequest request) {
        StarResponse response = starService.createStar(securityUser.getUserId(), recordId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{recordId}/star")
    @Operation(summary = "STAR 조회")
    public ResponseEntity<ApiResponse<StarResponse>> getStar(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long recordId) {
        StarResponse response = starService.getStar(securityUser.getUserId(), recordId).orElse(null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{recordId}/star")
    @Operation(summary = "STAR 수정")
    public ResponseEntity<ApiResponse<StarResponse>> updateStar(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long recordId,
            @Valid @RequestBody StarRequest request) {
        StarResponse response = starService.updateStar(securityUser.getUserId(), recordId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
