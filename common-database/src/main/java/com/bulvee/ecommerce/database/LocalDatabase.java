package com.bulvee.ecommerce.database;

import java.sql.*;

public class LocalDatabase {
    private final Connection connection;

    public LocalDatabase(String databaseName) throws SQLException {
        String url = "jdbc:sqlite:target/" + databaseName + "db";
        connection = DriverManager.getConnection(url);
    }

    // yes, this is way too generic
    // according kto your database tool, avoid injection.
    public void createIfNotExists(String sql) {
        try {
            this.connection.createStatement().execute(sql);
        } catch (SQLException e) {
            // be careful, the sql could be wrong, be really careful. It's a sample
            e.printStackTrace();
        }
    }

    public void update(String statement, String... params) throws SQLException {
        var preparedStatement = connection.prepareStatement(statement);
        prepare(preparedStatement, params);
        preparedStatement.execute();
    }

    private void prepare(PreparedStatement preparedStatement, String[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setString(i + 1, params[i]);
        }
    }

    public ResultSet query(String statement, String... params) throws SQLException {
        var preparedStatement = connection.prepareStatement(statement);
        prepare(preparedStatement, params);
        return preparedStatement.executeQuery();
    }
}
