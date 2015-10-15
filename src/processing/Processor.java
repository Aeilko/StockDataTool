package processing;

import java.math.BigDecimal;

import stockdata.Data;

public interface Processor {
	public BigDecimal process(Data data);
}