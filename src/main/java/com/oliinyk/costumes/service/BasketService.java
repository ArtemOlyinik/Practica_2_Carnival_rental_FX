package com.oliinyk.costumes.service;

import com.oliinyk.costumes.model.Costume;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Сервіс для керування кошиком обраних костюмів. Реалізує патерн Singleton. */
public class BasketService {
    private final List<Costume> items = new ArrayList<>();

    private BasketService() {}

    private static class Holder {
        private static final BasketService INSTANCE = new BasketService();
    }

    /** Отримати екземпляр сервісу. */
    public static BasketService getInstance() {
        return Holder.INSTANCE;
    }

    /** Додати костюм до кошика. */
    public void addItem(Costume costume) {
        items.add(costume);
    }

    /** Видалити костюм з кошика. */
    public void removeItem(Costume costume) {
        items.remove(costume);
    }

    /** Отримати копію списку костюмів у кошику. */
    public List<Costume> getItems() {
        return new ArrayList<>(items);
    }

    /** Очистити кошик. */
    public void clear() {
        items.clear();
    }

    /** Розрахувати загальну вартість оренди для всіх елементів у кошику. */
    public BigDecimal calculateTotal(long days) {
        BigDecimal total = BigDecimal.ZERO;
        for (Costume item : items) {
            total = total.add(item.getPricePerDay());
        }
        return total.multiply(BigDecimal.valueOf(days > 0 ? days : 1));
    }
}
