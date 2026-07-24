// domain/auth/service/AuthService.java
package shop.apppang.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.apppang.domain.auth.dto.request.FindEmailRequest;
import shop.apppang.domain.auth.dto.request.LoginRequest;
import shop.apppang.domain.auth.dto.request.PasswordResetVerifyRequest;
import shop.apppang.domain.auth.dto.request.ReissueRequest;
import shop.apppang.domain.auth.dto.request.ResetPasswordRequest;
import shop.apppang.domain.auth.dto.request.SignupRequest;
import shop.apppang.domain.auth.dto.response.EmailCheckResponse;
import shop.apppang.domain.auth.dto.response.FindEmailResponse;
import shop.apppang.domain.auth.dto.response.LoginResponse;
import shop.apppang.domain.auth.dto.response.LogoutResponse;
import shop.apppang.domain.auth.dto.response.PasswordResetVerifyResponse;
import shop.apppang.domain.auth.dto.response.ReissueResponse;
import shop.apppang.domain.auth.dto.response.ResetPasswordResponse;
import shop.apppang.domain.auth.dto.response.SignupResponse;
import shop.apppang.domain.auth.exception.DuplicateEmailException;
import shop.apppang.domain.auth.exception.InvalidCredentialsException;
import shop.apppang.domain.auth.exception.InvalidPasswordFormatException;
import shop.apppang.domain.auth.exception.InvalidRefreshTokenException;
import shop.apppang.domain.auth.exception.InvalidResetTokenException;
import shop.apppang.domain.auth.exception.MemberNotFoundException;
import shop.apppang.domain.auth.util.EmailMaskingUtil;
import shop.apppang.domain.user.entity.User;
import shop.apppang.domain.user.repository.UserRepository;
import shop.apppang.global.jwt.JwtProperties;
import shop.apppang.global.jwt.JwtTokenProvider;
import shop.apppang.global.redis.TokenRedisRepository;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    private static final String BEARER_PREFIX = "Bearer ";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final TokenRedisRepository tokenRedisRepository;

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

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        tokenRedisRepository.saveRefreshToken(
                user.getId(), refreshToken, Duration.ofMillis(jwtProperties.getRefreshTokenExpiration()));

        return LoginResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(LoginResponse.UserInfo.builder()
                        .userId(user.getId())
                        .name(user.getName())
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public ReissueResponse reissue(ReissueRequest request) {

        Long userId;
        try {
            userId = jwtTokenProvider.validateRefreshTokenAndGetUserId(request.getRefreshToken());
        } catch (IllegalArgumentException e) {
            throw new InvalidRefreshTokenException();
        }

        String storedRefreshToken = tokenRedisRepository.findRefreshToken(userId)
                .orElseThrow(InvalidRefreshTokenException::new);

        // 저장된 값과 다르면 이미 rotation으로 폐기된(재사용된) 리프레시 토큰이다.
        if (!storedRefreshToken.equals(request.getRefreshToken())) {
            throw new InvalidRefreshTokenException();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(InvalidRefreshTokenException::new);

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        tokenRedisRepository.saveRefreshToken(
                user.getId(), newRefreshToken, Duration.ofMillis(jwtProperties.getRefreshTokenExpiration()));

        return ReissueResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
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
        tokenRedisRepository.saveResetToken(
                resetToken, user.getId(), Duration.ofMillis(jwtProperties.getResetTokenExpiration()));

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

        // JWT 자체는 만료 전까지 계속 유효하므로, Redis에 남아있는지로 "아직 쓰지 않은 토큰"인지 별도 확인한다.
        if (!tokenRedisRepository.existsResetToken(request.getResetToken())) {
            throw new InvalidResetTokenException();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(InvalidResetTokenException::new);

        if (request.getNewPassword() == null || !PASSWORD_PATTERN.matcher(request.getNewPassword()).matches()) {
            throw new InvalidPasswordFormatException();
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
        tokenRedisRepository.deleteResetToken(request.getResetToken());

        return ResetPasswordResponse.builder()
                .message("비밀번호가 재설정되었습니다")
                .build();
    }

    public LogoutResponse logout(String authorizationHeader) {
        String accessToken = stripBearerPrefix(authorizationHeader);

        // /api/auth/logout은 SecurityConfig에서 인증을 요구하므로, 여기 도달했다면 이미 유효한 토큰이다.
        JwtTokenProvider.AccessTokenClaims claims = jwtTokenProvider.validateAccessTokenAndGetClaims(accessToken);

        long remainingMillis = claims.expiration().getTime() - System.currentTimeMillis();
        if (remainingMillis > 0) {
            tokenRedisRepository.blacklistAccessToken(claims.jti(), Duration.ofMillis(remainingMillis));
        }
        tokenRedisRepository.deleteRefreshToken(claims.userId());

        return LogoutResponse.builder()
                .message("로그아웃되었습니다")
                .build();
    }

    private String stripBearerPrefix(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }
        return authorizationHeader;
    }
}