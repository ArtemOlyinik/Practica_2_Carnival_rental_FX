package com.oliinyk.costumes.service;

public class ConsoleEmailServiceImpl implements EmailService {

    @Override
    public void sendVerificationEmail(String email, String verificationCode) {
        // Симуляція відправки email через вивід у консоль
        System.out.println("=====================================");
        System.out.println("EMAIL ПОВІДОМЛЕННЯ");
        System.out.println("Кому: " + email);
        System.out.println("Тема: Підтвердження реєстрації");
        System.out.println("Код підтвердження: " + verificationCode);
        System.out.println("=====================================");
    }
}
