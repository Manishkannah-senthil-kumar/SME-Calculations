package qicPages;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.sql.*;

public class OLDCALC {
	
	
    public static void main(String[] args) {
        // Database connection details
        String dbURL = "jdbc:mysql://aura-uat.cwfjz6cyloxy.me-south-1.rds.amazonaws.com:3306";
        String dbUsername = "admin";
        String dbPassword = "zFs4upwKvvpRbbXcKSTf8La3MP4ymd";
        
        // Queries to execute
        String[] queries = {
            "SELECT * FROM 7001_group_medical_qic_transactions.premium WHERE plan_id=666 AND status=1;",
            "SELECT * FROM 7001_group_medical_qic_transactions.benefits_table WHERE client_reference_number LIKE '%QIC-SME-FREEZONE-0125-1-00028%';",
            "SELECT ng.group_name, ng.loading_discount, n.nationality FROM uw_rules_FreezoneScheme_Dubai_NAS_transactions.nationality_group_mapping gm LEFT JOIN uw_rules_FreezoneScheme_Dubai_NAS_transactions.nationality_group ng ON ng.nationality_group_id = gm.nationality_group_id LEFT JOIN uw_rules_FreezoneScheme_Dubai_NAS_transactions.nationality n ON n.nationality_id = gm.nationality_id WHERE gm.version_id=7;",
            "SELECT im.industry_name, ig.loading_discount FROM uw_rules_FreezoneScheme_Dubai_NAS_transactions.industry_group_mapping igm LEFT JOIN uw_rules_FreezoneScheme_Dubai_NAS_transactions.industry_group ig ON ig.industry_group_id = igm.industry_group_id LEFT JOIN uw_rules_FreezoneScheme_Dubai_NAS_transactions.industry_master im ON im.industry_master_id = igm.industry_master_id WHERE igm.version_id=7;",
            "SELECT pgm.previous_insurer_group_id, pg.group_name, pg.loading_discount, pim.previous_insurer_name FROM uw_rules_FreezoneScheme_Dubai_NAS_transactions.previous_insurer_group_mapping pgm LEFT JOIN uw_rules_FreezoneScheme_Dubai_NAS_transactions.previous_insurer_group pg ON pg.previous_insurer_group_id = pgm.previous_insurer_group_id LEFT JOIN uw_rules_FreezoneScheme_Dubai_NAS_transactions.previous_insurer_master pim ON pim.previous_insurer_master_id = pgm.previous_insurer_master_id WHERE pgm.version_id=7;",
            "SELECT insurer_fee, tpa_fee, aura_commission, distributor_commission, member_type, total FROM 7001_group_medical_qic_transactions.ceding_commission WHERE plan_id=665;"
        };

        // Excel file path
        String excelFilePath = "D:\\eclipse workspace\\SME-Calculations\\target\\PremiumCalculator\\Master Piece  new.xlsx";
        
        Connection connection = null;
        
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);

            // Load the Excel workbook
            FileInputStream fileInputStream = new FileInputStream(excelFilePath);
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            
            int startColumn = 0; // Start writing at column A
            
            // Execute each query and write results with column gaps
            for (String query : queries) {
                startColumn = writeQueryToSheet(connection, query, workbook, sheet, startColumn);
                startColumn += 2; // Leave one empty column as a gap
            }
            
            // Recalculate formulas
            workbook.setForceFormulaRecalculation(true);
            
            // Save the workbook
            try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
                workbook.write(outputStream);
            }

            fileInputStream.close();
            workbook.close();
            System.out.println("Data successfully written to Excel sheet with column gaps.");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close database connection
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

    private static int writeQueryToSheet(Connection connection, String query, Workbook workbook, Sheet sheet, int startColumn) {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            int rowCount = 0;
            
            // Write headers in the first row
            Row headerRow = sheet.getRow(rowCount);
            if (headerRow == null) {
                headerRow = sheet.createRow(rowCount);
            }
            for (int i = 0; i < columnCount; i++) {
                Cell cell = headerRow.createCell(startColumn + i);
                cell.setCellValue(metaData.getColumnName(i + 1));
            }
            rowCount++;
            
            // Write data
            while (resultSet.next()) {
                Row row = sheet.getRow(rowCount);
                if (row == null) {
                    row = sheet.createRow(rowCount);
                }
                for (int i = 0; i < columnCount; i++) {
                    Cell cell = row.createCell(startColumn + i);
                    Object value = resultSet.getObject(i + 1);
                    
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
                rowCount++;
            }
            
            System.out.println("Data from query written in columns " + (startColumn + 1) + " to " + (startColumn + columnCount));
            return startColumn + columnCount;
            
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
            return startColumn;
        }
    }

}
