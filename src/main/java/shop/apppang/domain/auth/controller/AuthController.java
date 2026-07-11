package shop.apppang.domain.auth.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shop.apppang.domain.auth.dto.request.LoginRequest;
import shop.apppang.domain.auth.dto.request.SignupRequest;
import shop.apppang.domain.auth.dto.response.EmailCheckResponse;
import shop.apppang.domain.auth.dto.response.LoginResponse;
import shop.apppang.domain.auth.dto.response.SignupResponse;
import shop.apppang.domain.auth.service.AuthService;

@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request){

        SignupResponse response = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-email")
    public ResponseEntity<EmailCheckResponse> checkEmail(
            @RequestParam
            @Email(message = "올바른 이메일 형식을 입력해주세요.")
            @NotBlank(message = "이메일을 입력해주세요.")
            String email) {

        return ResponseEntity.ok(authService.checkEmailAvailable(email));
    }
}
