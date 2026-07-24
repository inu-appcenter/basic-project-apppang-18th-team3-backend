package shop.apppang.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.apppang.domain.user.dto.request.ChangePasswordRequest;
import shop.apppang.domain.user.dto.request.UpdateUserRequest;
import shop.apppang.domain.user.dto.response.ChangePasswordResponse;
import shop.apppang.domain.user.dto.response.UserMeResponse;
import shop.apppang.domain.user.dto.response.UserResponse;
import shop.apppang.domain.user.service.UserService;
import shop.apppang.domain.user.dto.response.RecentProductResponse;
import shop.apppang.global.exception.ErrorResponse;

@Tag(name = "회원")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    public UserMeResponse getMe(@AuthenticationPrincipal Long userId) {
        return userService.getMyInfo(userId);
    }

    @Operation(summary = "내 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "이름 공백 / 이메일 형식 오류 / 휴대폰 형식 오류 중 하나",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"올바른 이메일 형식을 입력해주세요\"}"))),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"이미 사용 중인 이메일입니다\"}")))
    })
    @PatchMapping("/me")
    public UserResponse updateMe(@AuthenticationPrincipal Long userId,
                                  @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateMyInfo(userId, request);
    }

    @Operation(summary = "비밀번호 변경 (마이페이지)")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "필드 누락 / 새 비밀번호 형식 오류 중 하나",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"현재/새 비밀번호를 입력해주세요.\"}"))),
            @ApiResponse(responseCode = "401", description = "인증 실패 / 현재 비밀번호 불일치 중 하나",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"현재 비밀번호가 올바르지 않습니다.\"}")))
    })
    @PatchMapping("/me/password")
    public ChangePasswordResponse changePassword(@AuthenticationPrincipal Long userId,
                                                   @Valid @RequestBody ChangePasswordRequest request) {
        return userService.changePassword(userId, request);
    }
    @Operation(summary = "최근 찾던 상품 조회")
    @GetMapping("/me/recent-products")
    public RecentProductResponse getRecentProducts(@AuthenticationPrincipal Long userId) {

        return userService.getRecentProducts(userId);

    }

}
