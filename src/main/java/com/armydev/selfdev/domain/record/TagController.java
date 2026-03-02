package com.armydev.selfdev.domain.record;

import com.armydev.selfdev.common.response.ApiResponse;
import com.armydev.selfdev.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@Tag(name = "Tags", description = "태그 API")
public class TagController {

    private final TagService tagService;

    @GetMapping
    @Operation(summary = "태그 목록")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTags(
            @AuthenticationPrincipal SecurityUser securityUser) {
        List<Map<String, Object>> tags = tagService.getTags(securityUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(tags));
    }

    @PostMapping
    @Operation(summary = "태그 생성")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createTag(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody Map<String, String> body) {
        Map<String, Object> tag = tagService.createTag(securityUser.getUserId(), body.get("name"));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(tag));
    }

    @DeleteMapping("/{tagId}")
    @Operation(summary = "태그 삭제")
    public ResponseEntity<Void> deleteTag(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long tagId) {
        tagService.deleteTag(securityUser.getUserId(), tagId);
        return ResponseEntity.noContent().build();
    }
}
