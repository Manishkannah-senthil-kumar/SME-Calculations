package qicPages;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QICSQLExecutionGetBasePremium {
	
//	public static void main(String[] args) throws SQLException {
//	    // Database connection details
//	    String dbURL = "jdbc:mysql://aura-uat.cwfjz6cyloxy.me-south-1.rds.amazonaws.com:3306";
//	    String dbUsername = "admin";
//	    String dbPassword = "zFs4upwKvvpRbbXcKSTf8La3MP4ymd";
//
//	    // Queries to execute
//	    Connection connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
//
//	    // Create a statement object
//	    Statement statement = connection.createStatement();
//
//	    // Step 1 Define the SQL query To Find Active Verison
//	    String sql = "WITH ActiveVersion AS ( \n" +
//	        "  SELECT pv.id \n" +
//	        "  FROM 7002_group_medical_dni_transactions.product_versions pv \n" +
//	        "  WHERE pv.status = 1 AND pv.effective_date <= CURRENT_TIMESTAMP \n" +
//	        "  ORDER BY pv.effective_date DESC \n" +
//	        "  LIMIT 1 \n" +
//	        ") \n" +
//	        "SELECT \n" +
//	        "  pv.id, \n" +
//	        "  pv.name, \n" +
//	        "  pv.description, \n" +
//	        "  pv.effective_date AS effectiveDate, \n" +
//	        "  pv.base_version AS baseVersion, \n" +
//	        "  pv.cloning_success AS cloningSuccess, \n" +
//	        "  version.name AS baseVersionName, \n" +
//	        "  pv.status, \n" +
//	        "  CASE \n" +
//	        "    WHEN pv.effective_date > CURRENT_TIMESTAMP THEN true \n" +
//	        "    ELSE false \n" +
//	        "  END AS isEditable, \n" +
//	        "  CASE \n" +
//	        "    WHEN pv.effective_date > CURRENT_TIMESTAMP THEN 'Up Coming' \n" +
//	        "    WHEN pv.id = (SELECT id FROM ActiveVersion) THEN 'Active' \n" +
//	        "    ELSE 'Expired' \n" +
//	        "  END AS versionStatus \n" +
//	        "FROM 7002_group_medical_dni_transactions.product_versions pv \n" +
//	        "LEFT JOIN \n" +
//	        "  7002_group_medical_dni_transactions.product_versions version \n" +
//	        "ON \n" +
//	        "  pv.base_version = version.id \n" +
//	        "WHERE pv.status = 1";
//
//	    // Execute the query and get the results
//	    ResultSet resultSet = statement.executeQuery(sql);
//
//	    // Variable to store the ID of the active version
//	    int activeVersionId = -1;
//
//	    // Process the results
//	    while (resultSet.next()) {
//	        int id = resultSet.getInt("id");
//	        String versionStatus = resultSet.getString("versionStatus");
//
//	        if ("Active".equals(versionStatus)) {
//	            activeVersionId = id;
//	            break; // Exit the loop once the active version ID is found
//	        }
//	    }
//
//	    // Print the active version ID
//	    if (activeVersionId != -1) {
//	        System.out.println("Active Version ID: " + activeVersionId);
//	    } else {
//	        System.out.println("No active version found.");
//	    }
//	    
//        // Query to get the group ID for group name = 'Aafiya Sme' and matching version ID
//        String groupQuery = "SELECT * FROM 7002_group_medical_dni_transactions.group \n" +
//            "WHERE status = 1";
//
//        // Execute the query to get group details
//        resultSet = statement.executeQuery(groupQuery);
//        
//        String GroupName = "Aafiya Sme"; // We are entering Group name to get the group ID
//
//        int groupId = -1;
//        while (resultSet.next()) {
//            String groupName = resultSet.getString("group_name");
//            int versionId = resultSet.getInt("version_id");
//
//            if (GroupName.equals(groupName) && versionId == activeVersionId) {
//                groupId = resultSet.getInt("id");
//                break;
//            }
//        }
//
//        if (groupId != -1) {
//            System.out.println("Group ID for"+" "+ GroupName+":"+ + groupId);
//        } else {
//            System.out.println("No matching group found for 'Aafiya Sme' with the active version ID.");
//        }
//        
//     // Query to get records from emirate table for the group_id
//        
//        String emirateQuery = "SELECT * FROM 7002_group_medical_dni_transactions.emirate WHERE group_id = " + groupId+";";
//        System.out.println(emirateQuery);
//        
//        resultSet = statement.executeQuery(emirateQuery);
//        
//     // Step 3: Get Emirate ID where group_id = 157 and emirate_name = 'Dubai'
//      // String EmirateName = "Dubai";
//       
//       
//       // Step 4: Store "Dubai" in a String variable and use it in the query
//       String emirateName = "Dubai";  // Storing "Dubai" in a string variable
//
//       // Query to get Emirate ID using the stored string
//       String emirateQuery3 = "SELECT id FROM 7002_group_medical_dni_transactions.emirate WHERE group_id = ? AND emirate_name = ?";
//       PreparedStatement emirateStmt = connection.prepareStatement(emirateQuery);
//       emirateStmt.setInt(1, 157); // Hardcoded as per request, you can replace with groupId if needed
//       emirateStmt.setString(2, emirateName); // Passing "Dubai" as an object
//       
//       System.out.println(emirateQuery3);
//
//       resultSet = emirateStmt.executeQuery();
//       int emirateId = -1;
//
//       if (resultSet.next()) {
//           emirateId = resultSet.getInt("id");
//       }
//
//       System.out.println(emirateId != -1 ? "Emirate ID for " + emirateName + ": " + emirateId : "No matching emirate found.");
//       
//
//
//        // Close resources
//        resultSet.close();
//        statement.close();
//        connection.close();
//
//
//	}

    public static void main(String[] args) throws SQLException {
        // Database connection details
        String dbURL = "jdbc:mysql://aura-uat.cwfjz6cyloxy.me-south-1.rds.amazonaws.com:3306";
        String dbUsername = "admin";
        String dbPassword = "zFs4upwKvvpRbbXcKSTf8La3MP4ymd";

        // Establish connection
        Connection connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);

        // Create a statement object
        Statement statement = connection.createStatement();

        // Step 1: Define and execute the SQL query to find the Active Version
        String sql = "WITH ActiveVersion AS ( " +
                "  SELECT pv.id " +
                "  FROM 7002_group_medical_dni_transactions.product_versions pv " +
                "  WHERE pv.status = 1 AND pv.effective_date <= CURRENT_TIMESTAMP " +
                "  ORDER BY pv.effective_date DESC " +
                "  LIMIT 1 " +
                ") " +
                "SELECT pv.id, pv.name, pv.description, pv.effective_date AS effectiveDate, " +
                "  pv.base_version AS baseVersion, pv.cloning_success AS cloningSuccess, " +
                "  version.name AS baseVersionName, pv.status, " +
                "  CASE WHEN pv.effective_date > CURRENT_TIMESTAMP THEN true ELSE false END AS isEditable, " +
                "  CASE " +
                "    WHEN pv.effective_date > CURRENT_TIMESTAMP THEN 'Up Coming' " +
                "    WHEN pv.id = (SELECT id FROM ActiveVersion) THEN 'Active' " +
                "    ELSE 'Expired' " +
                "  END AS versionStatus " +
                "FROM 7002_group_medical_dni_transactions.product_versions pv " +
                "LEFT JOIN 7002_group_medical_dni_transactions.product_versions version " +
                "ON pv.base_version = version.id " +
                "WHERE pv.status = 1";

        ResultSet resultSet = statement.executeQuery(sql);

        // Step 2: Get Active Version ID
        int activeVersionId = -1;
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String versionStatus = resultSet.getString("versionStatus");
            if ("Active".equals(versionStatus)) {
                activeVersionId = id;
                break;
            }
        }

        System.out.println("Active Version ID: " + (activeVersionId != -1 ? activeVersionId : "No active version found."));

        // Step 3: Get Group ID for "Aafiya Sme"
        String groupQuery = "SELECT id FROM 7002_group_medical_dni_transactions.group WHERE group_name = ? AND version_id = ?";
        PreparedStatement groupStmt = connection.prepareStatement(groupQuery);
        groupStmt.setString(1, "Aafiya Sme");
        groupStmt.setInt(2, activeVersionId);
        resultSet = groupStmt.executeQuery();

        int groupId = -1;
        if (resultSet.next()) {
            groupId = resultSet.getInt("id");
        }

        System.out.println(groupId != -1 ? "Group ID for Aafiya Sme: " + groupId : "No matching group found.");

        // Step 4: Store "Dubai" in a String variable and use it in the query
        String emirateName = "Dubai";  // Storing "Dubai" in a string variable

        // Query to get Emirate ID using the stored string
        String emirateQuery = "SELECT id FROM 7002_group_medical_dni_transactions.emirate WHERE group_id = ? AND emirate_name = ?";
        PreparedStatement emirateStmt = connection.prepareStatement(emirateQuery);
        emirateStmt.setInt(1, 157); // Hardcoded as per request, you can replace with groupId if needed
        emirateStmt.setString(2, emirateName); // Passing "Dubai" as an object

        resultSet = emirateStmt.executeQuery();
        int emirateId = -1;

        if (resultSet.next()) {
            emirateId = resultSet.getInt("id");
        }

        System.out.println(emirateId != -1 ? "Emirate ID for " + emirateName + ": " + emirateId : "No matching emirate found.");
        
        System.out.println(emirateQuery);

        // Close resources
        resultSet.close();
        statement.close();
        groupStmt.close();
        emirateStmt.close();
        connection.close();
    }

}
