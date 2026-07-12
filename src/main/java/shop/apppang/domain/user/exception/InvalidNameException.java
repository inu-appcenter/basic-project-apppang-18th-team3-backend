package shop.apppang.domain.user.exception;

public class InvalidNameException extends RuntimeException {
    public InvalidNameException() {
        super("이름을 입력해주세요");
    }
}
