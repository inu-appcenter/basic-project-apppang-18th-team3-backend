package shop.apppang.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.apppang.global.validation.ValidationPatterns;

@Getter
@NoArgsConstructor
public class UpdateUserRequest {

    @Schema(description = "변경할 이메일 (바꿀 항목만 전달)", example = "new@a.com")
    @Email(message = "올바른 이메일 형식을 입력해주세요")
    private String email;

    @Schema(description = "변경할 이름 (바꿀 항목만 전달)", example = "고명재")
    private String name;

    @Schema(description = "변경할 휴대폰 번호 (바꿀 항목만 전달, 하이픈 없이 숫자만)", example = "01012345678")
    @Pattern(regexp = ValidationPatterns.PHONE_NUMBER, message = ValidationPatterns.PHONE_NUMBER_MESSAGE)
    private String phoneNumber;
}
