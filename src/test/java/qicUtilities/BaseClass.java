package qicUtilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BaseClass {
	
    // Method to load properties from a file
    public static Properties calculatorData() throws IOException {
        Properties props = new Properties();
        FileReader reader = new FileReader("D:\\eclipse workspace\\SME-Calculations\\target\\DataForDistributor.properties\\Calculation.properties"); 
            props.load(reader);
        
        return props;
    }
    
   // Fetching data from data base
    
   public static void FetchDataFromDB (String DBUrlUat, String DBUserName, String DBPassWord,String Query,String PremiumCalculator, int sheetNum) throws SQLException, IOException {
	   
	   // Connect to DB
	   Connection DB = DriverManager.getConnection(DBUrlUat, DBUserName,DBPassWord);
	   ResultSet executeQuery = DB.createStatement().executeQuery(Query);
	   
	   // Open Excel File
	   FileInputStream excelInput = new FileInputStream(PremiumCalculator);
	   Workbook workbook = new XSSFWorkbook(excelInput);
	   Sheet sheet = workbook.getSheetAt(sheetNum);// Modify index if needed
	   
	   
	   // Clear existing rows except the header
	   
	   for (int i =1; i<=sheet.getLastRowNum();i++) {
		   
		   sheet.removeRow(sheet.getRow(i));
		   
	   }
	   
	   // Write database data to the Excel sheet
	   
	   int rowCount = 1;
       while (executeQuery.next()) {
           Row row = sheet.createRow(rowCount++);
           for (int i = 1; i <= executeQuery.getMetaData().getColumnCount(); i++) {
               Cell cell = row.createCell(i - 1);
               cell.setCellValue(executeQuery.getString(i));
           }
       }
       
    // Save changes to the Excel file
       FileOutputStream fileOutputStream = new FileOutputStream(PremiumCalculator); 
           workbook.write(fileOutputStream);
       

       System.out.println("Data written to Excel file successfully.");


}

	   
	   
   }
    


