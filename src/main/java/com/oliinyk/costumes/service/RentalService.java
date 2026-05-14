package com.oliinyk.costumes.service;

import com.oliinyk.costumes.infrastructure.DatabaseManager;
import com.oliinyk.costumes.model.Costume;
import com.oliinyk.costumes.model.Rental;
import com.oliinyk.costumes.model.RentalItem;
import com.oliinyk.costumes.model.User;
import com.oliinyk.costumes.repository.RentalItemRepository;
import com.oliinyk.costumes.repository.RentalRepository;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/** Сервіс для управління процесом оренди костюмів. Забезпечує бізнес-логіку та транзакційність. */
public class RentalService {

    private final RentalRepository rentalRepository;
    private final RentalItemRepository rentalItemRepository;

    public RentalService(
            RentalRepository rentalRepository, RentalItemRepository rentalItemRepository) {
        this.rentalRepository = rentalRepository;
        this.rentalItemRepository = rentalItemRepository;
    }

    /**
     * Оформлення нового замовлення (Checkout). Використовує транзакцію для збереження оренди та її
     * елементів.
     *
     * @param user Користувач, який робить замовлення
     * @param costumes Список обраних костюмів
     * @param startDate Дата початку оренди
     * @param endDate Дата завершення оренди
     * @return Створений об'єкт оренди
     */
    public Rental checkout(
            User user, List<Costume> costumes, LocalDate startDate, LocalDate endDate) {
        // 1. Бізнес-валідація
        validateRentalDates(startDate, endDate);
        if (costumes == null || costumes.isEmpty()) {
            throw new IllegalArgumentException("Кошик порожній.");
        }

        // Перевірка доступності кожного костюма
        for (Costume costume : costumes) {
            if (!rentalRepository.isCostumeAvailable(costume.getId(), startDate, endDate)) {
                throw new IllegalStateException(
                        "Костюм '" + costume.getName() + "' вже орендований на ці дати.");
            }
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days == 0) days = 1;

        BigDecimal totalPrice = calculateTotalPrice(costumes, days);

        Rental rental =
                Rental.builder()
                        .id(UUID.randomUUID())
                        .userId(user.getId())
                        .startDate(startDate)
                        .endDate(endDate)
                        .totalPrice(totalPrice)
                        .status("ACTIVE")
                        .build();

        // 2. Виконання транзакції (Вимога розділу 3.4 - чистий JDBC)
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Початок транзакції
            try {
                // Використання репозиторіїв у межах однієї транзакції
                rentalRepository.save(rental, conn);

                for (Costume costume : costumes) {
                    RentalItem item =
                            RentalItem.builder()
                                    .rentalId(rental.getId())
                                    .costumeId(costume.getId())
                                    .priceAtRental(costume.getPricePerDay())
                                    .build();
                    rentalItemRepository.save(item, conn);
                }

                conn.commit(); // Підтвердження транзакції
                return rental;
            } catch (Exception e) {
                conn.rollback(); // Відкат транзакції у разі помилки
                throw new RuntimeException(
                        "Помилка при оформленні оренди. Транзакцію скасовано.", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка з'єднання з базою даних", e);
        }
    }

    private void validateRentalDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Дати не можуть бути порожніми.");
        }
        if (start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Дата початку не може бути в минулому.");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException(
                    "Дата початку не може бути пізніше дати завершення.");
        }
    }

    private BigDecimal calculateTotalPrice(List<Costume> costumes, long days) {
        BigDecimal total = BigDecimal.ZERO;
        for (Costume costume : costumes) {
            total = total.add(costume.getPricePerDay().multiply(BigDecimal.valueOf(days)));
        }
        return total;
    }
}
