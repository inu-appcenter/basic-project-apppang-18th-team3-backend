package shop.apppang.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.apppang.domain.user.dto.RecentProductResponse;
import shop.apppang.domain.user.service.UserService;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/recent-products")
    public RecentProductResponse getRecentProducts(@AuthenticationPrincipal Long userId) {

        return userService.getRecentProducts(userId);

    }

}
