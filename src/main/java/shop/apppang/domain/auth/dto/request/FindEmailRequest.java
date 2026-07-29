package shop.apppang.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.apppang.global.validation.ValidationPatterns;

@Getter
@NoArgsConstructor
public class FindEmailRequest {

    @Schema(description = "이름", example = "정태영")
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @Schema(description = "전화번호 (하이픈 없이 숫자만)", example = "01012345678")
    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = ValidationPatterns.PHONE_NUMBER, message = ValidationPatterns.PHONE_NUMBER_MESSAGE)
    private String phoneNumber;
}
