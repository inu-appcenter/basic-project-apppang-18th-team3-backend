package shop.apppang.global.validation;

public final class ValidationPatterns {

    public static final String PHONE_NUMBER = "^01[016789]\\d{7,8}$";
    public static final String PHONE_NUMBER_MESSAGE = "휴대폰 번호는 하이픈(-) 없이 숫자만 입력해주세요 (예: 01012345678)";

    public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$";
    public static final String PASSWORD_MESSAGE = "비밀번호는 8~20자, 영문+숫자 조합이어야 합니다. 특수문자는 사용할 수 없습니다.";

    private ValidationPatterns() {
    }
}
