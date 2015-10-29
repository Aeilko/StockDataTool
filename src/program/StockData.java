package program;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import processing.CAPM;
import processing.DataProcess;
import stockdata.Data;

/**
 * Main method for the Stock Data application
 * @author Aeilko Bos
 */
public class StockData {
	// Afronding van BigDecimals
	public static final MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
	
	/**
	 * Calculates the difference using online Linear Regression
	 * @param comp The handle of the company which was attacked
	 * @param date The dat of the attack
	 * @throws ParseException If the given date isn't in the right format (dd-MM-yyyy)
	 */
	public static void runLinear(String comp, String date) throws ParseException{
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
		
		try {
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
			System.out.println("Percentual Difference:\t\t\t" + percentDifference.toPlainString().replace('.', ',') + "%");
			//System.out.println(mean.toPlainString().replace('.', ',') + "\t" + actual.toPlainString().replace('.', ',') + "\t" + difference.toPlainString().replace('.', ',') + "\t" + percentDifference.toPlainString().replace('.', ',') + "%");
			
			// Log the results
			Log l = new Log("log.txt");
			l.write(comp + "\t" + mean.toPlainString().replace('.', ',') + "\t" + actual.toPlainString().replace('.', ',') + "\t" + difference.toPlainString().replace('.', ',') + "\t" + percentDifference.toPlainString().replace('.', ',') + "%");
		}
		catch(IOException e){ System.err.println("De stockdata kan niet worden opgehaald."); }
	}
	
	
	/**
	 * Calculates the difference using CAPM
	 * @param comp The handle of the company which has been attacked
	 * @param market The market on which the company is traded
	 * @param date The date of the attack.
	 * @throws ParseException If the given date isn't in the right format (dd-MM-yyyy)
	 */
	public static void runCAPM(String comp, String market, String date) throws ParseException{
		// Read date
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		Date attackDate = formatter.parse(date);
		
		try {
			System.out.println("\nHandle:\t\t" + comp);
			
			// Calculate all dates
			Calendar attack = Calendar.getInstance();
			attack.setTime(attackDate);
			int attackDay = attack.get(Calendar.DATE);
			int attackMonth = attack.get(Calendar.MONTH);
			int attackYear = attack.get(Calendar.YEAR);
			Calendar start = (Calendar) attack.clone();
			start.add(Calendar.YEAR, -1);
			int startDay = start.get(Calendar.DATE);
			int startMonth = start.get(Calendar.MONTH);
			int startYear = start.get(Calendar.YEAR);
			Calendar end = (Calendar) attack.clone();
			end.add(Calendar.DATE, 5);
			int endDay = end.get(Calendar.DATE);
			int endMonth = end.get(Calendar.MONTH);
			int endYear = end.get(Calendar.YEAR);
			
			// Calculate BETA
			Data compData = new Data(comp, 'd', startDay, startMonth, startYear, attackDay, attackMonth, attackYear);
			Data marketData = new Data(market, 'd', startDay, startMonth, startYear, attackDay, attackMonth, attackYear);
			compData.save("data/" + comp + "_" + formatter.format(start.getTime()) + "-" + formatter.format(attack.getTime()) + ".csv");
			marketData.save("data/" + market + "_" + formatter.format(start.getTime()) + "-" + formatter.format(attack.getTime()) + ".csv");
			BigDecimal BETA = CAPM.calculateBETA(compData, marketData, attackDate);
			System.out.println("BETA:\t\t" + BETA);
			
			// Calculate ERM
			BigDecimal startOpen = marketData.getOpen(start.getTime());
			while(startOpen == null){
				start.add(Calendar.DATE, 1);
				startOpen = marketData.getOpen(start.getTime());
			}
			
			Calendar attackWeekDay = (Calendar) attack.clone();
			BigDecimal attackOpen = marketData.getOpen(attackWeekDay.getTime());
			while(attackOpen == null){
				attackWeekDay.add(Calendar.DATE, -1);
				attackOpen = marketData.getOpen(attackWeekDay.getTime());
			}
			BigDecimal ERM = attackOpen.subtract(startOpen).divide(startOpen, mc);
			System.out.println("ERM:\t\t" + ERM);
			
			
			// Get Stock data over 5 days
			Data attackData = new Data(comp, 'd', attackDay, attackMonth, attackYear, endDay, endMonth, endYear);
			attackData.save("data/" + comp + "_" + formatter.format(attack.getTime()) + "-" + formatter.format(end.getTime()) + ".csv");
			
			
			Calendar cur = Calendar.getInstance();
			cur.setTime(attackDate);
			// Loop for 5 days
			while(cur.getTime().before(end.getTime())){
				// Read actual data
				BigDecimal open = attackData.getOpen(cur.getTime());
				BigDecimal adjClose = attackData.getAdjClose(cur.getTime());
				
				// Skip days when the exchange is closed
				if(open == null){
					cur.add(Calendar.DATE, 1);
					continue;
				}
				
				// Calculate CAR data
				BigDecimal CAR = CAPM.calculateCAR(cur.getTime(), BETA, ERM);
				
				// Calculate difference
				BigDecimal meanDay = adjClose.subtract(open).divide(open, mc);
				
				// Show results
				System.out.println("\n" + cur.getTime());
				System.out.println("CAR:\t\t" + CAR);
				System.out.println("Increase:\t" + meanDay);
				System.out.println("Difference:\t" + meanDay.subtract(CAR));
				
				// Increase current date with one day.
				cur.add(Calendar.DATE, 1);
			}
		}
		catch (ParseException e){ System.err.println("Kan één of meerdere datums niet lezen."); }
		catch (IOException e) { System.err.println("Kan één of meerdere bestanden niet lezen."); e.printStackTrace(); };
	}
	
	
	// Main method
	public static void main(String[] args){
		try {
			if(args.length < 1 || (!"auto".equals(args[0]) && args.length != 3)){
				System.err.println("Use: StockData [auto] || ([companyHandle] [marketHandle] [attackDate(Format: dd-mm-yyyy)])");
			}
			else{
				if(args[0].equals("auto")){
					System.out.println("Automatic, loading attacks from /data/attacks.csv");
					List<String> lines = Files.readAllLines(Paths.get("data/attacks.csv"));
					for(String line: lines){
						String[] cols = line.split(";");
						StockData.runCAPM(cols[0], cols[1], cols[2]);
					}
				}
				else{
					String comp = args[0];
					String market = args[1];
					String date = args[2];
					//StockData.runLinear(comp, date);
					StockData.runCAPM(comp, market, date);
				}
			}
		}
		catch (ParseException e) { System.err.println("Datum niet in juiste format"); }
		catch (IOException e) { System.err.println("Kan attacks.csv niet openen"); }
	}
}