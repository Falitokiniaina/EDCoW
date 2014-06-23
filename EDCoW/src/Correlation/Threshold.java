package Correlation;

import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 * This class if for computing the theta1, theta1 used for filtering the insignificant words, 
 * and theta2, used for filtering the insignificant correlation between keywords.
 * @author Yue He & Falitokiniaina RABEARISON
 *
 */

public class Threshold {

	/**
	 * Compute the median absolute deviation of a vector, seen in the equation 13 of the paper EDCoW.
	 * @param autoCorrelationValues vector of double
	 * @return the median absolute deviation of a vector (median of tempTable here), Equation 13 in the paper
	 */
	public double mad(double [] autoCorrelationValues){
		double [] tempTable = new double[autoCorrelationValues.length];
		Median m = new Median();
		double medianValue = m.evaluate(autoCorrelationValues);
		for(int i=0 ; i<autoCorrelationValues.length ;i++){
			tempTable[i] = Math.abs(autoCorrelationValues[i] - medianValue);
		}
		return m.evaluate(tempTable); //return the median of tempTable, the equation (13 in the paper)
	}
	
	/**
	 * equation 14 of the paper, compute the theta1.
	 * @param autoCorrelationValues Values of the autoCorrelation of a keyword in vector.
	 * @param gama parameter entered by the user, gamma1 in the 'param' file, it should be >10 
	 * @return theta1, the first value for filtering the insignificant keywords.
	 */
	public double theta1(double [] autoCorrelationValues, double gama){
		Median m = new Median();
		return  (m.evaluate(autoCorrelationValues) + (gama * mad(autoCorrelationValues)));				
	}
	
	/**
	 * Transform the matrix 2D into Vector of 1D (to compute its median) 
	 * @param matrix to be transformed
	 * @return the corresponding vector of a matrix
	 */
	public double[] transformMatrix(double [][] matrix){
		int a = matrix[0].length * matrix.length;
		double[] vector = new double[a];
		int v=0;
		for(int i=0; i<matrix.length; i++){
			for(int j=0; j<matrix[0].length; j++){				
				vector[v] =  matrix[i][j];
				v++;
			}
		}
		return vector;
	}
	
	/**
	 * Method to compute theta2
	 * @param crossCorrelationValues cross correlation matrix of the remaining keywords
	 * @param gama parameter, input from the user. In the paper, it is the same as the gamma1 but we make it different in the implementation, called gamma2 in the 'param' file 
	 * @return the theta2, the threshold for filtering the correlation matrix.
	 */
	public double theta2(double [][] crossCorrelationValues, double gama){
		double[] vecCrossCorrelation = transformMatrix(crossCorrelationValues);
		Median m = new Median();		
		return (m.evaluate(vecCrossCorrelation) + (gama * mad(vecCrossCorrelation)));		
	}	
}
