package shop.apppang.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailCheckResponse {
    private  boolean available;
}
