package shop.apppang.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

// SignupRequest.java
@Getter
@NoArgsConstructor
public class SignupRequest {

    @Schema(description = "이메일", example = "a@a.com")
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String email;

    @Schema(description = "비밀번호 (8자 이상, 영문+숫자 조합)", example = "abcd1234")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "비밀번호는 8자 이상, 영문+숫자 조합이어야 합니다."
    )
    private String password;

    @Schema(description = "이름", example = "홍길동")
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @Schema(description = "전화번호", example = "01012345678")
    @NotBlank(message = "전화번호를 입력해주세요.")
    private String phoneNumber;

    @Schema(description = "필수 약관 동의 여부", example = "true")
    @AssertTrue(message = "필수 약관에 동의해주세요.")
    private boolean agreedRequiredTerms;

}
