package shop.apppang.domain.auth.util;

public class EmailMaskingUtil {

    private EmailMaskingUtil() {
    }

    public static String mask(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 0) {
            return email; // 방어 코드: '@' 없거나 맨 앞에 있으면 마스킹 없이 반환
        }

        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        String maskedLocalPart = localPart.length() <= 3
                ? localPart.charAt(0) + "*".repeat(localPart.length() - 1)
                : localPart.substring(0, 3) + "*".repeat(localPart.length() - 3);

        return maskedLocalPart + domainPart;
    }
}