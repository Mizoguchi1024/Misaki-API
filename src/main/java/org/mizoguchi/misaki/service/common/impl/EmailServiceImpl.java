package org.mizoguchi.misaki.service.common.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mizoguchi.misaki.common.constant.EmailConstant;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.FailedToSendEmailException;
import org.mizoguchi.misaki.mapper.EmailLogMapper;
import org.mizoguchi.misaki.pojo.entity.EmailLog;
import org.mizoguchi.misaki.service.common.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final EmailLogMapper emailLogMapper;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendEmail(String to, String subject, String body, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            if (code == null) {
                MimeMessageHelper helper = new MimeMessageHelper(message, EmailConstant.EMAIL_ENCODING);

                helper.setFrom(from, EmailConstant.SENDER_FRIENDLY_NAME);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(body);
            }else {
                MimeMessageHelper helper = new MimeMessageHelper(message, true, EmailConstant.EMAIL_ENCODING);

                helper.setFrom(from, EmailConstant.SENDER_FRIENDLY_NAME);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(body.formatted(code), true);

                ClassPathResource logoResource = new ClassPathResource("static/Misaki_logo.svg");
                helper.addInline(EmailConstant.LOGO_CONTENT_ID, logoResource, "image/svg+xml");
            }

            mailSender.send(message);
            log.info("Successfully send email to {} | Subject={}", to, subject);

            EmailLog emailLog = EmailLog.builder()
                    .sender(from)
                    .receiver(to)
                    .subject(subject)
                    .build();

            emailLogMapper.insert(emailLog);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new FailedToSendEmailException(FailMessageConstant.FAILED_TO_SEND_EMAIL);
        }
    }

    @Override
    public void sendVerificationEmail(String to, String code, Integer lang) {
        switch (lang) {
            case 0:
                sendEmail(to, EmailConstant.VERIFICATION_EMAIL_SUBJECT_ZH + code, EmailConstant.VERIFICATION_EMAIL_BODY_ZH, code);
                break;
            case 1:
                sendEmail(to, EmailConstant.VERIFICATION_EMAIL_SUBJECT_EN + code, EmailConstant.VERIFICATION_EMAIL_BODY_EN, code);
                break;
            case 2:
                sendEmail(to, EmailConstant.VERIFICATION_EMAIL_SUBJECT_JP + code, EmailConstant.VERIFICATION_EMAIL_BODY_JP, code);
        }
    }
}
