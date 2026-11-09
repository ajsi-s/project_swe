
package at.technikum.mrp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingRepository {

    public int create(int userId, int mediaId, int stars) throws SQLException {
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Stars must be between 1 and 5");
        }

        String sql = "INSERT INTO ratings (user_id, media_id, stars) VALUES (?, ?, ?) RETURNING id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setInt(2, mediaId);
            statement.setInt(3, stars);

            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getInt("id");
            }
        }
    }

    public List<Object> findAll() throws SQLException {
        String sql = """
                SELECT r.id, r.user_id, u.username,
                       r.media_id, m.title, r.stars
                FROM ratings r
                JOIN users u ON r.user_id = u.id
                JOIN media_entries m ON r.media_id = m.id
                ORDER BY r.id
                """;

        List<Object> ratings = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                java.util.Map<String, Object> rating = new java.util.LinkedHashMap<>();
                rating.put("id", result.getInt("id"));
                rating.put("userId", result.getInt("user_id"));
                rating.put("username", result.getString("username"));
                rating.put("mediaId", result.getInt("media_id"));
                rating.put("mediaTitle", result.getString("title"));
                rating.put("stars", result.getInt("stars"));
                ratings.add(rating);
            }
        }

        return ratings;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM ratings WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }
}
