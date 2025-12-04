package org.mizoguchi.misaki.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setFrom(from, "Misaki");
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
    public void sendVerificationEmail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String subject = "欢迎使用 Misaki - 邮箱安全验证码";

            helper.setFrom(from, "Misaki");
            helper.setTo(to);
            helper.setSubject(subject);

            String html = """
                    <style>
                        p {
                            margin: 0 auto;
                        }
                    </style>
                    <div style="font-family:sans-serif;">
                        <div style="width: 742px; margin: 0 auto; padding: 12px; background-color: #f5f5f7; color: #1d1d1f;">
                            <img src="cid:logoImage" alt="logo" style="width:192px; margin-top:10px;" />
                            <hr>
                            <div>
                                <h1>
                                    <span>你的验证码是：</span>
                                    <span style='color:#3142ef;'>%s</span>
                                </h1>
                                <p>有效期5分钟，请尽快使用。</p>
                            </div>
                            <hr>
                            <div style="color: #888;">
                                <p>你收到此电子邮件的原因：</p>
                                <p>当你使用电子邮箱注册 Misaki 账户或者更改 Misaki 账户密码时，Misaki 会提出验证要求。</p>
                                <p>如果你未做过此更改，或者认为有人未经授权访问了你的账户，你需尽快前往你的 Misaki 账户页面更改你的密码。</p>
                            </div>
                        </div>
                    </div>
                    """.formatted(code);

            helper.setText(html, true);

            FileSystemResource res = new FileSystemResource(new File("src/main/resources/static/Misaki_logo.svg"));
            helper.addInline("logoImage", res);

            mailSender.send(message);
            log.info("验证码成功发送到{} | 主题={}", to, subject);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new SendingEmailFailedException(FailMessageConstant.SENDING_EMAIL_FAILED);
        }
    }
}
