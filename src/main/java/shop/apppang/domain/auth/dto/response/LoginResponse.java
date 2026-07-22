package shop.apppang.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class LoginResponse {

    private String token;

    private UserInfo user;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class UserInfo{
        private Long userId;
        private String name;
    }
}
