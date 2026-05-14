package com.oliinyk.costumes.service;

import com.oliinyk.costumes.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/** Менеджер сесії користувача та глобальних налаштувань додатку. Реалізує патерн Singleton. */
public class SessionManager {
    private User currentUser;

    private final StringProperty viewMode = new SimpleStringProperty("GRID");
    private final StringProperty theme = new SimpleStringProperty("LIGHT");

    private SessionManager() {}

    private static class Holder {
        private static final SessionManager INSTANCE = new SessionManager();
    }

    /** Отримати екземпляр менеджера. */
    public static SessionManager getInstance() {
        return Holder.INSTANCE;
    }

    /** Увійти в систему. */
    public void login(User user) {
        this.currentUser = user;
    }

    /** Вийти з системи. */
    public void logout() {
        this.currentUser = null;
    }

    /** Отримати поточного користувача. */
    public User getCurrentUser() {
        return currentUser;
    }

    /** Перевірити, чи користувач авторизований. */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /** Перевірити, чи є поточний користувач адміністратором. */
    public boolean isAdmin() {
        return isLoggedIn() && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    /** Властивість режиму відображення каталогу (GRID/LIST). */
    public StringProperty viewModeProperty() {
        return viewMode;
    }

    /** Властивість поточної теми (LIGHT/DARK). */
    public StringProperty themeProperty() {
        return theme;
    }
}
