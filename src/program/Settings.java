package program;

import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Settings {
	
	// The number of days before the event from which the data should be collected
	public static final int daysBefore = 120;
	
	// The number of days after the event from which the data should be collected
	public static final int daysAfter = 5;
	
	// Date format
	public static final DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
	
	// Math context for dividing BigDecimals
	public static final MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
}