package org.mizoguchi.misaki.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mizoguchi.misaki.annotation.EnableEmailLog;
import org.mizoguchi.misaki.common.constant.EmailConstant;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.SendingEmailFailedException;
import org.mizoguchi.misaki.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    @EnableEmailLog(to = "#to", subject = "#subject")
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, EmailConstant.EMAIL_ENCODING);

            helper.setFrom(from, EmailConstant.SENDER_FRIENDLY_NAME);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            mailSender.send(message);
            log.info("邮件成功发送到{} | 主题={}", to, subject);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new SendingEmailFailedException(FailMessageConstant.SENDING_EMAIL_FAILED);
        }
    }

    @Override
    @EnableEmailLog(to = "#to", subject = "#subject")
    public void sendVerificationEmail(String to, String subject, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, EmailConstant.EMAIL_ENCODING);

            helper.setFrom(from, EmailConstant.SENDER_FRIENDLY_NAME);
            helper.setTo(to);
            helper.setSubject(subject);

            String html = EmailConstant.VERIFICATION_EMAIL_BODY.formatted(code);

            helper.setText(html, true);

            FileSystemResource res = new FileSystemResource(new File("src/main/resources/static/Misaki_logo.svg"));
            helper.addInline(EmailConstant.LOGO_CONTENT_ID, res);

            mailSender.send(message);
            log.info("验证码成功发送到{} | 主题={}", to, subject);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new SendingEmailFailedException(FailMessageConstant.SENDING_EMAIL_FAILED);
        }
    }
}
