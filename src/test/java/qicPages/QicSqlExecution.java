package qicPages;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class QicSqlExecution {
	
	public static void main(String[] args) throws SQLException {
	    // Database connection details
	    String dbURL = "jdbc:mysql://aura-uat.cwfjz6cyloxy.me-south-1.rds.amazonaws.com:3306";
	    String dbUsername = "admin";
	    String dbPassword = "zFs4upwKvvpRbbXcKSTf8La3MP4ymd";

	    // Queries to execute
	    Connection connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);

	    // Create a statement object
	    Statement statement = connection.createStatement();

	    // Define the SQL query
	    String sql = "WITH ActiveVersion AS ( \n" +
	        "  SELECT pv.id \n" +
	        "  FROM 7002_group_medical_dni_transactions.product_versions pv \n" +
	        "  WHERE pv.status = 1 AND pv.effective_date <= CURRENT_TIMESTAMP \n" +
	        "  ORDER BY pv.effective_date DESC \n" +
	        "  LIMIT 1 \n" +
	        ") \n" +
	        "SELECT \n" +
	        "  pv.id, \n" +
	        "  pv.name, \n" +
	        "  pv.description, \n" +
	        "  pv.effective_date AS effectiveDate, \n" +
	        "  pv.base_version AS baseVersion, \n" +
	        "  pv.cloning_success AS cloningSuccess, \n" +
	        "  version.name AS baseVersionName, \n" +
	        "  pv.status, \n" +
	        "  CASE \n" +
	        "    WHEN pv.effective_date > CURRENT_TIMESTAMP THEN true \n" +
	        "    ELSE false \n" +
	        "  END AS isEditable, \n" +
	        "  CASE \n" +
	        "    WHEN pv.effective_date > CURRENT_TIMESTAMP THEN 'Up Coming' \n" +
	        "    WHEN pv.id = (SELECT id FROM ActiveVersion) THEN 'Active' \n" +
	        "    ELSE 'Expired' \n" +
	        "  END AS versionStatus \n" +
	        "FROM 7002_group_medical_dni_transactions.product_versions pv \n" +
	        "LEFT JOIN \n" +
	        "  7002_group_medical_dni_transactions.product_versions version \n" +
	        "ON \n" +
	        "  pv.base_version = version.id \n" +
	        "WHERE pv.status = 1";

	    // Execute the query and get the results
	    ResultSet resultSet = statement.executeQuery(sql);

	    // Variable to store the ID of the active version
	    int activeVersionId = -1;

	    // Process the results
	    while (resultSet.next()) {
	        int id = resultSet.getInt("id");
	        String versionStatus = resultSet.getString("versionStatus");

	        if ("Active".equals(versionStatus)) {
	            activeVersionId = id;
	            break; // Exit the loop once the active version ID is found
	        }
	    }

	    // Print the active version ID
	    if (activeVersionId != -1) {
	        System.out.println("Active Version ID: " + activeVersionId);
	    } else {
	        System.out.println("No active version found.");
	    }

	}

}
