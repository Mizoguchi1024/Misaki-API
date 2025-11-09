package org.mizoguchi.misaki.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendVerificationEmail(String to, String code);
}
