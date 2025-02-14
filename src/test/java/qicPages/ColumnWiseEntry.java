package qicPages;
import java.io.*;
import java.sql.*;
import java.util.Date;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ColumnWiseEntry {
	
    public static void main(String[] args) {
        // Database connection details
        String dbURL = "jdbc:mysql://aura-uat.cwfjz6cyloxy.me-south-1.rds.amazonaws.com:3306";
        String dbUsername = "admin";
        String dbPassword = "zFs4upwKvvpRbbXcKSTf8La3MP4ymd";
        
        
        

        // Queries to execute
        String query1 = "SELECT * FROM 7001_group_medical_qic_transactions.premium WHERE plan_id=665 AND status=1;";
        String query2 = "SELECT * FROM 7001_group_medical_qic_transactions.benefits_table WHERE client_reference_number LIKE '%QIC-SME-FREEZONE-0125-1-00028%';";
        String query3 = "SELECT ng.group_name,ng.loading_discount,n.nationality FROM uw_rules_FreezoneScheme_Dubai_NAS_transactions.nationality_group_mapping gm LEFT JOIN uw_rules_FreezoneScheme_Dubai_NAS_transactions.nationality_group ng ON ng.nationality_group_id = gm.nationality_group_id LEFT JOIN uw_rules_FreezoneScheme_Dubai_NAS_transactions.nationality n ON n.nationality_id = gm.nationality_id WHERE gm.version_id=7";
        String query4 = "SELECT im.industry_name,ig.loading_discount FROM uw_rules_FreezoneScheme_Dubai_NAS_transactions.industry_group_mapping igm LEFT JOIN uw_rules_FreezoneScheme_Dubai_NAS_transactions.industry_group ig ON ig.industry_group_id = igm.industry_group_id LEFT JOIN uw_rules_FreezoneScheme_Dubai_NAS_transactions.industry_master im ON im.industry_master_id =igm.industry_master_id WHERE  igm.version_id=7;";
        String query5 = "SELECT pgm.previous_insurer_group_id, pg.group_name,pg.loading_discount, pim.previous_insurer_name FROM uw_rules_FreezoneScheme_Dubai_NAS_transactions.previous_insurer_group_mapping pgm LEFT JOIN uw_rules_FreezoneScheme_Dubai_NAS_transactions.previous_insurer_group pg ON pg.previous_insurer_group_id = pgm.previous_insurer_group_id LEFT JOIN uw_rules_FreezoneScheme_Dubai_NAS_transactions.previous_insurer_master pim ON pim.previous_insurer_master_id = pgm.previous_insurer_master_id WHERE  pgm.version_id=7; ";
        String query6 = "SELECT insurer_fee,tpa_fee,aura_commission,distributor_commission,member_type,total FROM 7001_group_medical_qic_transactions.ceding_commission where plan_id=665; ";
 
        // Excel file details
        String excelFilePath = "D:\\eclipse workspace\\SME-Calculations\\target\\PremiumCalculator\\Master Piece.xlsx";

        Connection connection = null;

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection
            connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);

            // Load the Excel workbook
            FileInputStream fileInputStream = new FileInputStream(excelFilePath);
            Workbook workbook = new XSSFWorkbook(fileInputStream);

            // Write data from query 1 to Sheet 0 (column range A to C)
            writeQueryToSheet(connection, query1, workbook, 0, 0, 13);

            // Write data from query 2 to Sheet 1 (column range A to Y)
            writeQueryToSheet(connection, query2, workbook, 1, 0, 24);

            // Write data from query 3 to Sheet 3 (column range A to C)
           writeQueryToSheet(connection, query3, workbook, 4, 0, 2);
           
           // Write data from query 4 to Sheet 3 (column range E to F)
          writeQueryToSheet(connection, query4, workbook, 4, 4, 5);
          
          // Write data from query 5 to Sheet 3 (column range H to J)
         writeQueryToSheet(connection, query5, workbook, 4, 7, 10);
         
         // Write data from query 6 to Sheet 4 (column range A to D)
        writeQueryToSheet(connection, query6, workbook, 5, 0, 5);

            // Trigger formula recalculation
            try {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                evaluator.evaluateAll(); // Evaluate formulas in the workbook
            } catch (NullPointerException e) {
                System.out.println("Warning: Skipping formula evaluation due to external workbook references.");
            }

            // Force Excel to recalculate on open
            workbook.setForceFormulaRecalculation(true);

            // Save the workbook
            try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
                workbook.write(outputStream);
            }

            fileInputStream.close();
            workbook.close();

            System.out.println("Data successfully written to Excel sheets and formulas will recalculate on open.");

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close the database connection
            try {
                if (connection != null) {
                    connection.close();
                    System.out.println("Database connection closed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Executes a SQL query and writes the results to a specific sheet in the workbook
     * within a specified column range.
     */
    private static void writeQueryToSheet(Connection connection, String query, Workbook workbook, int sheetIndex, int startColumn, int endColumn) {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            Sheet sheet = workbook.getSheetAt(sheetIndex);

            // Get column metadata from ResultSet
            ResultSetMetaData metaData = resultSet.getMetaData();
            int resultSetColumnCount = metaData.getColumnCount();

            // Ensure column range is valid
            if (endColumn - startColumn + 1 != resultSetColumnCount) {
                throw new IllegalArgumentException("Column range does not match the number of ResultSet columns.");
            }

            // Start writing data to the sheet, leaving the first row untouched
            int rowCount = 1; // Start from row 2 (index 1) in Excel
            while (resultSet.next()) {
                Row row = sheet.getRow(rowCount);
                if (row == null) {
                    row = sheet.createRow(rowCount); // Create a new row if it doesn't exist
                }

                // Populate the row with data from the ResultSet within the column range
                for (int i = 1; i <= resultSetColumnCount; i++) {
                    int sheetColumnIndex = startColumn + (i - 1); // Map ResultSet column to Excel column
                    Cell cell = row.createCell(sheetColumnIndex);
                    Object value = resultSet.getObject(i);

                    // Write value based on its type
                    if (value instanceof String) {
                        cell.setCellValue((String) value);
                    } else if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else if (value instanceof Boolean) {
                        cell.setCellValue((Boolean) value);
                    } else if (value instanceof Date) {
                        cell.setCellValue(value.toString()); // Format date as string
                    } else {
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
                }

                rowCount++;
            }

            System.out.println("Data from query written to Sheet " + (sheetIndex + 1) + " in columns " +
                               (startColumn + 1) + " to " + (endColumn + 1));

        } catch (SQLException | NullPointerException e) {
            System.out.println("Error writing data to Excel sheet: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
