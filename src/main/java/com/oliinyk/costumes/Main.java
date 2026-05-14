package com.oliinyk.costumes;

import atlantafx.base.theme.PrimerLight;
import com.oliinyk.costumes.infrastructure.DatabaseManager;
import com.oliinyk.costumes.repository.JdbcUserRepository;
import com.oliinyk.costumes.repository.UserRepository;
import com.oliinyk.costumes.service.AuthService;
import com.oliinyk.costumes.service.ConsoleEmailServiceImpl;
import com.oliinyk.costumes.service.EmailService;
import com.oliinyk.costumes.view.LoginController;
import com.oliinyk.costumes.viewmodel.LoginViewModel;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private UserRepository userRepository;
    private EmailService emailService;
    private AuthService authService;
    private LoginViewModel loginViewModel;

    @Override
    public void init() throws Exception {
        // 1. Ініціалізація бази даних та запуск міграцій Flyway
        // H2 embedded database file in current directory
        DatabaseManager.initialize("jdbc:h2:./carnival_rental_db;DB_CLOSE_DELAY=-1", "sa", "");

        // 2. Ручна ін'єкція залежностей (Dependency Injection)
        userRepository = new JdbcUserRepository();
        emailService = new ConsoleEmailServiceImpl();
        authService = new AuthService(userRepository, emailService);

        loginViewModel = new LoginViewModel(authService);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 3. Налаштування теми AtlantaFX (Світла за замовчуванням)
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        // 4. Завантаження головного FXML
        URL fxmlLocation = getClass().getResource("/views/LoginView.fxml");
        if (fxmlLocation == null) {
            throw new IllegalStateException(
                    "Не вдалося знайти файл /views/LoginView.fxml у ресурсах");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // 5. Контролер сам керує своїм станом

        // 6. Відображення головного вікна
        primaryStage.setTitle("Карнавальні Костюми - Авторизація");
        primaryStage.setScene(new Scene(root, 400, 350));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // Завершення роботи: безпечне закриття з'єднань із базою даних
        DatabaseManager.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
