package lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DB {
    // init database constants
    private static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://147.87.116.89:3306/dataScience";
    private static final String USERNAME = "dataScience";
    private static final String PASSWORD = "dataScience";
    private static final String MAX_POOL = "250";

    // init connection object
    private static Connection connection;
    // init properties object
    private static Properties properties;

    // create properties
    private static Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            properties.setProperty("user", USERNAME);
            properties.setProperty("password", PASSWORD);
            properties.setProperty("MaxPooledStatements", MAX_POOL);
        }
        return properties;
    }

    // connect database
    public static Connection connect() {
        if (connection == null) {
            try {
                Class.forName(DATABASE_DRIVER);
                connection = DriverManager.getConnection(DATABASE_URL, getProperties());
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    // disconnect database
    public static void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // query database
    public static ResultSet doQuery(String query, String... parameters)
    {
        try {

            PreparedStatement statement = connection.prepareStatement(query);

            int parameterCount = 1;

            for(String parameter : parameters)
            {
                statement.setString(parameterCount++, parameter);
            }

            return statement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    //update database
    public static void doUpdate(String query, String... parameters) {

        try {

            PreparedStatement statement = connection.prepareStatement(query);

            int parameterCount = 1;

            for (String parameter : parameters) {
                statement.setString(parameterCount++, parameter);
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}