package com.oliinyk.costumes.service;

public interface EmailService {
    // Відправка листа для верифікації
    void sendVerificationEmail(String email, String verificationCode);
}
