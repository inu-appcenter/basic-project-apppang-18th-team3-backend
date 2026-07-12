package shop.apppang.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    @JsonProperty("user_id")
    private Long userId;

    private String email;

    private String name;

    private String phone;
}
