package shop.apppang.domain.auth.exception;

public class InvalidPasswordFormatException extends RuntimeException {
    public InvalidPasswordFormatException() {
        super("비밀번호는 8자 이상, 영문+숫자 조합이어야 합니다");
    }
}
