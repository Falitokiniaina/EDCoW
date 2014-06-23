package Dwt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;

import Correlation.CrossCorrelationZeroTime;

/**
 * This class is used for treating the signals of the keywords stored in a folder. Each file of keyword (nwt) will correspond to an instance of this class.
 * @author Yue HE & Falitokiniaina RABEARISON
 *
 */
public class KeyWord {	
	static int delta;
	static double[] longNt;
	double[] partNt;
	double[] nwt;
	double[] SW1;
	double[] SW2;
	String keyWord;
	double autoCorrelation;
	double[] crossCorrelation;	
	String filePath;	
	
	/**
	 * This is used during reading the whole files of a folder (just putting in the memory without anycomputations).
	 * @param filePath_ path to the file
	 * @param keyword_ name of the file (conventionally is the keyword)
	 * @param nwt_ Signal of the keyword, read from the file 
	 * @param delta_ input from the user, read from the file 'param' used for the computation of SW1 and SW2
	 */
	public KeyWord(String filePath_, String keyword_, double[] nwt_, int delta_){     //For the global keywords   
		nwt = nwt_;
        delta = delta_;        
        this.filePath = filePath_;
        keyWord = keyword_;                
	}	
		
	/**
	 * After selecting the window, subKeywords of the above Keyword are created, so this method used to instantiate that subSignals of Keywords. and run the computations 
	 * (Computation of SW1, SW2 and autocorrelation). 
	 * @param filePath_ path to the nwt file
	 * @param keyword_ name of the file, the keyword
	 * @param nwt_ part of the keyword signal, corresponding to the window
	 * @param delta_  input from the user, read from the file 'param' used for the computation of SW1 and SW2
	 * @param partNt_ part of the nt signals corresponding to the window 
	 */
	public KeyWord(String filePath_, String keyword_, double[] nwt_, int delta_, double[] partNt_){    //for each window    
		nwt = nwt_;
        delta = delta_;        
        this.filePath = filePath_;
        keyWord = keyword_;
        partNt = partNt_;
        computations();
	}
	
	
	/**
	 * Read the signals from nt file and store them in 'longNt', a vector of double.
	 * @param filePath Path to the nt file.
	 */
	public static void setLongNt(String filePath){
		try{
			FileReader fileReadear = new FileReader(filePath);
			BufferedReader buff = new BufferedReader(fileReadear);

			String firstLine = buff.readLine();
			/** separate the data with delimiter ; */			
			StringTokenizer st = new StringTokenizer(firstLine, ";");		
			int nb = 0;
			longNt = new double[st.countTokens()];
			while(st.hasMoreTokens()){				
				longNt[nb] = Double.parseDouble(st.nextToken());
				nb++;
			}
			buff.close();
			fileReadear.close();						
		}//Fin try		 
		catch (IOException e){
			System.out.println("Error : "+e);
		}			
	}

	/**
	 * Compute SW1, SW2 and autocorelation of each part of Keyword (according to the window).
	 */
	public void computations(){
		int lev = (int) (Math.log(delta)/Math.log(2));
		SignalConstruction signWavelet = new SignalConstruction();
		signWavelet.firstSignalConstruction(nwt, longNt);
		signWavelet.secondSignalConstruction(signWavelet.getSw(), delta, lev);		

		//System.out.println("\n\n signal construction (for world w) in the first step:");		
		SW1 = signWavelet.getSw();
				
		//System.out.println("\n signal construction (for world w) in the second step:");				
		SW2 = signWavelet.getSw2();
		
		CrossCorrelationZeroTime cc = new CrossCorrelationZeroTime();
		autoCorrelation = cc.autoCorrelationZeroTime(SW2);						
	}
		
	/**
	 * Compute cross correlation of the keyword with the whole list of significan keywords.
	 * @param keyWordsList1
	 */
	public void computeCrossCorrelation(LinkedList<KeyWord> keyWordsList1) {
		// TODO Auto-generated method stub
		crossCorrelation = new double[keyWordsList1.size()];
		CrossCorrelationZeroTime cc = new CrossCorrelationZeroTime();
		for(int i=0; i<keyWordsList1.size(); i++){
			crossCorrelation[i] = cc.correlationZeroTime(SW2, keyWordsList1.get(i).getSW2());
		}
	}		

	public static double[] getLongNt() {
		return longNt;
	}

	public static void setLongNt(double[] nt) {
		KeyWord.longNt = nt;
	}

	public static int getDelta() {
		return delta;
	}

	public static void setDelta(int delta) {
		KeyWord.delta = delta;
	}

	public double[] getNwt() {
		return nwt;
	}

	public void setNwt(double[] nwt) {
		this.nwt = nwt;
	}

	public double[] getSW1() {
		return SW1;
	}

	public void setSW1(double[] sW1) {
		SW1 = sW1;
	}

	public double[] getSW2() {
		return SW2;
	}

	public void setSW2(double[] sW2) {
		SW2 = sW2;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public double getAutoCorrelation() {
		return autoCorrelation;
	}

	public void setAutoCorrelation(double autoCorrelation) {
		this.autoCorrelation = autoCorrelation;
	}

	public double[] getCrossCorrelation() {
		return crossCorrelation;
	}

	public void setCrossCorrelation(double[] crossCorrelation) {
		this.crossCorrelation = crossCorrelation;
	}


	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}	
	
	/**
	 * Format the display of SW1
	 * @return Formated string for SW1
	 */
	public String getSW1ToString() {
		String sw1 = "";
		for(double sg : SW1){
			if(sw1.equalsIgnoreCase("")) sw1 = String.valueOf(sg);
			else
				sw1 = sw1 + ";"+ sg;
		}
		return sw1;
	}	
	
	/**
	 * Format the display of SW2
	 * @return Formated string for SW2
	 */
	public String getSW2ToString() {
		String sw2 = "";
		for(double sg : SW2){
			if(sw2.equalsIgnoreCase("")) sw2 = String.valueOf(sg);
			else
			sw2 = sw2 + ";" +sg;
		}
		return sw2;
	}	
}
