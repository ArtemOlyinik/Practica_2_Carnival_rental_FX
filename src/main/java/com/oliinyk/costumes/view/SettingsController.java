package com.oliinyk.costumes.view;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

public class SettingsController {
    
    @FXML private ToggleButton themeToggle;
    
    @FXML
    public void initialize() {
        // У цьому простому прикладі ми керуємо темою локально. 
        // Пізніше можна винести стан теми у SettingsViewModel або глобальний SessionManager.
        themeToggle.setOnAction(e -> {
            if (themeToggle.isSelected()) {
                themeToggle.setText("Вимкнути");
                Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
            } else {
                themeToggle.setText("Увімкнути");
                Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            }
        });
    }
}
