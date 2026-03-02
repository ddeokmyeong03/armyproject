package com.armydev.selfdev.domain.user;

import com.armydev.selfdev.common.exception.BusinessException;
import com.armydev.selfdev.common.exception.ErrorCode;
import com.armydev.selfdev.domain.user.dto.UpdateUserRequest;
import com.armydev.selfdev.domain.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getMe(Long userId) {
        User user = findUser(userId);
        return new UserResponse(user);
    }

    @Transactional
    public UserResponse updateMe(Long userId, UpdateUserRequest request) {
        User user = findUser(userId);

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getDailyMinutes() != null) {
            user.setDailyMinutes(request.getDailyMinutes());
        }
        if (request.getGoalPriorities() != null) {
            user.setGoalPriorities(request.getGoalPriorities());
        }
        if (request.getDischargeDate() != null) {
            user.setDischargeDate(request.getDischargeDate());
        }

        return new UserResponse(userRepository.save(user));
    }

    public User findUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
