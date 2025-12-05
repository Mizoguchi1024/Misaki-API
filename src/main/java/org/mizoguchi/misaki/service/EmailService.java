package org.mizoguchi.misaki.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendVerificationEmail(String to, String subject, String code);
}
