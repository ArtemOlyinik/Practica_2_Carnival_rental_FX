package com.oliinyk.costumes.repository;

import com.oliinyk.costumes.model.RentalItem;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

/** Репозиторій для роботи з елементами оренди (костюмами в замовленні). */
public interface RentalItemRepository {
    /**
     * Зберегти елемент оренди.
     *
     * @param item елемент оренди
     */
    void save(RentalItem item);

    /**
     * Зберегти елемент оренди в межах існуючої транзакції.
     *
     * @param item елемент оренди
     * @param conn SQL з'єднання
     */
    void save(RentalItem item, Connection conn);

    /**
     * Знайти всі елементи для конкретної оренди.
     *
     * @param rentalId ID оренди
     * @return список елементів
     */
    List<RentalItem> findByRentalId(UUID rentalId);
}
