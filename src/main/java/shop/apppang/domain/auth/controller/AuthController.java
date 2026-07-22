package shop.apppang.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shop.apppang.domain.auth.dto.request.FindEmailRequest;
import shop.apppang.domain.auth.dto.request.LoginRequest;
import shop.apppang.domain.auth.dto.request.PasswordResetVerifyRequest;
import shop.apppang.domain.auth.dto.request.ResetPasswordRequest;
import shop.apppang.domain.auth.dto.request.SignupRequest;
import shop.apppang.domain.auth.dto.response.EmailCheckResponse;
import shop.apppang.domain.auth.dto.response.FindEmailResponse;
import shop.apppang.domain.auth.dto.response.LoginResponse;
import shop.apppang.domain.auth.dto.response.LogoutResponse;
import shop.apppang.domain.auth.dto.response.PasswordResetVerifyResponse;
import shop.apppang.domain.auth.dto.response.ResetPasswordResponse;
import shop.apppang.domain.auth.dto.response.SignupResponse;
import shop.apppang.domain.auth.service.AuthService;

@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request){

        SignupResponse response = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이메일 중복 확인")
    @GetMapping("/check-email")
    public ResponseEntity<EmailCheckResponse> checkEmail(
            @RequestParam
            @Email(message = "올바른 이메일 형식을 입력해주세요.")
            @NotBlank(message = "이메일을 입력해주세요.")
            String email) {

        return ResponseEntity.ok(authService.checkEmailAvailable(email));
    }

    @Operation(summary = "아이디(이메일) 찾기")
    @PostMapping("/find-email")
    public ResponseEntity<FindEmailResponse> findEmail(@Valid @RequestBody FindEmailRequest request) {

        FindEmailResponse response = authService.findEmail(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 재설정 본인인증")
    @PostMapping("/password-reset-verify")
    public ResponseEntity<PasswordResetVerifyResponse> verifyPasswordReset(@Valid @RequestBody PasswordResetVerifyRequest request) {

        PasswordResetVerifyResponse response = authService.verifyForPasswordReset(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 재설정")
    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestBody ResetPasswordRequest request) {

        ResetPasswordResponse response = authService.resetPassword(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout() {

        LogoutResponse response = authService.logout();

        return ResponseEntity.ok(response);
    }
}
