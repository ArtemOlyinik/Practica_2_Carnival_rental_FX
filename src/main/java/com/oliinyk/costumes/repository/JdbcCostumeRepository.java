package com.oliinyk.costumes.repository;

import com.oliinyk.costumes.infrastructure.DatabaseManager;
import com.oliinyk.costumes.model.Costume;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JdbcCostumeRepository implements CostumeRepository {

    @Override
    public void save(Costume costume) {
        String sql = "INSERT INTO costumes (id, category_id, name, description, price_per_day, image_path) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, costume.getId());
            stmt.setObject(2, costume.getCategoryId());
            stmt.setString(3, costume.getName());
            stmt.setString(4, costume.getDescription());
            stmt.setBigDecimal(5, costume.getPricePerDay());
            stmt.setString(6, costume.getImagePath());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving costume", e);
        }
    }

    @Override
    public Optional<Costume> findById(UUID id) {
        String sql = "SELECT * FROM costumes WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCostume(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding costume", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Costume> findAll() {
        String sql = "SELECT * FROM costumes";
        List<Costume> costumes = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                costumes.add(mapResultSetToCostume(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all costumes", e);
        }
        return costumes;
    }

    @Override
    public void update(Costume costume) {
        String sql = "UPDATE costumes SET category_id = ?, name = ?, description = ?, price_per_day = ?, image_path = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, costume.getCategoryId());
            stmt.setString(2, costume.getName());
            stmt.setString(3, costume.getDescription());
            stmt.setBigDecimal(4, costume.getPricePerDay());
            stmt.setString(5, costume.getImagePath());
            stmt.setObject(6, costume.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating costume", e);
        }
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM costumes WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting costume", e);
        }
    }

    private Costume mapResultSetToCostume(ResultSet rs) throws SQLException {
        return Costume.builder()
                .id(rs.getObject("id", UUID.class))
                .categoryId(rs.getObject("category_id", UUID.class))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .pricePerDay(rs.getBigDecimal("price_per_day"))
                .imagePath(rs.getString("image_path"))
                .build();
    }
}
