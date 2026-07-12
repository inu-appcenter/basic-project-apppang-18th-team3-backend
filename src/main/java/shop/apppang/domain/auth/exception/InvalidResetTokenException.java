package shop.apppang.domain.auth.exception;

public class InvalidResetTokenException extends RuntimeException {
    public InvalidResetTokenException() {
        super("유효하지 않거나 만료된 요청입니다");
    }
}
