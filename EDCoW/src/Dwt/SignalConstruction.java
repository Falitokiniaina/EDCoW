package Dwt;

/**
 * This class is for transforming the signals, to get SW1 and SW2.
 * @author Yue HE & Falitokiniaina RABEARISON
 *
 */
public class SignalConstruction {
	
	double[] nwt; //number of tweets which contain word w as INPUT
	double[] nt; //number of all tweets in the same period of time as INPUT
	double[] sw; //signal in the first step
	double[] sw2; // signal in the second step
	int delta; // size of sliding window
	
	/**
	 * Construction of SW1. equation 8-9 in the paper.
	 * @param nwt number of tweets which contain word w as INPUT
	 * @param nt number of all tweets in the same period of time as INPUT
	 */
	public void firstSignalConstruction(double[] nwt, double [] nt){
		this.nwt = nwt;
		this.nt = nt;
		sw = new double[nwt.length];
		
		Vector ni = new Vector(nt);
		Vector nwi = new Vector(nwt);
		double tempRatio = ni.getSum()/nwi.getSum();

		for(int i=0; i<nwt.length ;i++){
			if(nt[i]==0)sw[i]=0;
			else sw[i] = (nwt[i] / nt[i]) * (Math.log(tempRatio)/Math.log(2)); 
		}		
	}
	
	/**
	 * Construction of the SW2, equation 10 of the paper
	 * @param sw SW1
	 * @param delta sliding window, input from the user, defined in the 'param' file
	 * @param level number of level
	 */
	public void secondSignalConstruction(double[] sw, int delta, int level){		
		this.sw = sw;
		this.delta = delta;
		int sizeSw2 = (sw.length/delta) - 1;		
		sw2 = new double[sizeSw2];
		
		for (int i=0, k=0; i < sw2.length*delta; i=i+delta, k++){
			double[] signDtPrime = new double[delta];
			double[] signDtStar = new double[delta*2];
			
			for (int j=0; j <delta; j++){
				signDtPrime[j] = sw[i+j];				
			}	
			
			for (int j=0; j <delta*2; j++){
				signDtStar[j] = sw[i+j];				
			}
			
			Dwt dwtDtPrime = new Dwt(signDtPrime,level);
			Dwt dwtDtStar = new Dwt(signDtStar,level);
			dwtDtPrime.hMeasure();
			dwtDtStar.hMeasure();
			
			double htPrime  = dwtDtPrime.gethMeasure();
			double htStar = dwtDtStar.gethMeasure();
			
			if (htStar > htPrime)
				sw2[k] = (htStar - htPrime)/htPrime;
			else
				sw2[k] = 0;
		}
	}

	public double[] getSw() {
		return sw;
	}
	public double[] getSw2() {
		return sw2;
	}	
	
}
