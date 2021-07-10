package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

public class ConnectionPool {
   private final String connectionUrl;

    private static final int INITIAL_CAPACITY = 6;
    private LinkedList<Connection> pool = new LinkedList<Connection>();

    public ConnectionPool(String connectionUrl) throws SQLException {
        this.connectionUrl = connectionUrl;
        for (int i = 0; i < INITIAL_CAPACITY; i++) {
            pool.add(DriverManager.getConnection(connectionUrl));
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        if (pool.isEmpty()) {
            pool.add(DriverManager.getConnection(connectionUrl));
        }
        return pool.pop();
    }

    public synchronized void returnConnection(Connection connection) {
        pool.push(connection);
    }
}
