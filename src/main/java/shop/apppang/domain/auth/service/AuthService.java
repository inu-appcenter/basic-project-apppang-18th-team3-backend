// domain/auth/service/AuthService.java
package shop.apppang.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.apppang.domain.auth.dto.request.FindEmailRequest;
import shop.apppang.domain.auth.dto.request.LoginRequest;
import shop.apppang.domain.auth.dto.request.PasswordResetVerifyRequest;
import shop.apppang.domain.auth.dto.request.ResetPasswordRequest;
import shop.apppang.domain.auth.dto.request.SignupRequest;
import shop.apppang.domain.auth.dto.response.EmailCheckResponse;
import shop.apppang.domain.auth.dto.response.FindEmailResponse;
import shop.apppang.domain.auth.dto.response.LoginResponse;
import shop.apppang.domain.auth.dto.response.PasswordResetVerifyResponse;
import shop.apppang.domain.auth.dto.response.ResetPasswordResponse;
import shop.apppang.domain.auth.dto.response.SignupResponse;
import shop.apppang.domain.auth.exception.DuplicateEmailException;
import shop.apppang.domain.auth.exception.InvalidCredentialsException;
import shop.apppang.domain.auth.exception.InvalidPasswordFormatException;
import shop.apppang.domain.auth.exception.InvalidResetTokenException;
import shop.apppang.domain.auth.exception.MemberNotFoundException;
import shop.apppang.domain.auth.util.EmailMaskingUtil;
import shop.apppang.domain.user.entity.User;
import shop.apppang.domain.user.repository.UserRepository;
import shop.apppang.global.jwt.JwtTokenProvider;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("이미 가입된 이메일입니다");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);

        return SignupResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());

        return LoginResponse.builder()
                .token(token)
                .user(LoginResponse.UserInfo.builder()
                        .userId(user.getId())
                        .name(user.getName())
                        .build())
                .build();
    }

    public EmailCheckResponse checkEmailAvailable(String email) {
        boolean exists = userRepository.existsByEmail(email);

        return EmailCheckResponse.builder()
                .available(!exists)
                .build();
    }

    @Transactional(readOnly = true)
    public FindEmailResponse findEmail(FindEmailRequest request) {

        List<User> users = userRepository.findByNameAndPhoneNumber(
                request.getName(),
                request.getPhoneNumber()
        );

        if (users.isEmpty()) {
            throw new MemberNotFoundException("일치하는 회원 정보를 찾을 수 없습니다.");
        }

        List<String> maskedEmails = users.stream()
                .map(user -> EmailMaskingUtil.mask(user.getEmail()))
                .toList();

        return FindEmailResponse.builder()
                .emails(maskedEmails)
                .build();
    }

    @Transactional(readOnly = true)
    public PasswordResetVerifyResponse verifyForPasswordReset(PasswordResetVerifyRequest request) {

        User user = userRepository.findByEmailAndNameAndPhoneNumber(
                request.getEmail(),
                request.getName(),
                request.getPhoneNumber()
        ).orElseThrow(() -> new MemberNotFoundException("일치하는 회원 정보를 찾을 수 없습니다."));

        String resetToken = jwtTokenProvider.generateResetToken(user.getId());

        return PasswordResetVerifyResponse.builder()
                .resetToken(resetToken)
                .build();
    }

    @Transactional
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {

        Long userId;
        try {
            userId = jwtTokenProvider.validateResetTokenAndGetUserId(request.getResetToken());
        } catch (IllegalArgumentException e) {
            throw new InvalidResetTokenException();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(InvalidResetTokenException::new);

        if (request.getNewPassword() == null || !PASSWORD_PATTERN.matcher(request.getNewPassword()).matches()) {
            throw new InvalidPasswordFormatException();
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));

        return ResetPasswordResponse.builder()
                .message("비밀번호가 재설정되었습니다")
                .build();
    }
}