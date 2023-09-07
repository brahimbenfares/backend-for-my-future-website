package DAO;

import Model.Admin;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;


import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AdminDAO {

    private static final Logger logger = LoggerFactory.getLogger(AdminDAO.class);
    private static String DB_URL;
    private static String DB_USERNAME;
    private static String DB_PASSWORD;
    
    static {
        // Load DB credentials from config
        Properties properties = new Properties();
        try (InputStream input = AdminDAO.class.getClassLoader().getResourceAsStream("dbconfig.properties")) {
            properties.load(input);
            DB_URL = properties.getProperty("dbUrl");
            DB_USERNAME = properties.getProperty("dbUsername");
            DB_PASSWORD = properties.getProperty("dbPassword");

            // Test the connection
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                logger.info("Successfully connected to database in AdminDAO.");
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error("Failed to connect to the database in AdminDAO.");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read DB credentials from config", ex);
        }
    }

    private Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Admin getAdminByLogin(String username, String password) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT * FROM admins WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String storedHash = resultSet.getString("password");
                if (BCrypt.checkpw(password, storedHash)) { // Password checking
                    return new Admin(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"), // You might want to change this
                        resultSet.getBoolean("is_manager"),
                        resultSet.getString("permissions")
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving admin by login: ", e);
        }
        return null;
    }

    public boolean doesAdminExist(String username) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT COUNT(*) AS count FROM admins WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking if admin exists: ", e);
        }
        return false;
    }

    public boolean createNewAdmin(Admin admin) {
        try (Connection connection = getConnection()) {
            String sql = "INSERT INTO admins (username, password, is_manager, permissions) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Hash the password before storing it
            String hashedPassword = BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt());
            
            preparedStatement.setString(1, admin.getUsername());
            preparedStatement.setString(2, hashedPassword); // Storing hashed password
            preparedStatement.setBoolean(3, admin.getisManager());
            preparedStatement.setString(4, admin.getPermissions());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error creating new admin: ", e);
        }
        return false;
    }

    public boolean updateAdminPermissions(String username, String permissions) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE admins SET permissions = ? WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, permissions);
            preparedStatement.setString(2, username);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating admin permissions: ", e);
        }
        return false;
    }

    public boolean deleteAdmin(String username) {
        try (Connection connection = getConnection()) {
            String sql = "DELETE FROM admins WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting admin: ", e);
        }
        return false;
    }

// In AdminDAO.java

public List<Admin> getAllAdmins() {
    List<Admin> admins = new ArrayList<>();
    try (Connection connection = getConnection()) {
        String sql = "SELECT * FROM admins";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            admins.add(new Admin(
                resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getBoolean("is_manager"),
                resultSet.getString("permissions")
            ));
        }
    } catch (SQLException e) {
        logger.error("Error retrieving all admins: ", e);
    }
    return admins;
}
}