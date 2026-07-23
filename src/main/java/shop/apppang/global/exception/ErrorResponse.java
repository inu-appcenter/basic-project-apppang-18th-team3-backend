package shop.apppang.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ErrorResponse {

    @Schema(description = "에러 메시지", example = "잘못된 요청입니다.")
    private final String error;

    public ErrorResponse(String error){
        this.error = error;
    }
}
