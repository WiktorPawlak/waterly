package pl.lodz.p.it.ssbd2023.ssbd06.integration.config;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseConnector {

    private final Logger log = Logger.getLogger(getClass().getName());

    private final String DB_URL;
    private final String DB_USERNAME;
    private final String DB_PASSWORD;

    private final Connection connection;

    public DatabaseConnector(final String dbPort) {
        DB_URL = "jdbc:postgresql://localhost:" + dbPort + "/ssbd06";
        DB_USERNAME = "ssbd06admin";
        DB_PASSWORD = "12345";
        connection = connect();
    }

    @SneakyThrows
    private Connection connect() {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);;
    }

    public ResultSet executeQuery(String query) {
        ResultSet rs = null;
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            rs = pst.executeQuery();
            rs.next();
        } catch (SQLException e) {
            log.severe("Failed to execute query: " + e.getMessage());
        }
        return rs;
    }

}