package org.mizoguchi.misaki.aspect;

import jakarta.annotation.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.mizoguchi.misaki.annotation.EnableEmailLog;
import org.mizoguchi.misaki.mapper.EmailLogMapper;
import org.mizoguchi.misaki.pojo.entity.EmailLog;
import org.springframework.beans.factory.annotation.Value;
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
        Object[] args = joinPoint.getArgs();

        if (args == null || args.length < 2) {
            return;
        }

        String to = (String) args[0];
        String subject = (String) args[1];

        EmailLog emailLog = EmailLog.builder()
                .sender(from)
                .receiver(to)
                .subject(subject)
                .build();

        emailLogMapper.insert(emailLog);
    }
}
