package com.oliinyk.costumes.viewmodel;

import com.oliinyk.costumes.service.SessionManager;
import javafx.beans.property.StringProperty;

/** ViewModel для налаштувань. Керує станом теми та режимом відображення. */
public class SettingsViewModel {

    private final SessionManager sessionManager = SessionManager.getInstance();

    public StringProperty themeProperty() {
        return sessionManager.themeProperty();
    }

    public StringProperty viewModeProperty() {
        return sessionManager.viewModeProperty();
    }

    public boolean isDarkTheme() {
        return "DARK".equals(sessionManager.themeProperty().get());
    }

    public void setTheme(boolean isDark) {
        sessionManager.themeProperty().set(isDark ? "DARK" : "LIGHT");
    }

    public boolean isGridView() {
        return !"LIST".equals(sessionManager.viewModeProperty().get());
    }

    public void setViewMode(boolean isGrid) {
        sessionManager.viewModeProperty().set(isGrid ? "GRID" : "LIST");
    }
}
