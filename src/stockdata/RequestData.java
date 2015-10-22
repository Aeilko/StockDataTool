package stockdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class RequestData {
	
	// The data of this stock
	private String data = "";
	
	// Constructor
	public RequestData(){	}
	public RequestData(String url) throws IOException{
		getDataFromURL(url);
	}
	
	
	// Commands
	/**
	 * Requests and returns this stock data of the given URL
	 * @param url The URL from which the data is loaded.
	 * @return The data in string format or an empty string if an error occured
	 */
	public String getDataFromURL(String urlString) throws IOException{
		StringBuilder result = new StringBuilder();
		try {
		    URL url = new URL(urlString);
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    while ((line = rd.readLine()) != null) {
		       result.append(line + "\n");
		    }
		    rd.close();
		    this.data = result.toString();
		}
		catch(ProtocolException e){ e.printStackTrace(); }
		catch(MalformedURLException e) { e.printStackTrace(); }
		//catch(IOException e) { System.err.println("Hetvolgende bestand kon niet worden gevonden: '" + urlString + "'"); }
	    return result.toString();
	}
	
	/**
	 * Gets the stock data of the given company in the given interval from Yahoo Finance.
	 * @param id The ID of the company.
	 * @param mode The interval of the data (d=Day, w=Week, m=Month, y=Year)
	 * @param startDay The starting day of the month.
	 * @param startMonth The start month (1-12).
	 * @param startYear The start year.
	 * @param endDay The end day of the month.
	 * @param endMonth The end month (1-12)
	 * @param endYear The end year
	 * @return String representation of the data
	 * @throws IOException If the stock data is not available.
	 */
	public String getData(String id, char mode, int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear) throws IOException{
		// startMonth and endMonth both start at 0 for some reason, this fixes it.
		//startMonth--;
		//endMonth--;
		return getDataFromURL("http://ichart.yahoo.com/table.csv?s=" + id + "&a=" + startMonth + "&b=" + startDay + "&c=" + startYear + "&d=" + endMonth + "&e=" + endDay + "&f=" + endYear + "&g=" + mode + "&ignore=.csv");
	}
	
	
	// Queries
	/**
	 * Returns the last requested data.
	 * @return The last requested data or an empty string if there is none.
	 */
	public String getData(){
		return this.data;
	}
}