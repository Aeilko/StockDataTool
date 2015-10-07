package program;

import processing.DataProcess;
import stockdata.Data;

/**
 * Main method for the Stock Data application
 * @author Aeilko Bos
 */
public class Program {
	public static void main(String[] args){
		Data d = new Data("YHOO", 'd', 1, 1, 2015, 1, 6, 2015);
		DataProcess p = new DataProcess();
		p.process(d);
	}
}