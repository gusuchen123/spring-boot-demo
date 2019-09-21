package com.gusuchen.rbac.security.util;

import com.google.common.collect.Maps;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-17 15:18
 */
@Slf4j
public class JwtUtilTest {
    private static final String JWT_SECRET = "3edcVFR$";

    /**
     * JWT 生成
     *
     * @param claims 声明部分
     * @return JWT
     */
    private static String createJWT(Map<String, Object> claims, long ttlMillis) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(JWT_SECRET);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        long expMillis = nowMillis + ttlMillis;

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, signingKey)
                .setExpiration(new Date(expMillis))
                .compact();
    }

    private static Claims parseJWT(String jwt) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(JWT_SECRET)
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    public static void main(String[] args) {
        Map<String, Object> claims = Maps.newHashMap();
        claims.put("CLAIM_KEY_USERNAME", "gusuchen");
        claims.put("CLAIM_KEY_ROLES", "roles");
        claims.put("CLAIM_KEY_AUTHORITIES", "authorities");

        // 019-09-18 19:44:39
        String jwt = createJWT(claims, 60000L);
        log.error("jwt:{}", jwt);

        Claims convertToClaims = parseJWT(jwt);
        log.error("claims:{}", convertToClaims.toString());
    }
}
