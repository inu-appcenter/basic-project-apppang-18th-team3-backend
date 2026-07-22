package shop.apppang.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import shop.apppang.domain.auth.exception.DuplicateEmailException;
import shop.apppang.domain.auth.exception.MemberNotFoundException;
import shop.apppang.domain.user.dto.request.ChangePasswordRequest;
import shop.apppang.domain.user.dto.request.UpdateUserRequest;
import shop.apppang.domain.user.dto.response.ChangePasswordResponse;
import shop.apppang.domain.user.dto.response.UserMeResponse;
import shop.apppang.domain.user.dto.response.UserResponse;
import shop.apppang.domain.user.entity.User;
import shop.apppang.domain.user.exception.InvalidCurrentPasswordException;
import shop.apppang.domain.user.exception.InvalidNameException;
import shop.apppang.domain.user.repository.UserRepository;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.product.entity.ProductImageEntity;
import shop.apppang.domain.product.repository.ProductImageRepository;
import shop.apppang.domain.product.repository.ProductRepository;
import shop.apppang.domain.search.entity.SearchHistory;
import shop.apppang.domain.search.repository.SearchHistoryRepository;
import shop.apppang.domain.user.dto.RecentProductResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

        if (request.getPhoneNumber() != null) {
            user.changePhoneNumber(request.getPhoneNumber());
        }

        return UserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    @Transactional(readOnly = true)
    public UserMeResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다"));

        return UserMeResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .appMoney(user.getAppMoney())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public ChangePasswordResponse changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCurrentPasswordException();
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));

        return ChangePasswordResponse.builder()
                .message("비밀번호가 변경되었습니다")
                .build();
    }

    private final SearchHistoryRepository searchHistoryRepository;

    private final ProductRepository productRepository;

    private final ProductImageRepository productImageRepository;

    @Transactional(readOnly = true)
    public RecentProductResponse getRecentProducts(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));

        return searchHistoryRepository.findTopByUserOrderBySearchedAtDesc(user)
                .map(SearchHistory::getKeyword)
                .map(this::findRecentProductsByKeyword)
                .orElseGet(() -> new RecentProductResponse(List.of()));
    }

    private RecentProductResponse findRecentProductsByKeyword(String keyword) {

        List<ProductEntity> products = productRepository
                .findTop20ByNameContainingIgnoreCaseAndIsActiveTrueOrderByIdAsc(keyword);

        List<Long> productIds = products.stream()
                .map(ProductEntity::getId)
                .toList();

        Map<Long, String> mainImageUrlByProductId = productImageRepository
                .findByProductIdInAndIsMainTrue(productIds).stream()
                .collect(Collectors.toMap(
                        image -> image.getProduct().getId(),
                        ProductImageEntity::getImageUrl,
                        (existing, replacement) -> existing
                ));

        List<RecentProductResponse.Item> items = products.stream()
                .map(product -> new RecentProductResponse.Item(
                        product.getId(),
                        product.getName(),
                        mainImageUrlByProductId.get(product.getId()),
                        product.getPrice()
                ))
                .toList();

        return new RecentProductResponse(items);
    }

}
