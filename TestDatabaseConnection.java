import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDatabaseConnection {
    public static void main(String[] args) {
        // URL for SQLite (no need for username/password)
        String sqliteUrl = "jdbc:sqlite::memory:";  // In-memory database (no actual DB file)
        
        // URL for MySQL (you can comment this out if not testing MySQL)
        // String mysqlUrl = "jdbc:mysql://localhost:3306/test"; // Replace with your MySQL URL
        
        // Test SQLite connection
        testConnection("jdbc:sqlite:C:/Users/evara/OneDrive/Desktop/Flashcardsapp/database.db"
);

        // Test MySQL connection (comment this out if you don't have MySQL running)
        // testConnection(mysqlUrl);
    }

    private static void testConnection(String url) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connection successful to database: " + url);
            }
        } catch (SQLException e) {
            System.out.println("Connection failed to database: " + url);
            System.out.println("Error: " + e.getMessage());
        }
    }
}
