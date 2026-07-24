package shop.apppang.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.apppang.global.validation.ValidationPatterns;

@Getter
@NoArgsConstructor
public class PasswordResetVerifyRequest {

    @Schema(description = "이메일", example = "a@a.com")
    @NotBlank(message = "이메일 형식이 올바르지 않습니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @Schema(description = "이름", example = "고명재")
    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @Schema(description = "휴대폰 번호 (하이픈 없이 숫자만)", example = "01012345678")
    @NotBlank(message = "휴대폰 번호 형식이 올바르지 않습니다")
    @Pattern(regexp = ValidationPatterns.PHONE_NUMBER, message = ValidationPatterns.PHONE_NUMBER_MESSAGE)
    private String phoneNumber;
}
