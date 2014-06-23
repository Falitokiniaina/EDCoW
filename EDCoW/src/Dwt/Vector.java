package Dwt;

/**
 * 
 * This class is used to do some computation for vectors
 * @author Yue HE & Falitokiniaina RABEARISON
 *
 */
public class Vector {
	
	double[] vec;
	double norm;
	double sum;

	
	/**
	 * Constructor (compute at the same time its norm and the sum of the values in the vector)
	 * @param vec1 vector of values for the vector
	 */
	Vector(double[] vec1){
		vec = vec1;
		norm();
		sumVector();
	}

	/**
	 * Compute the sum of the element of the vector.
	 */
	public void sumVector(){
		sum = 0;
		for(double x: vec){
			sum = sum + x;
		}
	}
	
	
	/**
	 * Compute the norm of the vector.
	 */
	public void norm() {
		// TODO Auto-generated method stub
		norm = 0;
		for(double x : vec){
			norm = norm + Math.pow(x, 2);
		}
		norm = Math.sqrt(norm);
	}


	public double getNorm() {
		return norm;
	}
	public double getSum() {		
		return sum;
	}


}
