package com.oliinyk.costumes.view;

import atlantafx.base.theme.PrimerLight;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    private com.oliinyk.costumes.service.AuthService authService;

    @FXML
    public void initialize() {
        authService =
                new com.oliinyk.costumes.service.AuthService(
                        new com.oliinyk.costumes.repository.JdbcUserRepository(),
                        new com.oliinyk.costumes.service.ConsoleEmailServiceImpl());
        loginButton.setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            errorLabel.setText("Введіть email та пароль.");
            return;
        }

        try {
            java.util.Optional<com.oliinyk.costumes.model.User> userOpt =
                    authService.login(email, password);

            if (userOpt.isPresent()) {
                com.oliinyk.costumes.model.User user = userOpt.get();
                if (!user.isVerified()) {
                    errorLabel.setText("Акаунт не верифіковано.");
                    return;
                }
                com.oliinyk.costumes.service.SessionManager.getInstance().login(user);
                errorLabel.setText("");
                navigateToCatalog();
            } else {
                errorLabel.setText("Невірний email або пароль.");
            }
        } catch (RuntimeException ex) {
            errorLabel.setText(ex.getMessage());
            javafx.scene.control.Alert alert =
                    new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Помилка входу");
            alert.setHeaderText(null);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    private void navigateToCatalog() {
        try {
            URL fxmlLocation = getClass().getResource("/views/MainView.fxml");
            if (fxmlLocation == null) {
                throw new IllegalStateException("Не знайдено /views/MainView.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // Застосування теми за замовчуванням
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

            // Заміна сцени на тому ж Stage
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 650));
            stage.setTitle("Карнавальні Костюми");
            stage.setResizable(true);
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Помилка відкриття каталогу: " + e.getMessage());
        }
    }

    @FXML
    private void navigateToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RegisterView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 350));
            stage.setTitle("Реєстрація");
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Помилка відкриття форми: " + e.getMessage());
        }
    }
}
