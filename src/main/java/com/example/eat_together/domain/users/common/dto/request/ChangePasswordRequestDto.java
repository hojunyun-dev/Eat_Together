package com.example.eat_together.domain.users.common.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true) // Jakcson이 역직렬화할 수 있도록 기본 생성자 강제 생성
public class ChangePasswordRequestDto {

    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 최소 8자 이상이어야 하며, 영문(대소문자 무관), 숫자, 특수문자(@$!%*?&)를 각각 최소 1개 포함해야 합니다."
    )
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이여야합니다.")
    @NotBlank(message = "바꿀 비밀번호는 필수입력값입니다.")
    private final String newPassword;

    @NotBlank(message = "현재 비밀번호는 필수입력값입니다.")
    private final String oldPassword;

    public ChangePasswordRequestDto(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
