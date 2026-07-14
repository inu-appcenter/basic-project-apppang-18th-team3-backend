package shop.apppang.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// SignupResponse.java
@Getter
@AllArgsConstructor
@Builder
public class SignupResponse {

    private Long userId;

    private String email;

    private String name;
}