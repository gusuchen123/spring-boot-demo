package com.gusuchen.rbac.security.util;

import cn.hutool.core.util.StrUtil;
import com.gusuchen.rbac.security.common.Consts;
import com.gusuchen.rbac.security.common.Status;
import com.gusuchen.rbac.security.config.JwtConfig;
import com.gusuchen.rbac.security.exception.SecurityException;
import com.gusuchen.rbac.security.vo.UserPrincipal;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.annotation.Resource;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * JWT工具类，主要功能：生成JWT并存入redis、解析JWT并校验其准确性、从Request的Header中获取JWT
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-16 15:51
 */
@Slf4j
@Configuration
public class JwtUtil {
    private static final String JWT_TOKEN_HEADER = "Authorization";
    private static final String JWT_TOKEN_HEAD = "Bearer ";

    @Resource
    private JwtConfig jwtConfig;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 创建 JWT
     *
     * @param rememberMe  记住我 {@link JwtConfig#getRemember()}
     * @param id          用户ID
     * @param subject     用户名称
     * @param roles       用户角色
     * @param authorities 用户权限
     * @return JWT
     */
    public String createJwt(Boolean rememberMe, Long id, String subject, List<String> roles, Collection<? extends GrantedAuthority> authorities) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtConfig.getKey());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder jwtBuilder = Jwts.builder()
                .setId(id.toString())
                .setSubject(subject)
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, signingKey)
                .claim("roles", roles)
                .claim("authorities", authorities);

        //if it has been specified, let's add the expiration
        long ttlMillis = rememberMe ? jwtConfig.getRemember() : jwtConfig.getTtlMillis();
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            jwtBuilder.setExpiration(new Date(expMillis));
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        String jwt = jwtBuilder.compact();

        // 将生成的jwt保存至redis
        stringRedisTemplate.opsForValue().set(Consts.REDIS_JWT_KEY_PREFIX + subject, jwt, ttlMillis, TimeUnit.MILLISECONDS);
        return jwt;
    }

    /**
     * 创建 JWT
     *
     * @param authentication 认证信息
     * @param rememberMe     记住我
     * @return JWT
     */
    public String createJwt(Authentication authentication, Boolean rememberMe) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return createJwt(rememberMe, userPrincipal.getId(), userPrincipal.getUsername(), userPrincipal.getRoles(), userPrincipal.getAuthorities());
    }

    /**
     * 解析JWT
     *
     * @param jwt Jwt
     * @return {@link Claims}
     */
    public Claims parseJwt(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(jwtConfig.getKey()))
                    .parseClaimsJws(jwt)
                    .getBody();

            // 获得redisKey
            String username = claims.getSubject();
            String redisKey = Consts.REDIS_JWT_KEY_PREFIX + username;

            // 校验Redis中的JWT是否存在
            Long expire = stringRedisTemplate.getExpire(redisKey, TimeUnit.MILLISECONDS);
            if (Objects.isNull(expire) || expire <= 0) {
                throw new SecurityException(Status.TOKEN_EXPIRED);
            }

            // 校验redis中的jwt是否与当前的一致，不一致则代表用户已注销/用户在不同的设备登录，均代表jwt已过期
            String redisToken = stringRedisTemplate.opsForValue().get(redisKey);
            if (!StrUtil.equals(jwt, redisToken)) {
                throw new SecurityException(Status.TOKEN_OUT_OF_CTRL);
            }
            return claims;
        } catch (ExpiredJwtException e) {
            log.error("Token 已过期");
            throw new SecurityException(Status.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.error("不支持的 Token");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        } catch (MalformedJwtException e) {
            log.error("Token 无效");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        } catch (SignatureException e) {
            log.error("无效的 Token 签名");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        } catch (IllegalArgumentException e) {
            log.error("Token 参数不存在");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        }
    }

    /**
     * 设置JWT过期
     *
     * @param request {@link HttpServletRequest}
     */
    public void invalidateJwt(HttpServletRequest request) {
        String jwt = getJwtFromRequest(request);
        String username = getUsernameFromJwt(jwt);

        // 从redis中清除JWT
        stringRedisTemplate.delete(Consts.REDIS_JWT_KEY_PREFIX + username);
    }

    /**
     * 根据 jwt 获取用户名
     *
     * @param jwt JWT
     * @return 用户名
     */
    public String getUsernameFromJwt(String jwt) {
        Claims claims = parseJwt(jwt);
        return claims.getSubject();
    }

    /**
     * 从 request 的 header 中获取 JWT
     *
     * @param request 请求
     * @return JWT
     */
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(JWT_TOKEN_HEADER);
        if (StrUtil.isNotBlank(bearerToken) && bearerToken.startsWith(JWT_TOKEN_HEAD)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
