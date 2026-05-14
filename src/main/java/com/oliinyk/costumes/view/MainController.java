package com.oliinyk.costumes.view;

import com.oliinyk.costumes.viewmodel.CatalogViewModel;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController {

    @FXML private StackPane contentArea;

    // Створюємо загальний ViewModel для каталогу, щоб не втрачати стан при перемиканні
    @FXML private javafx.scene.control.Button btnAdmin;

    private CatalogViewModel catalogViewModel = new CatalogViewModel();

    @FXML
    public void initialize() {
        // Ховаємо кнопку "Адмін Панель", якщо користувач не ADMIN
        com.oliinyk.costumes.model.User user = com.oliinyk.costumes.service.SessionManager.getInstance().getCurrentUser();
        if (user == null || !"ADMIN".equals(user.getRole())) {
            btnAdmin.setVisible(false);
            btnAdmin.setManaged(false);
        }
        
        showCatalog(); // Відкриваємо каталог при старті
    }

    @FXML
    private void showCatalog() {
        loadView("/views/CatalogView.fxml", catalogViewModel);
    }

    @FXML
    private void showBasket() {
        loadView("/views/BasketView.fxml", null);
    }

    @FXML
    private void showSettings() {
        loadView("/views/SettingsView.fxml", null);
    }

    @FXML
    private void showAdminPanel() {
        loadView("/views/AdminPanelView.fxml", null);
    }

    @FXML
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 400, 350));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlPath, Object viewModel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            
            Object controller = loader.getController();
            if (controller instanceof CatalogController) {
                ((CatalogController) controller).setViewModel((CatalogViewModel) viewModel);
            }
            // TODO: додати if для BasketController та SettingsController
            
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
