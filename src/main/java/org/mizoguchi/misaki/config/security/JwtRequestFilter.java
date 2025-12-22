package org.mizoguchi.misaki.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.RedisConstant;
import org.mizoguchi.misaki.common.constant.WebConstant;
import org.mizoguchi.misaki.common.enumeration.AuthRoleEnum;
import org.mizoguchi.misaki.common.exception.UserNotExistsException;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.common.util.JwtUtil;
import org.mizoguchi.misaki.service.common.impl.UserDetailsServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        final String timestampHeader = request.getHeader(WebConstant.HEADER_TIMESTAMP);
        final String nonceHeader = request.getHeader(WebConstant.HEADER_NONCE);
        final String authHeader = request.getHeader(WebConstant.HEADER_AUTHORIZATION);

        if (request.getRequestURI().startsWith("/api/swagger-ui") || request.getRequestURI().startsWith("/api/v3/api-docs")){
            chain.doFilter(request, response);
            return;
        }

        if (!StringUtils.hasText(timestampHeader) || !StringUtils.hasText(nonceHeader)) {
            log.warn("{} | IP={} | URI={} | Method={}",
                    FailMessageConstant.REQUEST_MISSING_HEADERS, request.getRemoteAddr(), request.getRequestURI(), request.getMethod());
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, 40001, FailMessageConstant.REQUEST_MISSING_HEADERS);
            return;
        }

        long timestamp;
        try{
            timestamp = Long.parseLong(timestampHeader);
        }catch (NumberFormatException e){
            log.warn("{} | IP={} | URI={} | Method={}",
                    FailMessageConstant.REQUEST_MISSING_HEADERS, request.getRemoteAddr(), request.getRequestURI(), request.getMethod());
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, 40001, FailMessageConstant.REQUEST_MISSING_HEADERS);
            return;
        }

        if (Math.abs(System.currentTimeMillis() - timestamp) > WebConstant.REQUEST_EXPIRE_TIME){
            log.warn("{} | IP={} | URI={} | Method={}",
                    FailMessageConstant.REQUEST_EXPIRED, request.getRemoteAddr(), request.getRequestURI(), request.getMethod());
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, 40002, FailMessageConstant.REQUEST_EXPIRED);
            return;
        }

        String redisKey = RedisConstant.NONCE + nonceHeader;

        if (redisTemplate.hasKey(redisKey)) {
            log.warn("{} | IP={} | URI={} | Method={}",
                    FailMessageConstant.REPLAY_ATTACK_DETECTED, request.getRemoteAddr(), request.getRequestURI(), request.getMethod());
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, 40003, FailMessageConstant.REPLAY_ATTACK_DETECTED);
            return;
        }

        redisTemplate.opsForValue().set(redisKey, timestampHeader, Duration.ofMillis(WebConstant.REQUEST_EXPIRE_TIME));

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(WebConstant.BEARER_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String jwtId;
        String userId;
        Integer roleCode;

        try {
            jwtId = jwtUtil.getIdFromToken(jwt);
            userId = jwtUtil.getSubjectFromToken(jwt);
            roleCode = jwtUtil.getRoleFromToken(jwt);
            if (redisTemplate.hasKey(RedisConstant.BLOCKED_JWT + jwtId)) {
                throw new ExpiredJwtException(null, null, null);
            }
        } catch (ExpiredJwtException e) {
            log.warn("{} | IP={} | URI={} | Method={} | Exception={}",
                    FailMessageConstant.JWT_EXPIRED, request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getClass().getSimpleName());
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, 40102, FailMessageConstant.JWT_EXPIRED);
            return;
        } catch (Exception e) {
            log.warn("{} | IP={} | URI={} | Method={} | Exception={}",
                    FailMessageConstant.INVALID_JWT, request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getClass().getSimpleName());
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, 40103, FailMessageConstant.INVALID_JWT);
            return;
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(userId);
                customUserDetails.setJwtId(jwtId);

                AuthRoleEnum authRoleEnum = AuthRoleEnum.fromCode(roleCode);
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(authRoleEnum.getRoleName());

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, List.of(authority));

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } catch (UserNotExistsException e) {
                log.warn("{} | IP={} | URI={} | Method={} | Exception={}",
                        e.getMessage(), request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getClass().getSimpleName());
                writeError(response, e.getStatus().value(), e.getCode(), e.getMessage());
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int httpStatus, int code, String message) throws IOException {
        response.setStatus(httpStatus);
        response.setContentType(WebConstant.CONTENT_TYPE_JSON);
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(code, message)));
    }
}
