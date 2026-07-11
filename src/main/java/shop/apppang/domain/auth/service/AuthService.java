// domain/auth/service/AuthService.java
package shop.apppang.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.apppang.domain.auth.dto.request.LoginRequest;
import shop.apppang.domain.auth.dto.request.SignupRequest;
import shop.apppang.domain.auth.dto.response.EmailCheckResponse;
import shop.apppang.domain.auth.dto.response.LoginResponse;
import shop.apppang.domain.auth.dto.response.SignupResponse;
import shop.apppang.domain.auth.exception.DuplicateEmailException;
import shop.apppang.domain.auth.exception.InvalidCredentialsException;
import shop.apppang.domain.user.entity.User;
import shop.apppang.domain.user.repository.UserRepository;
import shop.apppang.global.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

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
}