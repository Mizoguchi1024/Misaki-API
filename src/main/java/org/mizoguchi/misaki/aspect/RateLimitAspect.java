package org.mizoguchi.misaki.aspect;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.mizoguchi.misaki.annotation.EnableRateLimit;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.RedisConstant;
import org.mizoguchi.misaki.common.exception.TooManyRequestsException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    @Resource
    private final RedisTemplate<String, String> redisTemplate;

    @Around("@annotation(enableRateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, EnableRateLimit enableRateLimit) throws Throwable {
        int limit = enableRateLimit.limit();
        int window = enableRateLimit.window();

        // 获取请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String ip = getClientIp(request);

        // 方法唯一标识
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Redis Key
        String key = RedisConstant.RATE_LIMIT
                + ip + ":"
                + method.getDeclaringClass().getName()
                + "." + method.getName();

        // 原子自增
        Long count = redisTemplate.opsForValue().increment(key);

        // 第一次访问，设置过期时间
        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(window));
        }

        // 超限
        if (count != null && count > limit) {
            throw new TooManyRequestsException(FailMessageConstant.TOO_MANY_REQUESTS);
        }

        return joinPoint.proceed();
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
