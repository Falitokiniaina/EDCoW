package Dwt;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import ch.epfl.lis.jmod.modularity.community.Community;
import Correlation.Threshold;
import JmodPackage.ModularityDetection;

/**
 * Implements runnable, run each thread assigned to a window. 
 * @author Yue HE & Falitokiniaina RABEARISON
 *
 */

public class ProcessWindow implements Runnable{	
	int window;
	int delta;
	int gamma1;
	int gamma2;
	double[] partNt;
	double[] partNwt;
	LinkedList<KeyWord> longKeyWords;
	LinkedList<KeyWord> partKeyWords;	
	int sizeWindow;
    float startDay;
    float endDay;
    LinkedList<Event> events; 
    
    double theta1;
    double theta2;
    double thresholdE;
    boolean debugMode;
    String nwtFolder;
    String FolderName;
    
    /**
     * Constructor
     * @param window number of the window
     * @param sizeWindow_ size of the window
     * @param longKeyWords_ the whole KeyWord of the specified folder
     * @param delta_ parameter, input form the user, defined in the 'param' file
     * @param gamma1_ parameter, input form the user, defined in the 'param' file (should be >10)
     * @param gamma2_ parameter, input form the user, defined in the 'param' file (should be >10)
     * @param thresholdE_ parameter, input form the user, defined in the 'param' file
     * @param debugMode_ if debug mode enabled, it will store some files in the hard drive.
     * @param nwtFolder_ path to folder containing the nwt files.
     */
	ProcessWindow(int window, int sizeWindow_, LinkedList<KeyWord> longKeyWords_, int delta_, int gamma1_, int gamma2_, double thresholdE_, boolean debugMode_, String nwtFolder_){
		this.window = window;
		sizeWindow = sizeWindow_;
        this.startDay = window*sizeWindow;
        this.endDay = window*sizeWindow+sizeWindow;
        delta = delta_;
        gamma1 = gamma1_;
        gamma2 = gamma2_;
        longKeyWords = new LinkedList<>();
        longKeyWords.addAll(longKeyWords_);
        partKeyWords = new LinkedList<>();
        events = new LinkedList<>();
        thresholdE = thresholdE_;
        debugMode = debugMode_;
        nwtFolder = nwtFolder_;
		String[] nwtFolderSplit = nwtFolder.split("/");
		FolderName = nwtFolderSplit[nwtFolderSplit.length-1];        
	}

//computeCrossCorrelation
	/**
	 * for each selected window to be run,
	 * - assign the corresponding signals to the partNt and partNwt
	 * - with the construction of the KeyWord, all the needed computations are done
	 * - theta1 will be used to filter the keywords
	 * - For each window, it has big correlation matrix of the remaining keywords
	 * - The matrix will be filtered with the threshold theta2
	 * - for each window, some events will be detected using the modularity detection
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		PrintWriter writer1;

        for(KeyWord kw : longKeyWords){        	
        	partNt = new double[sizeWindow];        	
        	partNwt = new double[sizeWindow];
        	
	        for(int i = window*sizeWindow; i < window*sizeWindow+sizeWindow;  i++){
	            partNt[i-window*sizeWindow] = KeyWord.getLongNt()[i]; 
	        }		
	        	        
	        for(int i=window*sizeWindow; i<window*sizeWindow+sizeWindow; i++){
	            partNwt[i-window*sizeWindow] = (double) kw.getNwt()[i];
	        }
	        partKeyWords.add(new KeyWord(kw.getFilePath(), kw.getKeyWord(), partNwt, delta,partNt));      	        	                
        }
            
						    	        	        	        
	    double[] autoCorrelationValues = new double[partKeyWords.size()];
	    for(int i = 0; i < partKeyWords.size(); i++){
	        autoCorrelationValues[i] = partKeyWords.get(i).getAutoCorrelation();
	    }
	    
		if(debugMode){//////////////////////////////////////DEBUG MODE////////////////////////////////////////////
			//Get the autoCorrelationValues and put the values in a file
			int len = partKeyWords.size();			
			try {
				writer1 = new PrintWriter(nwtFolder+"/results/"+FolderName+"_"+window+"_"+KeyWord.delta+"_AutoCorrVal.csv", "UTF-8");
				String snippet1 = new String();
				snippet1 = "";
				double autoCorrelationValue;
				for(int i=0; i<len;i++){
					 autoCorrelationValue = partKeyWords.get(i).getAutoCorrelation();
					snippet1 = snippet1 + autoCorrelationValue + ";";
				}
				writer1.println(snippet1);
				writer1.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		} ////////////////////////////////////// END DEBUG MODE////////////////////////////////////////////
		
	    Threshold th1 = new Threshold();
	    theta1 = th1.theta1(autoCorrelationValues, gamma1);
	    System.out.println("[window "+window+"] : theta1 = "+theta1);
	    
	    // Removing trivial keywords based on theta1
	    LinkedList<KeyWord> partKeyWordsList1 = new LinkedList<>();
	    for(KeyWord k : partKeyWords){
	        if(k.getAutoCorrelation() > theta1){
	            partKeyWordsList1.add(k);
	        }
	    }        
	    for(KeyWord kw1 : partKeyWordsList1){
	        kw1.computeCrossCorrelation(partKeyWordsList1);
	    }
	    
		//display the remain Keywords and put them in a file				
		if(debugMode){//////////////////////////////////////DEBUG MODE////////////////////////////////////////////			
			//form of the fileName : FolderName_window_delta_gama_theta1__RemainKeyWords.txt
			try {
				writer1 = new PrintWriter(nwtFolder+"/results/"+FolderName+"_"+window+"_"+KeyWord.delta+"_"+gamma1+"_"+theta1+"_RemainKeyWords.txt", "UTF-8");
			int u=0;
			for(KeyWord k : partKeyWordsList1){
				System.out.println(" index ="+u+" : "+k.getKeyWord()); //display
				writer1.println(" index ="+u+" : "+k.getKeyWord()); //write in the file
				u++;
			}
			writer1.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}////////////////////////////////////// END DEBUG MODE////////////////////////////////////////////	    
	    
	    System.out.println("[window "+window+"] : number of non-trivial keywords = "+partKeyWordsList1.size());

	    for(int i=0; i<partKeyWordsList1.size(); i++){
	        partKeyWordsList1.get(i).computeCrossCorrelation(partKeyWordsList1);
	    }	     	    	    
	    double[][] bigMatrix = new double[partKeyWordsList1.size()][partKeyWordsList1.size()];
	    for(int i=0; i<partKeyWordsList1.size(); i++){
	        bigMatrix[i] = partKeyWordsList1.get(i).getCrossCorrelation();
	    }	
	    
		if(debugMode){//////////////////////////////////////DEBUG MODE////////////////////////////////////////////	
			//Put OriginalBigMatrix in a file
			//name of the file : NameOfTheFolder_Delta_Gama_Theta1_Theta2_OriginalBigMatrix.csv
			try {
				writer1 = new PrintWriter(nwtFolder+"/results/"+FolderName+"_"+window+"_"+KeyWord.delta+"_"+gamma1+"_"+theta1+"_"+theta2+"_OriginalBigMatrix.csv", "UTF-8");			
				for(int i=0; i<partKeyWordsList1.size(); i++){
					String valueSnippet = new String(); 					
					for(double x:bigMatrix[i]){
						valueSnippet = valueSnippet + x + ",";				
					}
					writer1.println(valueSnippet);
				}
				writer1.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}////////////////////////////////////// END DEBUG MODE////////////////////////////////////////////	  	    
	    	    
	    //compute theta2 using the bigmatrix
	    theta2 = th1.theta2(bigMatrix, gamma2);
	    System.out.println("[window "+window+"] : theta2 = "+theta2);
	    
	    for(int i = 0; i < partKeyWordsList1.size(); i++){
	        for(int j = i+1; j < partKeyWordsList1.size(); j++){
	            bigMatrix[i][j] = (bigMatrix[i][j] < theta2)?0:bigMatrix[i][j];
	        }
	    }	   	    			
			
		if(debugMode){	//////////////////////////////////////DEBUG MODE////////////////////////////////////////////			
			try {				
				//Filter OriginalBigMatrix with theta2 & Put FilteredBigMatrix in a file
				//name of the file : NameOfTheFolder_Delta_Gama_Theta1_Theta2_FilteredBigMatrix.csv
				writer1 = new PrintWriter(nwtFolder+"/results/"+FolderName+"_"+window+"_"+KeyWord.delta+"_"+gamma1+"_"+gamma2+"_"+theta1+"_"+theta2+"_FilteredBigMatrix.csv", "UTF-8");								
				for(int i=0; i<bigMatrix.length; i++){
					String valueSnippet = new String(); 
					for(int j=0; j<bigMatrix[0].length; j++){
						if(i==j){
							valueSnippet = valueSnippet + 0 + ",";	
						}
						else{
							if(bigMatrix[i][j] < theta2) valueSnippet = valueSnippet + 0 + ",";
							else valueSnippet = valueSnippet + bigMatrix[i][j] + ",";
						}
					}
					writer1.println(valueSnippet);
				}			
				writer1.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}////////////////////////////////////// END DEBUG MODE////////////////////////////////////////////	  	    
	    
	    System.out.println("\n --Modularity Detection for window "+window+"--");
	    ModularityDetection modularity;
		try {
			modularity = new ModularityDetection(partKeyWordsList1,bigMatrix,startDay,endDay,window,nwtFolder);	        			
		    ArrayList<Community> finalArrCom= modularity.getCommunitiesFiltered(thresholdE);
		    System.out.println("[window "+window+"] ["+startDay+","+endDay+"] : "+finalArrCom.size()+" communities with filter Epsylon = " +thresholdE);
		    
			// récupérer ici la liste d'évènements à partir de ModularityDetection		    		    
		    events.addAll(modularity.getEvents());
		    LinkedList<Event> tempListEvent=new LinkedList<>();
		    for(Event ev : events){
		    	if(ev.getEpsylon()<thresholdE)tempListEvent.add(ev);
		    }		    
		    events.removeAll(tempListEvent);
		    
		} catch (Exception e) {		
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}	    
	}

	public LinkedList<Event> getEvents() {
		return events;
	}	
		
}

