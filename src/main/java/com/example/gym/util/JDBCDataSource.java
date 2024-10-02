package com.example.gym.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class JDBCDataSource {
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/GymDB";
    private static final String USER = "postgres";
    private static final String PASS = "1234";

    public static Connection getConnection() throws Exception {

        Class.forName(JDBC_DRIVER);

        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASS);

        return DriverManager.getConnection(DB_URL, props);
    }
}
