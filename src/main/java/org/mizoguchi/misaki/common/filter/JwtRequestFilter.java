package org.mizoguchi.misaki.common.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mizoguchi.misaki.common.constant.MessageConstant;
import org.mizoguchi.misaki.common.constant.WebConstant;
import org.mizoguchi.misaki.common.enumeration.AuthRoleEnum;
import org.mizoguchi.misaki.common.exception.UserNotExistsException;
import org.mizoguchi.misaki.common.util.JwtUtil;
import org.mizoguchi.misaki.service.impl.UserDetailsServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String authHeader = request.getHeader(WebConstant.HEADER_AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(WebConstant.BEARER_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String userId;
        Integer roleCode;

        try {
            userId = jwtUtil.getSubjectFromToken(jwt);
            roleCode = jwtUtil.getRoleFromToken(jwt);
        } catch (ExpiredJwtException e) {
            log.warn("{} | IP={} | URI={} | Method={} | Exception={}",
                    MessageConstant.JWT_EXPIRED, request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getClass().getSimpleName());
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, 401, MessageConstant.JWT_EXPIRED);
            return;
        } catch (Exception e) {
            log.warn("{} | IP={} | URI={} | Method={} | Exception={} | Message={}",
                    MessageConstant.INVALID_JWT, request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getClass().getSimpleName(), e.getMessage());
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, 401, MessageConstant.INVALID_JWT);
            return;
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

                if (jwtUtil.validateToken(jwt)) {
                    AuthRoleEnum authRoleEnum = AuthRoleEnum.fromCode(roleCode);
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(authRoleEnum.getRoleName());

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, List.of(authority));

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (UserNotExistsException e) {
                log.warn("{} | IP={} | URI={} | Method={} | Exception={}",
                        e.getMessage(), request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getClass().getSimpleName());
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, 401, e.getMessage());
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int httpStatus, int code, String message) throws IOException {
        response.setStatus(httpStatus);
        response.setContentType(WebConstant.CONTENT_TYPE_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put(WebConstant.FIELD_CODE, code);
        body.put(WebConstant.FIELD_MESSAGE, message);
        response.getWriter().write(MAPPER.writeValueAsString(body));
    }
}
