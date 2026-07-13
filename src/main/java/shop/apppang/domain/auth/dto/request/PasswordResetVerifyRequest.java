package shop.apppang.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordResetVerifyRequest {

    @NotBlank(message = "이메일 형식이 올바르지 않습니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotBlank(message = "휴대폰 번호 형식이 올바르지 않습니다")
    @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "휴대폰 번호 형식이 올바르지 않습니다")
    private String phoneNumber;
}
