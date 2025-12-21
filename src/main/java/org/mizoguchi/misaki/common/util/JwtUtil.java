package org.mizoguchi.misaki.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    // 定义一个密钥字符串，该密钥需足够长，例如256位以上。
    @Value("${misaki.jwt.secret}")
    private String JWT_SECRET;

    // 设置 token 过期时间（例如2小时）
    @Value("${misaki.jwt.expiration}")
    private long JWT_EXPIRATION_IN_MS;

    // 基于密钥字符串生成 SecretKey 对象，此处采用 HS256 算法，需要使用 Keys.hmacShaKeyFor() 确保密钥长度符合要求
    private SecretKey SECRET_KEY;

    @PostConstruct
    public void init() {
        this.SECRET_KEY = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    /**
     * 根据指定的 subject 生成一个 JWT token
     *
     * @param subject 可以是用户名或者用户 ID
     * @return token 字符串
     */
    public String generateToken(String subject, Integer roleCode) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_IN_MS);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(subject)           // 设置 token 主题
                .issuedAt(now)              // 设置 token 签发时间
                .expiration(expiryDate)     // 设置 token 过期时间
                .claim("role", roleCode) // 设置 token 角色代号
                .signWith(SECRET_KEY)       // 使用 HS256 算法进行签名，并传入密钥对象
                .compact();                 // 生成紧凑的 token 字符串
    }

    /**
     * 解析 token 获取所有声明（Claims）
     *
     * @param token JWT token 字符串
     * @return Claims 对象，包含所有解析出来的声明信息
     * @throws ExpiredJwtException      如果 token 过期
     * @throws UnsupportedJwtException  如果 token 格式不支持
     * @throws MalformedJwtException    如果 token 格式异常
     * @throws SignatureException       如果 token 签名不匹配
     * @throws IllegalArgumentException 如果 token 为空或非法
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)  // 设置签名密钥
                .build()
                .parseSignedClaims(token)      // 解析 token
                .getPayload();                 // 获取其中的声明部分
    }

    /**
     * 从 token 中提取 id
     *
     * @param token JWT token 字符串
     * @return JWT id
     */
    public String getIdFromToken(String token) {
        return getClaimsFromToken(token).getId();
    }


    /**
     * 从 token 中提取 subject（例如用户名或者用户ID）
     *
     * @param token JWT token 字符串
     * @return token 中存储的 subject
     */
    public String getSubjectFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 从 token 中提取 roleCode
     *
     * @param token JWT token 字符串
     * @return roleCode
     */
    public Integer getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", Integer.class);
    }

    /**
     * 校验 token 的有效性，包括签名是否正确和是否过期
     *
     * @param token JWT token 字符串
     * @return 如果 token 有效则返回 true，否则返回 false
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (ExpiredJwtException ex) {
            System.out.println("JWT 已经过期: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            System.out.println("不支持的 JWT: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            System.out.println("格式错误的 JWT: " + ex.getMessage());
        } catch (SignatureException ex) {
            System.out.println("JWT 签名验证失败: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT 为空或者非法: " + ex.getMessage());
        }
        return false;
    }

    // 如果需要更多功能，比如刷新 token、获取 token 中的其他信息，都可以在此进行扩展
}
