package org.mizoguchi.misaki.service.common;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendVerificationEmail(String to, String subject, String code);
}
