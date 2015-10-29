package processing;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import program.Settings;
import stockdata.Data;


public class CAPM{

	public static BigDecimal calculateCAR(Date d, BigDecimal BETA, BigDecimal ERM) throws IOException, ParseException {		
		// Get Risk-Free interest
		BigDecimal RF = new BigDecimal(0);
		List<String> lines = Files.readAllLines(Paths.get("data/RFIrate.csv"));
		String O10Y = "Over_10_Years";
		for(String line: lines){
			String[] cols = line.split(";");
			if(O10Y.equals(cols[1])){
				// RF over 10 years
				Date date = Settings.formatter.parse(cols[0]);
				if(d.compareTo(date) == 0){
					RF = new BigDecimal(cols[2].replace(',', '.'));
				}
			}
		}
		
		// Calculate CAR
		BigDecimal ER = RF.add(BETA.multiply(ERM.subtract(RF)));
		System.out.println(ER);
		return ER;
	}
	
	
	/**
	 * Calculates the beta value and the market return of the given company on the day given.
	 * @param company The handle of the company for which the values will be calculated
	 * @param market The handle of the market for which the values will be calculated
	 * @param date The date for which the values will be calculated
	 * @return A list containing:	[0] = beta value
	 * 								[1] = market return
	 */
	public static BigDecimal calculateBETA(Data compData, Data marketData, Date d){
		BigDecimal BETA = new BigDecimal(0);
		
		// Initialize the plotting loop
		Calendar curDate = Calendar.getInstance();
		curDate.setTime(d);
		curDate.add(Calendar.YEAR, -1);
		BigDecimal lastComp = new BigDecimal(0);
		BigDecimal curComp = compData.getAdjClose(curDate.getTime());
		BigDecimal lastMarket = new BigDecimal(0);
		BigDecimal curMarket = marketData.getAdjClose(curDate.getTime());
		HashMap<BigDecimal, BigDecimal> plot = new HashMap<BigDecimal, BigDecimal>();
		curDate.add(Calendar.DATE, 1);
		// Loop over all days and plot the increase.
		while(curDate.getTime().before(d)){
			// Ignore days without data
			if(marketData.getAdjClose(curDate.getTime()) == null){
				curDate.add(Calendar.DATE, 1);
				continue;
			}
			
			// Update values
			lastComp = curComp;
			lastMarket = curMarket;
			curComp = compData.getAdjClose(curDate.getTime());
			curMarket = marketData.getAdjClose(curDate.getTime());
			
			// Calculate increase since yesterday
			BigDecimal comp = curComp.subtract(lastComp).divide(lastComp, Settings.mc);
			BigDecimal market = curMarket.subtract(lastMarket).divide(lastMarket, Settings.mc);
			
			// Save values
			plot.put(comp, market);
			
			// Add 1 day;
			curDate.add(Calendar.DATE, 1);
		}
		
		
		// Plot in LinearRegression
		LinearRegression LR = new LinearRegression(plot.size());
		for(BigDecimal key: plot.keySet()){
			LR.addPoint(key, plot.get(key));
		}
		LR.calculate();
		BETA = LR.getY();
		
		// Adjust BETA
		BigDecimal oneThird = new BigDecimal(1).divide(new BigDecimal(3), Settings.mc);
		BigDecimal twoThird = new BigDecimal(2).divide(new BigDecimal(3), Settings.mc);
		BETA = twoThird.multiply(BETA).add(oneThird);

		// Return BETA
		return BETA;
	}
}
