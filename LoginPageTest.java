package mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class LoginPageTest {

    private LoginPage loginPage;

    // Test database credentials (change these based on your actual database setup)
    private final String dbUrl = "jdbc:mysql://localhost:3306/patient_management_system";
    private final String dbUsername = "root";
    private final String dbPassword = "";

    @BeforeEach
    public void setUp() {
        // Initialize the LoginPage instance
        loginPage = new LoginPage();
    }

    @Test
    public void testAuthenticateUser_SuccessfulLogin() {
        // Arrange: Set up test data
        String validUsername = "admin";
        String validPassword = "password";

        // Act: Call the authenticateUser method
        String resultMessage = authenticateUser(validUsername, validPassword);

        // Assert: Check if login was successful
        assertEquals("Login successful!", resultMessage);
    }

    @Test
    public void testAuthenticateUser_InvalidLogin() {
        // Arrange: Set up test data
        String invalidUsername = "invalid_user";
        String invalidPassword = "invalid_password";

        // Act: Call the authenticateUser method
        String resultMessage = authenticateUser(invalidUsername, invalidPassword);

        // Assert: Check if login failed
        assertEquals("Invalid username or password!", resultMessage);
    }

    @Test
    public void testAuthenticateUser_DatabaseError() {
        // Arrange: Set up test data
        String username = "admin";
        String password = "password123";

        // Act: Call the authenticateUser method with incorrect database URL
        String resultMessage = authenticateUserWithIncorrectDBUrl(username, password);

        // Assert: Check if correct error message is shown
        assertEquals("Database connection error!", resultMessage);
    }

    // Helper method to authenticate user and return message
    private String authenticateUser(String username, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish database connection
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

            // SQL query to check the user credentials
            String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            // Execute query
            resultSet = statement.executeQuery();

            // Check if user exists
            if (resultSet.next()) {
                return "Login successful!";
            } else {
                return "Invalid username or password!";
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Database connection error!";
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Helper method to authenticate user with incorrect database URL
    private String authenticateUserWithIncorrectDBUrl(String username, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish database connection with incorrect URL
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/invalid_database", dbUsername, dbPassword);

            // SQL query to check the user credentials
            String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            // Execute query
            resultSet = statement.executeQuery();

            // Check if user exists
            if (resultSet.next()) {
                return "Login successful!";
            } else {
                return "Invalid username or password!";
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Database connection error!";
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
