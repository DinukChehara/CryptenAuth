package me.tomqnto.cryptenAuth.data;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLDataManager {

    /**
     * Establishes a connection to the database.
     * @return A Connection object.
     * @throws SQLException if a database access error occurs.
     */
    Connection getConnection() throws SQLException;

    /**
     * Initializes the database schema (e.g., creates tables if they don't exist).
     */
    void initializeDatabase();

    /**
     * Closes the database connection.
     */
    void closeConnection();
}
