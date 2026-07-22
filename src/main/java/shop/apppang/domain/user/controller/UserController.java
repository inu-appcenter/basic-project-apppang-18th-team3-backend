package shop.apppang.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import shop.apppang.domain.user.dto.RecentProductResponse;

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
    @PatchMapping("/me")
    public UserResponse updateMe(@AuthenticationPrincipal Long userId,
                                  @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateMyInfo(userId, request);
    }

    @Operation(summary = "비밀번호 변경")
    @PatchMapping("/me/password")
    public ChangePasswordResponse changePassword(@AuthenticationPrincipal Long userId,
                                                   @Valid @RequestBody ChangePasswordRequest request) {
        return userService.changePassword(userId, request);
    }
    @Operation(summary = "최근 찾던 상품 조회")
    @GetMapping("/recent-products")
    public RecentProductResponse getRecentProducts(@AuthenticationPrincipal Long userId) {

        return userService.getRecentProducts(userId);

    }

}
