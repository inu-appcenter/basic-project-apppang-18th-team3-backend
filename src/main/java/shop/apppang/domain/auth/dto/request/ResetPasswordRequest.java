package shop.apppang.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.apppang.global.validation.ValidationPatterns;

@Getter
@NoArgsConstructor
public class ResetPasswordRequest {

    @Schema(description = "비밀번호 재설정용 임시 토큰", example = "tmp_eyJ...")
    private String resetToken;

    @Schema(description = "새 비밀번호 (8~20자, 영문+숫자 조합)", example = "newpass12")
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Pattern(regexp = ValidationPatterns.PASSWORD, message = ValidationPatterns.PASSWORD_MESSAGE)
    private String newPassword;
}
