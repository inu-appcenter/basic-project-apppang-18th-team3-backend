package shop.apppang.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResetPasswordRequest {

    @Schema(description = "비밀번호 재설정용 임시 토큰", example = "tmp_eyJ...")
    private String resetToken;

    @Schema(description = "새 비밀번호 (8자 이상, 영문+숫자 조합)", example = "newpass12")
    private String newPassword;
}
