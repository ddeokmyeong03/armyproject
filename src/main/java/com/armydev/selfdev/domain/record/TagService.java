package com.armydev.selfdev.domain.record;

import com.armydev.selfdev.common.exception.BusinessException;
import com.armydev.selfdev.common.exception.ErrorCode;
import com.armydev.selfdev.domain.user.User;
import com.armydev.selfdev.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTags(Long userId) {
        return tagRepository.findByUserIdOrderByName(userId).stream()
            .map(t -> Map.of("id", (Object) t.getId(), "name", t.getName()))
            .toList();
    }

    @Transactional
    public Map<String, Object> createTag(Long userId, String name) {
        if (tagRepository.existsByUserIdAndName(userId, name)) {
            throw new BusinessException(ErrorCode.TAG_ALREADY_EXISTS);
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Tag tag = Tag.builder().user(user).name(name).build();
        tag = tagRepository.save(tag);
        return Map.of("id", tag.getId(), "name", tag.getName());
    }

    @Transactional
    public void deleteTag(Long userId, Long tagId) {
        Tag tag = tagRepository.findByIdAndUserId(tagId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.TAG_NOT_FOUND));
        tagRepository.delete(tag);
    }
}
