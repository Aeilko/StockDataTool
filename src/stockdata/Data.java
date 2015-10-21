package stockdata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * The stock data of a company over a given time period
 * @author Aeilko Bos
 */
public class Data {
	// Opening price on a given day
	private TreeMap<Date, BigDecimal> open;
	// The highest price on a given day
	private TreeMap<Date, BigDecimal> high;
	// The lowwest price on a given day
	private TreeMap<Date, BigDecimal> low;
	// The closing price on a given day
	private TreeMap<Date, BigDecimal> close;
	// The total volume of stocks traded
	private TreeMap<Date, Integer> volume;
	// The adjusted closing price
	private TreeMap<Date, BigDecimal> adjClose;
	
	private List<String> rawData;
	
	
	// Constructor
	/**
	 * Creates an empty data instance
	 */
	public Data(){
		this.reset();
	}
	
	/**
	 * Creates the data model based on the given CSV file
	 * @param csvFile The csv file the data is based on.
	 */
	public Data(String csvFile){
		this.reset();
		try {
			List<String> data = Files.readAllLines(Paths.get(csvFile));
			processDataCSV(data);
		}
		catch (IOException e) { System.err.println("Het bestand '" + csvFile + "' kon niet gelezen worden");e.printStackTrace(); }
	}
	
	/**
	 * Creates the data model based on Yahoo Finance data with the given parameters
	 * @param id The ID of the company.
	 * @param mode The interval of the data (d=Day, w=Week, m=Month, y=Year)
	 * @param startDay The starting day of the month.
	 * @param startMonth The start month (1-12).
	 * @param startYear The start year.
	 * @param endDay The end day of the month.
	 * @param endMonth The end month (1-12)
	 * @param endYear The end year
	 */
	public Data(String id, char mode, int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear){
		this.reset();
		RequestData src = new RequestData();
		String data = src.getData(id, mode, startDay, startMonth, startYear, endDay, endMonth, endYear);
		String[] lines = data.split("\n");
		List<String> d = new ArrayList<String>();
		for(String s: lines){
			d.add(s);
		}
		this.processData(d);
	}
	
	
	
	// Commands
	private void reset(){
		this.open = new TreeMap<Date, BigDecimal>();
		this.high = new TreeMap<Date, BigDecimal>();
		this.low = new TreeMap<Date, BigDecimal>();
		this.close = new TreeMap<Date, BigDecimal>();
		this.volume = new TreeMap<Date, Integer>();
		this.adjClose = new TreeMap<Date, BigDecimal>();
	}
	
	/**
	 * Processes the given data into the data model
	 * @param data
	 */
	private void processData(List<String> data){
		this.rawData = data;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		for(String line: data){
			String[] col = line.split(",");
			// Filter the name row
			if(!col[0].equals("") && !col[0].equals("Date")){
				try {
					Date datum = df.parse(col[0]);
					this.open.put(datum, new BigDecimal(col[1]));
					this.high.put(datum, new BigDecimal(col[2]));
					this.low.put(datum, new BigDecimal(col[3]));
					this.close.put(datum, new BigDecimal(col[4]));
					this.volume.put(datum, Integer.parseInt(col[5]));
					this.adjClose.put(datum, new BigDecimal(col[6]));
				}
				catch (ParseException e) { System.err.println("Kon de datum '" + col[0] + "' niet parsen"); }
			}
		}
	}
	
	/**
	 * Nederlandse CSV's hebben een andere structuur dan de standaard CSV, deze methode past dit aan voordat de data
	 * naar processData wordt gestuurd.
	 * @param data De CSV data.
	 */
	private void processDataCSV(List<String> data){
		ArrayList<String> newFormat = new ArrayList<String>();
		for(String s: data){
			s = s.replace(",", ".");
			s = s.replace(";", ",");
			newFormat.add(s);
		}
		processData(newFormat);
	}
	
	/**
	 * Saves the current data in csv format to the given file
	 * @param file The filepath and name to which the data should be saved
	 */
	public void save(String file){
		// Convert List<String> to a single string
		String d = "";
		for(String s: this.rawData){
			d += s + "\n";
		}
		
		PrintWriter p;
		try {
			// Transform string to Dutch CSV format
			d = d.replace(',', ';');
			d = d.replace('.', ',');
			
			// Save string to CSV file
			p = new PrintWriter(new File(file));
			p.println(d);
			p.close();
		}
		catch (FileNotFoundException e) { e.printStackTrace(); }
	}
	
	
	// Queries
	/**
	 * @return Map with the opening prices
	 */
	public TreeMap<Date, BigDecimal> getOpen(){
		return this.open;
	}
	
	/**
	 * @param d The day of which the opening price is requested
	 * @return The opening price on the given day
	 */
	public BigDecimal getOpen(Date d){
		return this.open.get(d);
	}
	
	/**
	 * @return Map with the highest prices
	 */
	public TreeMap<Date, BigDecimal> getHigh(){
		return this.high;
	}
	
	/**
	 * @param d The day of which the highest price is requested
	 * @return The opening price on the given day
	 */
	public BigDecimal getHigh(Date d){
		return this.high.get(d);
	}
	
	/**
	 * @return Map with the lowest prices
	 */
	public TreeMap<Date, BigDecimal> getLow(){
		return this.low;
	}
	
	/**
	 * @param d The day of which the lowest price is requested
	 * @return The opening price on the given day
	 */
	public BigDecimal getLow(Date d){
		return this.low.get(d);
	}
	
	/**
	 * @return Map with the closing prices
	 */
	public TreeMap<Date, BigDecimal> getClose(){
		return this.close;
	}
	
	/**
	 * @param d The day of which the closing price is requested
	 * @return The opening price on the given day
	 */
	public BigDecimal getClose(Date d){
		return this.close.get(d);
	}
	
	/**
	 * @return Map with the traded volume
	 */
	public TreeMap<Date, Integer> getVolume(){
		return this.volume;
	}
	
	/**
	 * @param d The day of which the traded volume is requested
	 * @return The opening price on the given day
	 */
	public Integer getVolume(Date d){
		return this.volume.get(d);
	}
	
	/**
	 * @return Map with the adjusted closing prices
	 */
	public TreeMap<Date, BigDecimal> getAdjClose(){
		return this.adjClose;
	}
	
	/**
	 * @param d The day of which the adjusted closing price is requested
	 * @return The opening price on the given day
	 */
	public BigDecimal getAdjClose(Date d){
		return this.adjClose.get(d);
	}
}