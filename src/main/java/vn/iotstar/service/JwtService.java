package vn.iotstar.service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class JwtService {

    private final byte[] secretKey;
    private final long jwtExpiration;

    public JwtService(
            @Value("${security.jwt.secret-key}") String secretKey,
            @Value("${security.jwt.expiration-time}") long jwtExpiration) {

        this.secretKey =
            secretKey.getBytes(StandardCharsets.UTF_8);

        this.jwtExpiration = jwtExpiration;

        if (this.secretKey.length < 32) {
            throw new IllegalArgumentException(
                "JWT secret phải có ít nhất 32 bytes để sử dụng HS256"
            );
        }
    }

    public String generateToken(UserDetails userDetails) {

        Date now = new Date();
        Date expiration =
            new Date(now.getTime() + jwtExpiration);

        JWTClaimsSet claims =
            new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issueTime(now)
                .expirationTime(expiration)
                .claim(
                    "username",
                    userDetails.getUsername()
                )
                .build();

        JWSHeader header =
            new JWSHeader.Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();

        SignedJWT signedJWT =
            new SignedJWT(header, claims);

        try {

            MACSigner signer =
                new MACSigner(secretKey);

            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (JOSEException e) {

            throw new IllegalStateException(
                "Không thể tạo JWT",
                e
            );
        }
    }

    public String extractUsername(String token) {

        SignedJWT signedJWT = parseAndVerify(token);

        try {

            Date expiration =
                signedJWT
                    .getJWTClaimsSet()
                    .getExpirationTime();

            if (expiration == null ||
                    expiration.before(new Date())) {

                throw new IllegalArgumentException(
                    "JWT đã hết hạn"
                );
            }

            return signedJWT
                .getJWTClaimsSet()
                .getSubject();

        } catch (ParseException e) {

            throw new IllegalArgumentException(
                "Không đọc được claims của JWT",
                e
            );
        }
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        try {

            SignedJWT signedJWT =
                parseAndVerify(token);

            JWTClaimsSet claims =
                signedJWT.getJWTClaimsSet();

            String username =
                claims.getSubject();

            Date expiration =
                claims.getExpirationTime();

            return username != null
                && username.equals(
                    userDetails.getUsername()
                )
                && expiration != null
                && expiration.after(new Date());

        } catch (Exception e) {

            return false;
        }
    }

    private SignedJWT parseAndVerify(String token) {

        try {

            SignedJWT signedJWT =
                SignedJWT.parse(token);

            if (!JWSAlgorithm.HS256.equals(
                    signedJWT
                        .getHeader()
                        .getAlgorithm())) {

                throw new IllegalArgumentException(
                    "JWT không sử dụng HS256"
                );
            }

            MACVerifier verifier =
                new MACVerifier(secretKey);

            if (!signedJWT.verify(verifier)) {

                throw new IllegalArgumentException(
                    "Chữ ký JWT không hợp lệ"
                );
            }

            return signedJWT;

        } catch (ParseException e) {

            throw new IllegalArgumentException(
                "JWT không đúng định dạng",
                e
            );

        } catch (JOSEException e) {

            throw new IllegalStateException(
                "Không thể xác minh JWT",
                e
            );
        }
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }
}