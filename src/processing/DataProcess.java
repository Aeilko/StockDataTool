package processing;

import java.math.BigDecimal;
import java.util.Date;

import stockdata.Data;

public class DataProcess implements Processor {
	
	@Override
	/**
	 * Processes the given data.
	 */
	public BigDecimal process(Data data) {
		LinearRegression lr = new LinearRegression(data.getAdjClose().size());
		int x = 0;
		for(Date d: data.getAdjClose().keySet()){
			lr.addPoint(new BigDecimal(x), data.getAdjClose(d));
			x++;
		}
		lr.calculate();
		return lr.getY();
	}

}
