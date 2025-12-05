package org.mizoguchi.misaki.aspect;

import jakarta.annotation.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.mizoguchi.misaki.annotation.EnableEmailLog;
import org.mizoguchi.misaki.mapper.EmailLogMapper;
import org.mizoguchi.misaki.pojo.entity.EmailLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class EmailLogAspect {
    @Resource
    private EmailLogMapper emailLogMapper;

    @Value("${spring.mail.username}")
    private String from;

    @AfterReturning("@annotation(enableEmailLog)")
    public void insertEmailLog(JoinPoint joinPoint, EnableEmailLog enableEmailLog) {
        // 构造 SpEL 环境
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        // 解析注解字段
        String to = parse(enableEmailLog.to(), context);
        String subject = parse(enableEmailLog.subject(), context);

        // 写入数据库
        EmailLog emailLog = EmailLog.builder()
                .sender(from)
                .receiver(to)
                .subject(subject)
                .build();

        emailLogMapper.insert(emailLog);
    }

    private String parse(String exp, EvaluationContext ctx) {
        if (exp == null || exp.isEmpty()) return null;
        return new SpelExpressionParser().parseExpression(exp).getValue(ctx, String.class);
    }
}
