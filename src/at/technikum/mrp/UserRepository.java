package at.technikum.mrp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

public class UserRepository {

    public int register(String username, String password) throws SQLException {

        String checkSql = "SELECT id FROM users WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement check = connection.prepareStatement(checkSql)) {

            check.setString(1, username);

            try (ResultSet result = check.executeQuery()) {
                if (result.next()) {
                    throw new IllegalArgumentException("Username already exists");
                }
            }
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        String sql = "INSERT INTO users (username, password) VALUES (?, ?) RETURNING id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, hashedPassword);

            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getInt("id");
            }
        }
    }

    public boolean login(String username, String password) throws SQLException {

        String sql = "SELECT password FROM users WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {

                if (!result.next()) {
                    return false;
                }

                String hashedPassword = result.getString("password");

                return BCrypt.checkpw(password, hashedPassword);
            }
        }
    }

    // Find the ID of a user by their username.
    public int findIdByUsername(String username) throws SQLException {

        String sql = "SELECT id FROM users WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getInt("id");
                }

                return -1;
            }
        }
    }
}