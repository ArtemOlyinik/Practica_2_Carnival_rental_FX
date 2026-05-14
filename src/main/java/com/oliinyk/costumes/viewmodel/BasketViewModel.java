package com.oliinyk.costumes.viewmodel;

import com.oliinyk.costumes.model.Costume;
import com.oliinyk.costumes.model.User;
import com.oliinyk.costumes.service.BasketService;
import com.oliinyk.costumes.service.RentalFacade;
import com.oliinyk.costumes.service.SessionManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/** ViewModel для кошика. Інкапсулює бізнес-логіку та стан для BasketController. */
public class BasketViewModel {

    private final RentalFacade rentalFacade;
    private final BasketService basketService = BasketService.getInstance();

    private final ObservableList<Costume> items = FXCollections.observableArrayList();
    private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>(LocalDate.now());
    private final ObjectProperty<LocalDate> endDate =
            new SimpleObjectProperty<>(LocalDate.now().plusDays(1));
    private final StringProperty totalPrice = new SimpleStringProperty("0.00 грн");
    private final StringProperty depositTotal = new SimpleStringProperty("0.00 грн");
    private final StringProperty discountAmount = new SimpleStringProperty("0.00 грн");

    public BasketViewModel(RentalFacade rentalFacade) {
        this.rentalFacade = rentalFacade;
        refresh();
    }

    public void refresh() {
        items.setAll(basketService.getItems());
        recalculate();
    }

    public void recalculate() {
        if (startDate.get() != null && endDate.get() != null) {
            long days = ChronoUnit.DAYS.between(startDate.get(), endDate.get());
            if (days <= 0) days = 1;

            BigDecimal rentalTotal = basketService.calculateRentalTotal(days);
            BigDecimal deposit = basketService.calculateTotalDeposit();
            BigDecimal discount = basketService.calculateDiscount(days);

            totalPrice.set(String.format("%.2f грн", rentalTotal.add(deposit)));
            depositTotal.set(String.format("%.2f грн", deposit));
            discountAmount.set(String.format("-%.2f грн", discount));
        }
    }

    public void checkout() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("Будь ласка, авторизуйтесь.");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Кошик порожній.");
        }

        rentalFacade.checkout(user, items, startDate.get(), endDate.get());
        basketService.clear();
        refresh();
    }

    public ObservableList<Costume> getItems() {
        return items;
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }

    public StringProperty totalPriceProperty() {
        return totalPrice;
    }

    public StringProperty depositTotalProperty() {
        return depositTotal;
    }

    public StringProperty discountAmountProperty() {
        return discountAmount;
    }

    public List<Costume> getItemsSnapshot() {
        return basketService.getItems();
    }

    public BigDecimal getRentalTotal(long days) {
        return basketService.calculateRentalTotal(days);
    }

    public BigDecimal getTotalDeposit() {
        return basketService.calculateTotalDeposit();
    }

    public BigDecimal getDiscount(long days) {
        return basketService.calculateDiscount(days);
    }
}
