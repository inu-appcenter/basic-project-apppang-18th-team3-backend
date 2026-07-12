package shop.apppang.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import shop.apppang.domain.auth.exception.DuplicateEmailException;
import shop.apppang.domain.auth.exception.MemberNotFoundException;
import shop.apppang.domain.user.dto.request.UpdateUserRequest;
import shop.apppang.domain.user.dto.response.UserResponse;
import shop.apppang.domain.user.entity.User;
import shop.apppang.domain.user.exception.InvalidNameException;
import shop.apppang.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse updateMyInfo(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다"));

        if (request.getName() != null) {
            if (!StringUtils.hasText(request.getName())) {
                throw new InvalidNameException();
            }
            user.changeName(request.getName());
        }

        if (request.getEmail() != null) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
                throw new DuplicateEmailException("이미 사용 중인 이메일입니다");
            }
            user.changeEmail(request.getEmail());
        }

        if (request.getPhone() != null) {
            user.changePhoneNumber(request.getPhone());
        }

        return UserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhoneNumber())
                .build();
    }
}
