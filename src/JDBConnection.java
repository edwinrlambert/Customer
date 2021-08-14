import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBConnection {
    // Method to create a database connection.
    public java.sql.Connection connect() throws ClassNotFoundException, SQLException {
        // Call JDBC Driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost/savings", "root", "");
    }
}
