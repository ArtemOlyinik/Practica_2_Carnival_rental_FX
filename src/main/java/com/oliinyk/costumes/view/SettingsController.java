package com.oliinyk.costumes.view;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.oliinyk.costumes.service.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

/** Контролер налаштувань. Керує темою та режимом відображення каталогу. */
public class SettingsController {

    @FXML private ToggleButton themeToggle;
    @FXML private ToggleButton gridToggle;
    @FXML private ToggleButton listToggle;

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();

        // Налаштування теми
        themeToggle.setSelected("DARK".equals(session.themeProperty().get()));
        updateThemeText();

        themeToggle.setOnAction(
                e -> {
                    if (themeToggle.isSelected()) {
                        session.themeProperty().set("DARK");
                        Application.setUserAgentStylesheet(
                                new PrimerDark().getUserAgentStylesheet());
                    } else {
                        session.themeProperty().set("LIGHT");
                        Application.setUserAgentStylesheet(
                                new PrimerLight().getUserAgentStylesheet());
                    }
                    updateThemeText();
                });

        // Налаштування режиму каталогу
        ToggleGroup viewGroup = new ToggleGroup();
        gridToggle.setToggleGroup(viewGroup);
        listToggle.setToggleGroup(viewGroup);

        if ("LIST".equals(session.viewModeProperty().get())) {
            listToggle.setSelected(true);
        } else {
            gridToggle.setSelected(true);
        }
    }

    private void updateThemeText() {
        themeToggle.setText(themeToggle.isSelected() ? "Вимкнути" : "Увімкнути");
    }

    @FXML
    private void onGridViewSelected() {
        SessionManager.getInstance().viewModeProperty().set("GRID");
    }

    @FXML
    private void onListViewSelected() {
        SessionManager.getInstance().viewModeProperty().set("LIST");
    }
}
