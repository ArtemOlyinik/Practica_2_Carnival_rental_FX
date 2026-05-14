package com.oliinyk.costumes.viewmodel;

import com.oliinyk.costumes.model.Costume;
import java.math.BigDecimal;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.oliinyk.costumes.service.BasketService;

public class CatalogViewModel {

    private final ObservableList<Costume> costumes = FXCollections.observableArrayList();
    private final BooleanProperty isDarkTheme = new SimpleBooleanProperty(false);
    private final StringProperty cartButtonText = new SimpleStringProperty("Кошик (0)");
    private int cartCount = 0;

    public CatalogViewModel() {
        loadFromDatabase();
    }

    public ObservableList<Costume> getCostumes() {
        return costumes;
    }

    public BooleanProperty isDarkThemeProperty() {
        return isDarkTheme;
    }

    public void toggleTheme() {
        isDarkTheme.set(!isDarkTheme.get());
    }

    public StringProperty cartButtonTextProperty() {
        return cartButtonText;
    }

    public void addToCart(Costume costume) {
        BasketService.getInstance().addItem(costume);
        cartCount++;
        cartButtonText.set("Кошик (" + cartCount + ")");
    }

    private void loadFromDatabase() {
        com.oliinyk.costumes.repository.CostumeRepository repo = new com.oliinyk.costumes.repository.JdbcCostumeRepository();
        costumes.setAll(repo.findAll());
    }
}
