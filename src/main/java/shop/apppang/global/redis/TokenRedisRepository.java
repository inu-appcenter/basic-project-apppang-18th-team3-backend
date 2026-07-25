package shop.apppang.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TokenRedisRepository {

    private static final String RESET_TOKEN_PREFIX = "password-reset:";
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;

    public void saveResetToken(String resetToken, Long userId, Duration ttl) {
        redisTemplate.opsForValue().set(RESET_TOKEN_PREFIX + resetToken, String.valueOf(userId), ttl);
    }

    public boolean existsResetToken(String resetToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(RESET_TOKEN_PREFIX + resetToken));
    }

    public void deleteResetToken(String resetToken) {
        redisTemplate.delete(RESET_TOKEN_PREFIX + resetToken);
    }

    public void blacklistAccessToken(String jti, Duration ttl) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + jti, "logout", ttl);
    }

    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + jti));
    }

    public void saveRefreshToken(Long userId, String refreshToken, Duration ttl) {
        redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + userId, refreshToken, ttl);
    }

    public Optional<String> findRefreshToken(Long userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId));
    }

    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }
}
