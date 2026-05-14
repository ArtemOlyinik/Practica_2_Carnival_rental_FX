package com.oliinyk.costumes.service;

import com.oliinyk.costumes.model.Costume;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BasketService {
    private static BasketService instance;
    private final List<Costume> items = new ArrayList<>();

    private BasketService() {}

    public static BasketService getInstance() {
        if (instance == null) {
            instance = new BasketService();
        }
        return instance;
    }

    public void addItem(Costume costume) {
        items.add(costume);
    }

    public void removeItem(Costume costume) {
        items.remove(costume);
    }

    public List<Costume> getItems() {
        return new ArrayList<>(items);
    }

    public void clearBasket() {
        items.clear();
    }

    public BigDecimal calculateTotal(long days) {
        BigDecimal total = BigDecimal.ZERO;
        for (Costume item : items) {
            total = total.add(item.getPricePerDay());
        }
        return total.multiply(BigDecimal.valueOf(days > 0 ? days : 1));
    }
}
