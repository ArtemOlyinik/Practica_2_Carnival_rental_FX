package com.oliinyk.costumes.service;

import com.oliinyk.costumes.dto.RentalDTO;
import com.oliinyk.costumes.model.Costume;
import com.oliinyk.costumes.model.Rental;
import com.oliinyk.costumes.model.User;
import com.oliinyk.costumes.repository.CostumeRepository;
import com.oliinyk.costumes.repository.RentalRepository;
import com.oliinyk.costumes.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Фасад для роботи з орендами. Реалізує патерн Facade (Вимога розділу 4.3.2). Спрощує доступ до
 * бізнес-логіки для шару представлення.
 */
public class RentalFacade {

    private final RentalService rentalService;
    private final RentalRepository rentalRepository;
    private final CostumeRepository costumeRepository;
    private final UserRepository userRepository;

    public RentalFacade(
            RentalService rentalService,
            RentalRepository rentalRepository,
            CostumeRepository costumeRepository,
            UserRepository userRepository) {
        this.rentalService = rentalService;
        this.rentalRepository = rentalRepository;
        this.costumeRepository = costumeRepository;
        this.userRepository = userRepository;
    }

    /** Оформити замовлення та повернути DTO. */
    public RentalDTO checkout(User user, List<Costume> costumes, LocalDate start, LocalDate end) {
        Rental rental = rentalService.checkout(user, costumes, start, end);
        return mapToDTO(rental);
    }

    /** Отримати всі оренди у вигляді DTO для адмінки. */
    public List<RentalDTO> getAllRentals() {
        return rentalRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /** Отримати оренди конкретного користувача. */
    public List<RentalDTO> getRentalsByUserId(java.util.UUID userId) {
        return rentalRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private RentalDTO mapToDTO(Rental rental) {
        String userEmail =
                userRepository.findById(rental.getUserId()).map(User::getEmail).orElse("Unknown");

        // Реальне отримання імен костюмів (Вимога 4.2.4 про Lazy Initialization)
        List<String> names =
                new com.oliinyk.costumes.repository.JdbcRentalItemRepository()
                        .findByRentalId(rental.getId()).stream()
                        .map(
                                item ->
                                        costumeRepository
                                                .findById(item.getCostumeId())
                                                .map(com.oliinyk.costumes.model.Costume::getName)
                                                .orElse("Видалений костюм"))
                        .collect(Collectors.toList());

        return RentalDTO.builder()
                .id(rental.getId())
                .userId(rental.getUserId())
                .userEmail(userEmail)
                .startDate(rental.getStartDate())
                .endDate(rental.getEndDate())
                .totalPrice(rental.getTotalPrice())
                .status(rental.getStatus())
                .costumeNames(names)
                .build();
    }
}
