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

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String RESET_TOKEN_PREFIX = "tmp_";

    private final JwtProperties jwtProperties;

    public String generateResetToken(Long userId) {
        try {
            Date now = new Date();
            Date expiration = new Date(now.getTime() + jwtProperties.getResetTokenExpiration());

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(String.valueOf(userId))
                    .claim("purpose", "password-reset")
                    .issueTime(now)
                    .expirationTime(expiration)
                    .build();

            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            signedJWT.sign(new MACSigner(jwtProperties.getSecret().getBytes()));

            return RESET_TOKEN_PREFIX + signedJWT.serialize();
        } catch (JOSEException e){
            throw new IllegalStateException("JWT 서명에 실패했습니다.", e);
        }
    }

    public Long validateResetTokenAndGetUserId(String token) {
        if (token == null || !token.startsWith(RESET_TOKEN_PREFIX)) {
            throw new IllegalArgumentException("유효하지 않은 리셋 토큰입니다.");
        }

        JWTClaimsSet claims = parseAndVerify(token.substring(RESET_TOKEN_PREFIX.length()), "유효하지 않은 리셋 토큰입니다.");

        if (!"password-reset".equals(claims.getClaim("purpose"))) {
            throw new IllegalArgumentException("유효하지 않은 리셋 토큰입니다.");
        }

        if (isExpired(claims)) {
            throw new IllegalArgumentException("만료된 리셋 토큰입니다.");
        }

        return Long.valueOf(claims.getSubject());
    }

    public Long validateAccessTokenAndGetUserId(String token) {
        if (token == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        JWTClaimsSet claims = parseAndVerify(token, "유효하지 않은 토큰입니다.");

        if (isExpired(claims)) {
            throw new IllegalArgumentException("만료된 토큰입니다.");
        }

        return Long.valueOf(claims.getSubject());
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

    public String generateAccessToken(Long userId, String email){
        try{
            Date now = new Date();
            Date expiration = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(String.valueOf(userId))
                    .claim("email", email)
                    .issueTime(now)
                    .expirationTime(expiration)
                    .build();

            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            signedJWT.sign(new MACSigner(jwtProperties.getSecret().getBytes()));

            return signedJWT.serialize();
        } catch (JOSEException e){
            throw new IllegalStateException("JWT 서명에 실패했습니다.", e);
        }
    }
}
