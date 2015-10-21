package program;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import processing.DataProcess;
import stockdata.Data;

/**
 * Main method for the Stock Data application
 * @author Aeilko Bos
 */
public class StockData {
	// Afronding van BigDecimals
	public static final MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
	
	public static void run(String comp, String date) throws ParseException{
		
		// Create start and end days
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		Date datum = formatter.parse(date);
		Calendar attack = Calendar.getInstance();
		attack.setTime(datum);
		Calendar start = (Calendar) attack.clone();
		Calendar end = (Calendar) attack.clone();
		start.add(Calendar.DATE, Settings.daysBefore*(-1));
		end.add(Calendar.DATE, Settings.daysAfter);
		
		// Save the starting and ending dates
		int startDay = start.get(Calendar.DATE);
		int startMonth = start.get(Calendar.MONTH);
		int startYear = start.get(Calendar.YEAR);
		int attackDay = attack.get(Calendar.DATE);
		int attackMonth = attack.get(Calendar.MONTH);
		int attackYear = attack.get(Calendar.YEAR);
		int endDay = end.get(Calendar.DATE);
		int endMonth = end.get(Calendar.MONTH);
		int endYear = end.get(Calendar.YEAR);
		
		// Get stock data before the attack and save it
		Data allData = new Data(comp, 'd', startDay, startMonth, startYear, attackDay, attackMonth, attackYear);
		formatter = new SimpleDateFormat("yyyMMdd");
		allData.save("data/" + comp + "_" + formatter.format(start.getTime()) + "-" + formatter.format(attack.getTime()) + ".csv");
		
		// Process the stock data before
		DataProcess p = new DataProcess();
		BigDecimal mean = p.process(allData);
		
		// Get stock data after the attack and save it
		Data actualData = new Data(comp, 'd', attackDay, attackMonth, attackYear, endDay, endMonth, endYear);
		actualData.save("data/" + comp + "_" + formatter.format(attack.getTime()) + "-" + formatter.format(end.getTime()) + ".csv");
		
		// Process the stock data after the attack
		BigDecimal actual = p.process(actualData);
		
		// Calculate the difference
		BigDecimal difference = actual.subtract(mean);
		
		// Calculate the percent difference based on the opening price on the day of the attack.
		Calendar firstDay = attack;
		BigDecimal attackOpen = null;
		while(attackOpen == null){
			attackOpen = actualData.getOpen(firstDay.getTime());
			firstDay.add(Calendar.DATE, 1);
		}
		BigDecimal percentDifference = difference.divide(attackOpen, mc).multiply(new BigDecimal(100));
		
		// Show results
		System.out.println("Company:\t\t\t\t" + comp);
		System.out.println("Attack Date:\t\t\t\t" + date);
		System.out.println("Mean over " + Settings.daysBefore + " days\t\t\t" + mean.toPlainString().replace('.', ','));
		System.out.println("Mean during attack over " + Settings.daysAfter + " days:\t\t" + actual.toPlainString().replace('.', ','));
		System.out.println("Difference:\t\t\t\t" + difference.toPlainString().replace('.', ','));
		System.out.println("Percentual Difference:\t\t\t" + percentDifference + "%");
	}
	
	
	public static void main(String[] args){
		try {
			if(args.length != 2){
				System.err.println("Use: StockData [companyHandle] [attackDate(Format: dd-mm-yyyy)]");
			}
			else{
				String comp = args[0];
				String date = args[1];
				StockData.run(comp, date);
			}
		}
		catch (ParseException e) { System.err.println("Datum niet in juiste format"); }
	}
}