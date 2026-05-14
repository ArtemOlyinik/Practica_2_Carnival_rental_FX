package com.oliinyk.costumes.repository;

import com.oliinyk.costumes.infrastructure.DatabaseManager;
import com.oliinyk.costumes.model.RentalItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** JDBC реалізація репозиторію елементів оренди. Підтримує транзакційність. */
public class JdbcRentalItemRepository implements RentalItemRepository {

    @Override
    public void save(RentalItem item) {
        try (Connection conn = DatabaseManager.getConnection()) {
            save(item, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при збереженні елемента оренди", e);
        }
    }

    @Override
    public void save(RentalItem item, Connection conn) {
        String sql =
                "INSERT INTO rental_items (rental_id, costume_id, price_at_rental) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, item.getRentalId());
            stmt.setObject(2, item.getCostumeId());
            stmt.setBigDecimal(3, item.getPriceAtRental());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при збереженні елемента оренди в транзакції", e);
        }
    }

    @Override
    public List<RentalItem> findByRentalId(UUID rentalId) {
        String sql = "SELECT * FROM rental_items WHERE rental_id = ?";
        List<RentalItem> items = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, rentalId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToRentalItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при пошуку елементів оренди", e);
        }
        return items;
    }

    private RentalItem mapResultSetToRentalItem(ResultSet rs) throws SQLException {
        return RentalItem.builder()
                .rentalId(rs.getObject("rental_id", UUID.class))
                .costumeId(rs.getObject("costume_id", UUID.class))
                .priceAtRental(rs.getBigDecimal("price_at_rental"))
                .build();
    }
}
