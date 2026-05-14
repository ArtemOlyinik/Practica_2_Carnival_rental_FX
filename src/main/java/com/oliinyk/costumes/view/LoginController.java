package com.oliinyk.costumes.view;

import atlantafx.base.theme.PrimerLight;
import com.oliinyk.costumes.viewmodel.CatalogViewModel;
import com.oliinyk.costumes.viewmodel.LoginViewModel;
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

    private LoginViewModel viewModel;

    // Ініціалізація ViewModel
    public void setViewModel(LoginViewModel viewModel) {
        this.viewModel = viewModel;
        bindViewModel();
    }

    private void bindViewModel() {
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());

        loginButton.setOnAction(e -> viewModel.attemptLogin());

        viewModel.loginSuccessfulProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                navigateToCatalog();
            }
        });
    }

    @FXML
    public void initialize() {
        // Залишено пустим, бо ініціалізація йде через setViewModel
    }

    private void navigateToCatalog() {
        try {
            URL fxmlLocation =
                    getClass().getResource("/views/MainView.fxml");
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
            if (viewModel != null) {
                viewModel.errorMessageProperty().set("Помилка відкриття каталогу: " + e.getMessage());
            }
        }
    }
}
