package shop.apppang.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueRequest {

    @Schema(description = "재발급에 사용할 리프레시 토큰", example = "eyJhbGci...")
    @NotBlank(message = "리프레시 토큰을 입력해주세요.")
    private String refreshToken;
}
