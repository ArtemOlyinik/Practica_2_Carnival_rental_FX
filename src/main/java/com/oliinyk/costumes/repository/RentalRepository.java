package com.oliinyk.costumes.repository;

import com.oliinyk.costumes.model.Rental;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

/** Репозиторій для роботи з орендою. */
public interface RentalRepository extends Repository<Rental> {
    /**
     * Знайти всі оренди конкретного користувача.
     *
     * @param userId ID користувача
     * @return список оренд
     */
    List<Rental> findByUserId(UUID userId);

    /**
     * Зберегти оренду в межах існуючої транзакції.
     *
     * @param rental об'єкт оренди
     * @param conn SQL з'єднання
     */
    void save(Rental rental, Connection conn);

    /**
     * Перевірити, чи доступний костюм на вказані дати.
     *
     * @param costumeId ID костюма
     * @param start дата початку
     * @param end дата кінця
     * @return true, якщо доступний
     */
    boolean isCostumeAvailable(UUID costumeId, java.time.LocalDate start, java.time.LocalDate end);
}
