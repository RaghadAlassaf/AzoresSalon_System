/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package azoressalon_system;

/**
 *
 * @author ragha
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Manages the connection to the MySQL database
public class DatabaseConnection {

    // Database connection information
    private static final String URL = "jdbc:mysql://localhost:3306/azores_salon_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Cpit305";
    // Establishes and returns a connection to the database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}