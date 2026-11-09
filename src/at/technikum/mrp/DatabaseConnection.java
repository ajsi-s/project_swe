
package at.technikum.mrp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            setting("DB_URL", "jdbc:postgresql://localhost:5432/media_ratings_db");

    private static final String USER =
            setting("DB_USER", "postgres");

    private static final String PASSWORD =
            setting("DB_PASSWORD", "mysecretpassword");

    private static String setting(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
