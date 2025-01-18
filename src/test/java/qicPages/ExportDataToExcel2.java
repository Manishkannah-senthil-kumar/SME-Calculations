package qicPages;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class ExportDataToExcel2 {
	
    public static void main(String[] args) {
        // Database connection details
        String dbURL = "jdbc:mysql://aura-uat.cwfjz6cyloxy.me-south-1.rds.amazonaws.com:3306";
        String dbUsername = "admin";
        String dbPassword = "zFs4upwKvvpRbbXcKSTf8La3MP4ymd";
        String query = "SELECT * FROM 7001_group_medical_qic_transactions.premium WHERE plan_id=665 AND status=1;";

        // Excel file details
        String excelFilePath = "D:\\eclipse workspace\\SME-Calculations\\target\\PremiumCalculator\\Book1.xlsx";
        int targetSheetIndex = 0; // The index of the sheet where data will be written

        Connection connection = null;

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection
            connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);

            // Execute the query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Load the Excel workbook
            FileInputStream fileInputStream = new FileInputStream(excelFilePath);
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(targetSheetIndex);

            // Determine column count dynamically
            int columnCount = resultSet.getMetaData().getColumnCount();

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
            
            // Trigger formula recalculation
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            evaluator.evaluateAll();

            // Force Excel to recalculate on open
            workbook.setForceFormulaRecalculation(true);

            // Save the workbook
            try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
                workbook.write(outputStream);
            }

            System.out.println("Excel updated and formulas will recalculate on open.");

            // Close the input stream and save changes to the file
            fileInputStream.close();
            FileOutputStream fileOutputStream = new FileOutputStream(excelFilePath);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            workbook.close();

            System.out.println("Data successfully written to Excel sheet.");


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

}
