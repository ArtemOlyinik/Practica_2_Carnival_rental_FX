package com.oliinyk.costumes.service;

import com.oliinyk.costumes.model.Costume;
import com.oliinyk.costumes.model.Rental;
import com.oliinyk.costumes.model.RentalItem;
import com.oliinyk.costumes.model.User;
import com.oliinyk.costumes.repository.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class RentalService {

    private final Repository<Rental> rentalRepository;
    private final Repository<RentalItem> rentalItemRepository;

    public RentalService(
            Repository<Rental> rentalRepository, Repository<RentalItem> rentalItemRepository) {
        this.rentalRepository = rentalRepository;
        this.rentalItemRepository = rentalItemRepository;
    }

    // Бізнес-логіка створення нового замовлення (оренди)
    public Rental createRental(
            User user, List<Costume> costumes, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Дата початку оренди не може бути пізніше дати завершення.");
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days == 0) {
            days = 1; // Мінімальна оренда - 1 день
        }

        BigDecimal totalPrice = BigDecimal.ZERO;

        // Розрахунок загальної вартості оренди на основі ціни за день
        for (Costume costume : costumes) {
            BigDecimal costumeTotal = costume.getPricePerDay().multiply(BigDecimal.valueOf(days));
            totalPrice = totalPrice.add(costumeTotal);
        }

        Rental rental =
                Rental.builder()
                        .id(UUID.randomUUID())
                        .userId(user.getId())
                        .startDate(startDate)
                        .endDate(endDate)
                        .totalPrice(totalPrice)
                        .status("ACTIVE")
                        .build();

        // Збереження транзакції (Rental) у базу даних
        rentalRepository.save(rental);

        // Створення та збереження елементів замовлення (RentalItem)
        for (Costume costume : costumes) {
            RentalItem rentalItem =
                    RentalItem.builder()
                            .rentalId(rental.getId())
                            .costumeId(costume.getId())
                            .priceAtRental(costume.getPricePerDay())
                            .build();
            rentalItemRepository.save(rentalItem);
        }

        return rental;
    }
}
