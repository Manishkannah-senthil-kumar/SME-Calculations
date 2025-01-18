package qicPages;

import java.io.IOException;
import java.sql.SQLException;

import org.testng.annotations.Test;

import qicUtilities.BaseClass;

public class QICPremiumCalcution extends BaseClass{
	
	
	
	@Test
	public void user_should_validate_the_total_premium_for_the_created_qoute() throws SQLException, IOException {
		

		// base premium
		FetchDataFromDB(calculatorData().getProperty("DBUrlUat"), calculatorData().getProperty("DBUserName"),
				calculatorData().getProperty("DBPassWord"), calculatorData().getProperty("QueryQICBasePremium"),
				calculatorData().getProperty("PremiumCalculator"), 0);
		
		// benefits
		FetchDataFromDB(calculatorData().getProperty("DBUrlUat"), calculatorData().getProperty("DBUserName"),
				calculatorData().getProperty("DBPassWord"), calculatorData().getProperty("QueryQICBenefits"),
				calculatorData().getProperty("PremiumCalculator"), 1);
		
		// Nationality loading
		
		FetchDataFromDB(calculatorData().getProperty("DBUrlUat"), calculatorData().getProperty("DBUserName"),
				calculatorData().getProperty("DBPassWord"), calculatorData().getProperty("QueryQICNationalityLoadings"),
				calculatorData().getProperty("PremiumCalculator"), 2);
		
		
		
		
		
		
		
		
		
	}
	
	

}
