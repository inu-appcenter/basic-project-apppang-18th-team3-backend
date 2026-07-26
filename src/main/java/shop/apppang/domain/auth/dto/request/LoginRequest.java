package shop.apppang.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    @Schema(description = "이메일", example = "a@a.com")
    @NotBlank(message = "이메일과 비밀번호를 입력해주세요.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String email;

    @Schema(description = "비밀번호", example = "abcd1234")
    @NotBlank(message = "이메일과 비밀번호를 입력해주세요.")
    private String password;
}
