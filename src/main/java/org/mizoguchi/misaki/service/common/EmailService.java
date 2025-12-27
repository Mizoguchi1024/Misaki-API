package org.mizoguchi.misaki.service.common;

public interface EmailService {
    void sendEmail(String to, String subject, String body, String code);
    void sendVerificationEmail(String to, String code, Integer lang);
}
