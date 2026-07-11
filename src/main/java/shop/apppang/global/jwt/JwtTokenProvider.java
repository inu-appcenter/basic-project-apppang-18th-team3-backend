package shop.apppang.global.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

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
