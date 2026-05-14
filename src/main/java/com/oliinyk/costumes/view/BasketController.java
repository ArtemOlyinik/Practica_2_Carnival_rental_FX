package com.oliinyk.costumes.view;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import com.oliinyk.costumes.model.Costume;
import com.oliinyk.costumes.service.BasketService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BasketController {
    
    @FXML private ListView<String> basketItems;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label totalPriceLabel;
    
    @FXML
    public void initialize() {
        basketItems.getItems().clear();
        for (Costume c : BasketService.getInstance().getItems()) {
            basketItems.getItems().add(c.getName() + " - " + c.getPricePerDay() + " грн/день");
        }
        
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(1));
        
        // Слухачі для автоматичного перерахунку ціни (поки що тестові значення)
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> recalculatePrice());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> recalculatePrice());
        
        recalculatePrice();
    }
    
    private void recalculatePrice() {
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
            long days = ChronoUnit.DAYS.between(startDatePicker.getValue(), endDatePicker.getValue());
            if (days <= 0) days = 1;
            
            java.math.BigDecimal total = BasketService.getInstance().calculateTotal(days);
            totalPriceLabel.setText(total + " грн");
        }
    }
}
