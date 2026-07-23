package shop.apppang.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import shop.apppang.global.exception.ErrorResponse;

@Tag(name = "인증")
@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "이메일 형식 오류 / 비밀번호 형식 오류 / 필수 약관 미동의 중 하나",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"올바른 이메일 형식을 입력해주세요\"}"))),
            @ApiResponse(responseCode = "409", description = "이미 가입된 이메일",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"이미 가입된 이메일입니다\"}")))
    })
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request){

        SignupResponse response = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "이메일/비밀번호 누락",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"이메일과 비밀번호를 입력해주세요\"}"))),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"이메일 또는 비밀번호가 올바르지 않습니다\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이메일 중복 확인")
    @ApiResponse(responseCode = "400", description = "이메일 형식 오류 / 이메일 누락 중 하나",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"error\": \"이메일 형식이 올바르지 않습니다.\"}")))
    @GetMapping("/check-email")
    public ResponseEntity<EmailCheckResponse> checkEmail(
            @Parameter(description = "중복 확인할 이메일", required = true, example = "a@a.com")
            @RequestParam
            @Email(message = "올바른 이메일 형식을 입력해주세요.")
            @NotBlank(message = "이메일을 입력해주세요.")
            String email) {

        return ResponseEntity.ok(authService.checkEmailAvailable(email));
    }

    @Operation(summary = "아이디(이메일) 찾기")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "이름 누락 / 전화번호 형식 오류 중 하나",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"이름을 입력해주세요\"}"))),
            @ApiResponse(responseCode = "404", description = "일치하는 회원 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"일치하는 회원 정보를 찾을 수 없습니다\"}")))
    })
    @PostMapping("/find-email")
    public ResponseEntity<FindEmailResponse> findEmail(@Valid @RequestBody FindEmailRequest request) {

        FindEmailResponse response = authService.findEmail(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 재설정 본인인증")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "이메일 형식 오류 / 휴대폰 번호 형식 오류 / 이름 누락 중 하나",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"이메일 형식이 올바르지 않습니다\"}"))),
            @ApiResponse(responseCode = "404", description = "일치하는 회원 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"일치하는 회원 정보를 찾을 수 없습니다\"}")))
    })
    @PostMapping("/password-reset-verify")
    public ResponseEntity<PasswordResetVerifyResponse> verifyPasswordReset(@Valid @RequestBody PasswordResetVerifyRequest request) {

        PasswordResetVerifyResponse response = authService.verifyForPasswordReset(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 재설정")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "비밀번호 형식 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"비밀번호는 8자 이상, 영문+숫자 조합이어야 합니다\"}"))),
            @ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 재설정 토큰 (토큰 검증이 우선 적용됨)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"유효하지 않거나 만료된 요청입니다\"}")))
    })
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
