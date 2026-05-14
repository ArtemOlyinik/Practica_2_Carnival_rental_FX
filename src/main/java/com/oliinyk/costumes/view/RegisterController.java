package com.oliinyk.costumes.view;

import com.oliinyk.costumes.repository.JdbcUserRepository;
import com.oliinyk.costumes.service.AuthService;
import com.oliinyk.costumes.service.ConsoleEmailServiceImpl;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private VBox registerPane;
    @FXML private VBox verifyPane;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField codeField;
    @FXML private Label errorLabel;

    private AuthService authService;

    @FXML
    public void initialize() {
        authService = new AuthService(new JdbcUserRepository(), new ConsoleEmailServiceImpl());
    }

    @FXML
    private void handleRegister() {
        String email = emailField.getText();
        String pass = passwordField.getText();
        if (email == null || email.trim().isEmpty() || pass == null || pass.isEmpty()) {
            errorLabel.setText("Введіть email та пароль.");
            return;
        }

        try {
            authService.registerUser(email, pass, "USER");
            registerPane.setVisible(false);
            registerPane.setManaged(false);
            verifyPane.setVisible(true);
            verifyPane.setManaged(true);
            errorLabel.setText("");
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Помилка реєстрації: " + e.getMessage());
        }
    }

    @FXML
    private void handleVerify() {
        String email = emailField.getText();
        String code = codeField.getText();

        if (authService.verifyUser(email, code)) {
            errorLabel.setStyle("-fx-text-fill: -color-success-fg;");
            errorLabel.setText("Успішно! Тепер увійдіть.");
            // Робимо паузу або просто чекаємо поки користувач натисне Назад
            verifyPane.setVisible(false);
            verifyPane.setManaged(false);
            Button backBtn = new Button("Повернутися до входу");
            backBtn.setOnAction(e -> navigateBack());
            backBtn.getStyleClass().add("accent");
            ((VBox) errorLabel.getParent()).getChildren().add(backBtn);
        } else {
            errorLabel.setStyle("-fx-text-fill: -color-danger-fg;");
            errorLabel.setText("Невірний код.");
        }
    }

    @FXML
    private void navigateBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 350));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
