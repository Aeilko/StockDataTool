package processing;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Linear Regression calculator.
 * Based on the code from: http://introcs.cs.princeton.edu/java/97data/LinearRegression.java.html
 * @author Aeilko Bos
 */
@SuppressWarnings("unused")
public class LinearRegression {
	
	// Maximum number of days
	int maxDays;
	// Current day
	int curDay;
	// Array containing the x values
	int[] x;
	// Array containing the y values
	BigDecimal[] y;
	
	// Math context for infinite numbers;
	MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
	
	// Calculation variables
	private double sumX;
	private double sumXSQ;
	private BigDecimal sumY;
	private BigDecimal barX;
	private BigDecimal barY;
	private BigDecimal barXX;
	private BigDecimal barYY;
	private BigDecimal barXY;
	private BigDecimal beta0;
	private BigDecimal beta1;
	private int df;
	private BigDecimal rss;
	private BigDecimal ssr;
	private BigDecimal fit;
	
	
	// Constructor
	/**
	 * Initializes a new Linear Regression graph
	 * @param dagen Number of days
	 * @require dagen > 0
	 */
	public LinearRegression(int dagen){
		this.maxDays = dagen;
		this.reset();
	}
	
	
	// Commands
	/**
	 * Resets the data
	 */
	private void reset(){
		this.x = new int[this.maxDays];
		this.y = new BigDecimal[this.maxDays];
		this.curDay = 0;
		this.sumX = 0;
		this.sumXSQ = 0;
		this.sumY = new BigDecimal(0);
		this.barX = new BigDecimal(0);
		this.barY = new BigDecimal(0);
		this.barXX = new BigDecimal(0);
		this.barYY = new BigDecimal(0);
		this.barXY = new BigDecimal(0);
		this.beta0 = new BigDecimal(0);
		this.beta1 = new BigDecimal(0);
		this.df = 0;
		this.rss = new BigDecimal(0);
		this.ssr = new BigDecimal(0);
		this.fit = new BigDecimal(0);
	}
	
	/**
	 * Adds a day to the data
	 * @param x The day of the y value
	 * @param y The value
	 */
	public void addDay(int x, BigDecimal y){
		this.x[curDay] = x;
		this.y[curDay] = y;
		sumX += x;
        sumXSQ += x*x;
        sumY = sumY.add(y);
        curDay++;
	}
	
	/**
	 * Performs calculations
	 */
	public void calculate(){
		this.barX = new BigDecimal(this.sumX).divide(new BigDecimal(this.maxDays));
		this.barY = sumY.divide(new BigDecimal(this.maxDays), mc);
		
        for (int i = 0; i < this.maxDays; i++) {
        	BigDecimal tmp1 = new BigDecimal(x[i]).subtract(barX);
        	BigDecimal tmp2 = this.y[i].subtract(barY);
        	this.barXX = barXX.add(tmp1.multiply(tmp1));
        	this.barYY = barYY.add(tmp2.multiply(tmp2));
        	this.barXY = barXY.add(tmp1.multiply(tmp2));
        }
        
        this.beta1 = barXY.divide(barXX, mc);
        this.beta0 = barY.subtract(beta1.multiply(barX));
        
        //System.out.println("Y\t\t\t= " + beta1);
        //System.out.println("X\t\t\t= " + beta0);
        //System.out.println("f(x)\t\t\t= " + beta1 + "x + " + beta0);
        
        /*this.df = this.maxDays-2;
        for(int i = 0; i < this.maxDays; i++){
        	this.fit = beta1.multiply(new BigDecimal(x[i])).add(beta0);
        	this.rss = rss.add(fit.subtract(y[i]).multiply(fit.subtract(y[i])));
        	this.ssr = ssr.add(fit.subtract(barY).multiply(fit.subtract(barY)));
        }
        
        BigDecimal R2 = ssr.divide(barYY, mc);
        BigDecimal svar = rss.divide(new BigDecimal(df), mc);
        BigDecimal svar1 = svar.divide(barXX, mc);
        BigDecimal svar0 = svar.divide(new BigDecimal(maxDays), mc).add(barX.multiply(barX).multiply(svar1));*/
	}
	
	
	// Queries
	/**
	 * @return The Y value of the Linear Regression
	 */
	public BigDecimal getY(){
		return this.beta1;
	}
	
	/**
	 * @return The X value of the Linear Regression
	 */
	public BigDecimal getX(){
		return this.beta0;
	}
}