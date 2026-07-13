package shop.apppang.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private Long userId;

    private String email;

    private String name;

    private String phoneNumber;
}
