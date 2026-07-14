package shop.apppang.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserMeResponse {

    private Long userId;

    private String email;

    private String name;

    private String phoneNumber;

    private Long appMoney;

    private LocalDateTime createdAt;
}
