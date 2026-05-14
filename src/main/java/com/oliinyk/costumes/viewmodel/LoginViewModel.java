package com.oliinyk.costumes.viewmodel;

import com.oliinyk.costumes.service.AuthService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginViewModel {

    private final AuthService authService;

    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty loginSuccessful = new SimpleBooleanProperty(false);

    public LoginViewModel(AuthService authService) {
        this.authService = authService;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public BooleanProperty loginSuccessfulProperty() {
        return loginSuccessful;
    }

    public void attemptLogin() {
        if (email.get() == null || email.get().isEmpty()) {
            errorMessage.set("Email не може бути порожнім");
            return;
        }

        authService
                .login(email.get(), password.get())
                .ifPresentOrElse(
                        user -> {
                            if (!user.isVerified()) {
                                errorMessage.set("Акаунт не верифіковано.");
                                loginSuccessful.set(false);
                            } else {
                                com.oliinyk.costumes.service.SessionManager.getInstance().login(user);
                                errorMessage.set("");
                                loginSuccessful.set(true);
                            }
                        },
                        () -> errorMessage.set("Неправильний email або пароль"));
    }
}
