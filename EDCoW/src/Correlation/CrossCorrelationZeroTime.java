package Correlation;

/**
 *      This class serves for computing the cross correlation function (CCF).
 *      The cross correlation between a pair of signals is calculated without applying time lag.
 *      The computing is done using the FFT.
 *      @author Yue He & Falitokiniaina RABEARISON
 */


public class CrossCorrelationZeroTime {       		
	
	/**
	 * Method for computing the autocorrelation at zero time of the vector double[] sign
	 * If the result is so insignificant (<0.00001) we return 0 (to gain more memory).
	 * @param sign Vector with which we are going to compute its autocorrelation.
	 * @return Value corresponding to the auto-correlation of the vector sign
	 */
	public double autoCorrelationZeroTime(double[] sign){
		double sum = 0.0;
		for(double x:sign)sum += Math.pow(x, 2); 
		return (sum<0.00001)?0:sum;
	}
	
	/**
	 * Compute the correlation of two different vectors.
	 * If the return is <0.00001, the return value will be 0.
	 * @param sign1 First vector as input
	 * @param sign2	Second vector as input
	 * @return Value corresponding to the correlation of the two vectors
	 */
	public double correlationZeroTime(double[] sign1, double[] sign2){
		double sum = 0.0;
		if(sign1.length == sign2.length)
			for(int i=0; i < sign1.length; i++)
				sum += (sign1[i] * sign2[i]);
		else System.out.println("The length of sign1 and sign2 is not the same.");
		return (sum<0.00001)?0:sum;
	}

}       
