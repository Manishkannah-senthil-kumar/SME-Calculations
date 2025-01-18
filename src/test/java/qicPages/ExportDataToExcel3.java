package qicPages;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class ExportDataToExcel3 {
	
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
        String excelFilePath = "D:\\eclipse workspace\\SME-Calculations\\target\\PremiumCalculator\\Book1.xlsx";

        Connection connection = null;

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection
            connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);

            // Load the Excel workbook
            FileInputStream fileInputStream = new FileInputStream(excelFilePath);
            Workbook workbook = new XSSFWorkbook(fileInputStream);

            // Write data from query 1 to Sheet 0
            writeQueryToSheet(connection, query1, workbook,0 ); // Target Sheet 1 (index 0)

            // Write data from query 2 to Sheet 1
            writeQueryToSheet(connection, query2, workbook, 1); // Target Sheet 2 (index 1)
            
            // Write data from query 3 to sheet4
            writeQueryToSheet(connection, query3, workbook, 3); // Target Sheet 3 (index 3)
           // Write data from query 4 to sheet5
            writeQueryToSheet(connection, query4, workbook, 4); // Target Sheet 4 (index 4)
          // Write data from query 5 to sheet6
            writeQueryToSheet(connection, query5, workbook, 5); // Target Sheet 5 (index 5)
         // Write data from query 6 to sheet7
            writeQueryToSheet(connection, query6, workbook, 6); // Target Sheet 6 (index 6)

            // Trigger formula recalculation
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            evaluator.evaluateAll();

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
     * Executes a SQL query and writes the results to a specific sheet in the workbook.
     */
    private static void writeQueryToSheet(Connection connection, String query, Workbook workbook, int sheetIndex) {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            Sheet sheet = workbook.getSheetAt(sheetIndex);

            // Determine column count dynamically
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

//            // Clear existing data except the first row
//            int lastRow = sheet.getLastRowNum();
//            for (int i = 1; i <= lastRow; i++) {
//                Row row = sheet.getRow(i);
//                if (row != null) {
//                    sheet.removeRow(row);
//                }
//            }

            // Start writing data to the sheet, leaving the first row untouched
            int rowCount = 1; // Start from row 2 (index 1) in Excel
            while (resultSet.next()) {
                Row row = sheet.getRow(rowCount);
                if (row == null) {
                    row = sheet.createRow(rowCount); // Create a new row if it doesn't exist
                }

                // Populate the row with data from the ResultSet
                for (int i = 1; i <= columnCount; i++) {
                    Cell cell = row.createCell(i - 1);
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

            System.out.println("Data from query written to Sheet " + (sheetIndex + 1));
            
            

        } catch (SQLException | NullPointerException e) {
            System.out.println("Error writing data to Excel sheet: " + e.getMessage());
            e.printStackTrace();
        }
    }
	

}
