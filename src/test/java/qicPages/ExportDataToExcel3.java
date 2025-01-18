package qicPages;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class ExportDataToExcel3 {
	
	
	
	public static void main(String[] args) {
        String dbUrl = "jdbc:mysql://aura-uat.cwfjz6cyloxy.me-south-1.rds.amazonaws.com:3306";
        String dbUsername = "admin";
        String dbPassword = "zFs4upwKvvpRbbXcKSTf8La3MP4ymd";
        String excelFilePath = "D:\\eclipse workspace\\SME-Calculations\\target\\PremiumCalculator\\Premium Calculator - QIC.xlsx";

        String query1 = "SELECT * FROM 7001_group_medical_qic_transactions.premium WHERE plan_id=665 AND status=1;";
        String query2 = "SELECT * FROM 7001_group_medical_qic_transactions.benefits_table WHERE client_reference_number LIKE '%QIC-SME-FREEZONE-0125-1-00028%';";

        try (FileInputStream fis = new FileInputStream(new File(excelFilePath));
             Workbook workbook = new XSSFWorkbook(fis);
             Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {

            // Execute first query and write to Sheet 1
            writeQueryToSheet(connection, query1, workbook, 0);

            // Execute second query and write to Sheet 2
            writeQueryToSheet(connection, query2, workbook, 1);

            // Save the workbook
            try (FileOutputStream fos = new FileOutputStream(new File(excelFilePath))) {
                workbook.write(fos);
                System.out.println("Data has been written to the Excel file successfully.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeQueryToSheet(Connection connection, String query, Workbook workbook, int sheetIndex) {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            Sheet sheet = workbook.getSheetAt(sheetIndex);
            int rowCount = 1; // Skip the first row

            // Get metadata to handle dynamic columns
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Clear existing data except the first row
            int lastRow = sheet.getLastRowNum();
            for (int i = 1; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    sheet.removeRow(row);
                }
            }

            // Write query results to the sheet
            while (resultSet.next()) {
                Row row = sheet.createRow(rowCount++);
                for (int i = 1; i <= columnCount; i++) {
                    Cell cell = row.createCell(i - 1);
                    Object value = resultSet.getObject(i);

                    if (value instanceof String) {
                        cell.setCellValue((String) value);
                    } else if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else if (value instanceof Boolean) {
                        cell.setCellValue((Boolean) value);
                    } else if (value instanceof Date) {
                        cell.setCellValue(value.toString());
                    } else {
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
                }
            }

            System.out.println("Data from query written to Sheet " + (sheetIndex + 1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
