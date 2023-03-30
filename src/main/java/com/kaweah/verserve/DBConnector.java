package com.kaweah.verserve;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    /**
     * Connect to the verserve database
     *
     * @return the Connection object
     */
    static Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(Configuration.dbUrl);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


}
