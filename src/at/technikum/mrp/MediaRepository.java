package at.technikum.mrp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** Reads and writes media entries in PostgreSQL. */
public class MediaRepository {

    public List<MediaEntry> findAll() throws SQLException {
        String sql = "SELECT id, title, genre, media_type, duration, seasons FROM media_entries ORDER BY id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<MediaEntry> entries = new ArrayList<>();
            while (resultSet.next()) entries.add(map(resultSet));
            return entries;
        }
    }

    public MediaEntry findById(int id) throws SQLException {
        String sql = "SELECT id, title, genre, media_type, duration, seasons FROM media_entries WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? map(resultSet) : null;
            }
        }
    }

    public int create(String title, String genre, String type, Integer duration, Integer seasons) throws SQLException {
        String sql = "INSERT INTO media_entries (title, genre, media_type, duration, seasons) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, title, genre, type, duration, seasons);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("id");
            }
        }
    }

    public MediaEntry update(int id, String title, String genre, String type, Integer duration, Integer seasons) throws SQLException {
        String sql = "UPDATE media_entries SET title = ?, genre = ?, media_type = ?, duration = ?, seasons = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, title, genre, type, duration, seasons);
            statement.setInt(6, id);
            if (statement.executeUpdate() == 0) return null;
            MediaEntry updated = createModel(type, title, genre, duration, seasons);
            updated.setId(id);
            return updated;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM media_entries WHERE id = ?")) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private void bind(PreparedStatement statement, String title, String genre, String type,
                      Integer duration, Integer seasons) throws SQLException {
        statement.setString(1, title);
        statement.setString(2, genre);
        statement.setString(3, type);
        if (duration == null) statement.setNull(4, Types.INTEGER); else statement.setInt(4, duration);
        if (seasons == null) statement.setNull(5, Types.INTEGER); else statement.setInt(5, seasons);
    }

    private MediaEntry map(ResultSet row) throws SQLException {
        Integer duration = (Integer) row.getObject("duration");
        Integer seasons = (Integer) row.getObject("seasons");
        MediaEntry entry = createModel(row.getString("media_type"), row.getString("title"), row.getString("genre"), duration, seasons);
        entry.setId(row.getInt("id"));
        return entry;
    }

    private MediaEntry createModel(String type, String title, String genre, Integer duration, Integer seasons) {
        if ("Movie".equalsIgnoreCase(type)) return new Movie(title, genre, duration == null ? 0 : duration);
        if ("Series".equalsIgnoreCase(type)) return new Series(title, genre, seasons == null ? 0 : seasons);
        throw new IllegalArgumentException("Unknown media type: " + type);
    }
}
