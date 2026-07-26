package shop.apppang.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.apppang.global.validation.ValidationPatterns;

@Getter
@NoArgsConstructor
public class ChangePasswordRequest {

    @Schema(description = "현재 비밀번호", example = "old1234")
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    @Schema(description = "새 비밀번호 (8~20자, 영문+숫자 조합)", example = "new12345")
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Pattern(
            regexp = ValidationPatterns.PASSWORD,
            message = ValidationPatterns.PASSWORD_MESSAGE
    )
    private String newPassword;
}
