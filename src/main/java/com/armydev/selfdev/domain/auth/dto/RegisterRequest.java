package com.armydev.selfdev.domain.auth.dto;

import com.armydev.selfdev.domain.user.GoalCategory;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
             message = "비밀번호는 영문, 숫자, 특수문자(@$!%*#?&)를 각각 1개 이상 포함해야 합니다.")
    private String password;

    @NotBlank
    @Size(max = 50)
    private String nickname;

    @NotNull
    @Future
    private LocalDate dischargeDate;

    @Min(10)
    @Max(480)
    private int dailyMinutes = 60;

    @NotEmpty
    @Size(max = 6)
    private List<@NotNull GoalCategory> goalPriorities;
}
