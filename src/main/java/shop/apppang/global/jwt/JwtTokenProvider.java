package shop.apppang.global.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String RESET_TOKEN_PREFIX = "tmp_";
    private static final String RESET_PURPOSE = "password-reset";
    private static final String REFRESH_PURPOSE = "refresh-token";

    private final JwtProperties jwtProperties;

    public record AccessTokenClaims(Long userId, String jti, Date expiration) {}

    public String generateAccessToken(Long userId, String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .jwtID(UUID.randomUUID().toString())
                .issueTime(now)
                .expirationTime(expiration)
                .build();

        return sign(claimsSet);
    }

    public String generateResetToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getResetTokenExpiration());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(String.valueOf(userId))
                .claim("purpose", RESET_PURPOSE)
                .jwtID(UUID.randomUUID().toString())
                .issueTime(now)
                .expirationTime(expiration)
                .build();

        return RESET_TOKEN_PREFIX + sign(claimsSet);
    }

    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(String.valueOf(userId))
                .claim("purpose", REFRESH_PURPOSE)
                .jwtID(UUID.randomUUID().toString())
                .issueTime(now)
                .expirationTime(expiration)
                .build();

        return sign(claimsSet);
    }

    public AccessTokenClaims validateAccessTokenAndGetClaims(String token) {
        if (token == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        JWTClaimsSet claims = parseAndVerify(token, "유효하지 않은 토큰입니다.");

        // 리셋/리프레시 토큰은 반드시 purpose 클레임을 갖고 있으므로, 이게 있으면 액세스 토큰이 아니다.
        if (claims.getClaim("purpose") != null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        if (isExpired(claims)) {
            throw new IllegalArgumentException("만료된 토큰입니다.");
        }

        return new AccessTokenClaims(Long.valueOf(claims.getSubject()), claims.getJWTID(), claims.getExpirationTime());
    }

    public Long validateAccessTokenAndGetUserId(String token) {
        return validateAccessTokenAndGetClaims(token).userId();
    }

    public Long validateResetTokenAndGetUserId(String token) {
        if (token == null || !token.startsWith(RESET_TOKEN_PREFIX)) {
            throw new IllegalArgumentException("유효하지 않은 리셋 토큰입니다.");
        }

        JWTClaimsSet claims = parseAndVerify(token.substring(RESET_TOKEN_PREFIX.length()), "유효하지 않은 리셋 토큰입니다.");

        if (!RESET_PURPOSE.equals(claims.getClaim("purpose"))) {
            throw new IllegalArgumentException("유효하지 않은 리셋 토큰입니다.");
        }

        if (isExpired(claims)) {
            throw new IllegalArgumentException("만료된 리셋 토큰입니다.");
        }

        return Long.valueOf(claims.getSubject());
    }

    public Long validateRefreshTokenAndGetUserId(String token) {
        if (token == null) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        JWTClaimsSet claims = parseAndVerify(token, "유효하지 않은 리프레시 토큰입니다.");

        if (!REFRESH_PURPOSE.equals(claims.getClaim("purpose"))) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        if (isExpired(claims)) {
            throw new IllegalArgumentException("만료된 리프레시 토큰입니다.");
        }

        return Long.valueOf(claims.getSubject());
    }

    private String sign(JWTClaimsSet claimsSet) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            signedJWT.sign(new MACSigner(jwtProperties.getSecret().getBytes()));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("JWT 서명에 실패했습니다.", e);
        }
    }

    private JWTClaimsSet parseAndVerify(String jwt, String invalidMessage) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwt);

            if (!signedJWT.verify(new MACVerifier(jwtProperties.getSecret().getBytes()))) {
                throw new IllegalArgumentException(invalidMessage);
            }

            return signedJWT.getJWTClaimsSet();
        } catch (ParseException | JOSEException e) {
            throw new IllegalArgumentException(invalidMessage, e);
        }
    }

    private boolean isExpired(JWTClaimsSet claims) {
        return claims.getExpirationTime() == null || claims.getExpirationTime().before(new Date());
    }
}
