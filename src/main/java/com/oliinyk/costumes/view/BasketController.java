package com.oliinyk.costumes.view;

import com.oliinyk.costumes.model.Costume;
import com.oliinyk.costumes.model.User;
import com.oliinyk.costumes.repository.JdbcCostumeRepository;
import com.oliinyk.costumes.repository.JdbcRentalItemRepository;
import com.oliinyk.costumes.repository.JdbcRentalRepository;
import com.oliinyk.costumes.repository.JdbcUserRepository;
import com.oliinyk.costumes.service.BasketService;
import com.oliinyk.costumes.service.RentalFacade;
import com.oliinyk.costumes.service.RentalService;
import com.oliinyk.costumes.service.SessionManager;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/** Контролер кошика. Реалізує асинхронну логіку оформлення замовлення (Вимога розділу 4.4.5). */
public class BasketController {

    @FXML private ListView<String> basketItems;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label totalPriceLabel;

    private RentalFacade rentalFacade;

    @FXML
    public void initialize() {
        // Ініціалізація фасаду (Вимога розділу 4.3.2)
        rentalFacade =
                new RentalFacade(
                        new RentalService(
                                new JdbcRentalRepository(), new JdbcRentalItemRepository()),
                        new JdbcRentalRepository(),
                        new JdbcCostumeRepository(),
                        new JdbcUserRepository());

        refreshList();

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(1));

        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> recalculatePrice());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> recalculatePrice());

        recalculatePrice();
    }

    private void refreshList() {
        basketItems.getItems().clear();
        for (Costume c : BasketService.getInstance().getItems()) {
            basketItems.getItems().add(c.getName() + " - " + c.getPricePerDay() + " грн/день");
        }
    }

    private void recalculatePrice() {
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
            long days =
                    ChronoUnit.DAYS.between(startDatePicker.getValue(), endDatePicker.getValue());
            if (days <= 0) days = 1;

            java.math.BigDecimal total = BasketService.getInstance().calculateTotal(days);
            totalPriceLabel.setText(total + " грн");
        }
    }

    @FXML
    private void onRentClicked() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) {
            showAlert("Помилка", "Будь ласка, авторизуйтесь.", Alert.AlertType.ERROR);
            return;
        }

        if (BasketService.getInstance().getItems().isEmpty()) {
            showAlert("Кошик", "Кошик порожній.", Alert.AlertType.WARNING);
            return;
        }

        // Асинхронне оформлення замовлення (Вимога розділу 4.4.5)
        Task<Void> checkoutTask =
                new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        rentalFacade.checkout(
                                user,
                                BasketService.getInstance().getItems(),
                                startDatePicker.getValue(),
                                endDatePicker.getValue());
                        return null;
                    }
                };
        checkoutTask.setOnSucceeded(
                e -> {
                    showAlert("Успіх", "Оренда успішно оформлена!", Alert.AlertType.INFORMATION);

                    BasketService.getInstance().clear();
                    refreshList();
                    recalculatePrice();

                    // Оновлення стану через SessionManager
                    com.oliinyk.costumes.service.SessionManager.getInstance()
                            .viewModeProperty()
                            .set(
                                    com.oliinyk.costumes.service.SessionManager.getInstance()
                                            .viewModeProperty()
                                            .get());
                });

        checkoutTask.setOnFailed(
                e -> {
                    Throwable ex = checkoutTask.getException();
                    showAlert("Помилка", ex.getMessage(), Alert.AlertType.ERROR);
                });

        // Запуск таска у фоновому потоці
        new Thread(checkoutTask).start();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
