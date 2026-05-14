package com.oliinyk.costumes.service;

import com.oliinyk.costumes.model.User;
import com.oliinyk.costumes.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    // Реєстрація нового користувача із хешуванням пароля (jBCrypt)
    public User registerUser(String email, String rawPassword, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Користувач з таким email вже існує.");
        }

        // Хешування пароля перед збереженням у БД
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        String verificationCode = UUID.randomUUID().toString().substring(0, 6);

        User newUser =
                User.builder()
                        .id(UUID.randomUUID())
                        .email(email)
                        .passwordHash(hashedPassword)
                        .role(role)
                        .verificationCode(verificationCode)
                        .isVerified(false)
                        .createdAt(LocalDateTime.now())
                        .build();

        userRepository.save(newUser);

        // Відправка коду верифікації
        emailService.sendVerificationEmail(email, verificationCode);

        return newUser;
    }

    // Авторизація користувача з перевіркою хешу
    public Optional<User> login(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Перевірка відповідності введеного пароля хешу з БД
            if (BCrypt.checkpw(rawPassword, user.getPasswordHash())) {
                if (user.isBlocked()) {
                    throw new RuntimeException("Ваш акаунт заблоковано адміністратором.");
                }
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    // Підтвердження пошти користувача за допомогою коду
    public boolean verifyUser(String email, String code) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.isVerified() && code.equals(user.getVerificationCode())) {
                user.setVerified(true);
                user.setVerificationCode(null); // Очищаємо код після успішної верифікації
                userRepository.update(user);
                return true;
            }
        }
        return false;
    }
}
