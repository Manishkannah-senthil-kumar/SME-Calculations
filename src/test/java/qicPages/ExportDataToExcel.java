package qicPages;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ExportDataToExcel {
	
	public static void main(String[] args) {
        // Database connection details
        String dbURL = "jdbc:mysql://aura-uat.cwfjz6cyloxy.me-south-1.rds.amazonaws.com:3306";
        String dbUsername = "admin";
        String dbPassword = "zFs4upwKvvpRbbXcKSTf8La3MP4ymd";
// Test
        // Query to fetch data
        String query = "SELECT * FROM 7001_group_medical_qic_transactions.premium WHERE plan_id=665 AND status=1;";

        // Connection object
        Connection connection = null;

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection
            System.out.println("Connecting to the database...");
            connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
            System.out.println("Connection established successfully!");

            // Create a statement
            Statement statement = connection.createStatement();

            // Execute the query
            System.out.println("Executing query: " + query);
            ResultSet resultSet = statement.executeQuery(query);

            // Print the column names
            System.out.println("Fetching data...");
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(resultSet.getMetaData().getColumnName(i) + "\t");
            }
            System.out.println(); // New line after column headers

            // Process and print each row
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getString(i) + "\t");
                }
                System.out.println(); // New line after each row
            }

            // Close ResultSet and Statement
            resultSet.close();
            statement.close();
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found! Make sure to add it to the classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close the connection
            try {
                if (connection != null) {
                    connection.close();
                    System.out.println("Connection closed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
