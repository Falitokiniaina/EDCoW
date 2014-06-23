package Dwt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import FileSystem.DiskFileExplorer;
import ch.epfl.lis.networks.NetworkException;

/**
 * This class is the main class for running EDCoW
 * @author Yue HE & Falitokiniaina RABEARISON
 *
 */
public class Main {
	public static HashMap<String, String> mapParam = new HashMap<String, String>();

    /**
     * To Treat the menu set parameters
     * - Display the menu that can be done with the parameters (display - modify)
     * At the same time, the values of the parameters in the memory are changed and the values in the 'param' file changed as well. 
     * @throws IOException
     */
	private static void setParameters() throws IOException {
		// TODO Auto-generated method stub	
		System.out.println("\n==== SET PARAMETERS ====");
		System.out.println("1 - Display actual parameters");
		System.out.println("2 - Change parameters");
		System.out.println("3 - Back");
		int menuChoi = choiceMenu();
		switch (menuChoi){
			case 1: 
					FileReader fileReader = new FileReader("param");
					BufferedReader buff = new BufferedReader(fileReader);
					String line = null;
					while ((line = buff.readLine()) != null) {
						System.out.println(line);
					}
					setParameters();
					break;
			case 2: 	
//				sizeWindow = 48
//				debugMode = true
//				gamma1 = 9
//				gamma2 = 8
//				thresholdE = 0.1
//				delta = 16				
					Scanner sc = new Scanner(System.in);
					String enteredScan = null;
					do{
						System.out.println("New debug mode? [Actual value = "+ mapParam.get("debugMode") +"]" );			
						enteredScan = sc.nextLine();	
						if(enteredScan.equalsIgnoreCase("")) enteredScan = mapParam.get("debugMode");					
					}while(!enteredScan.equalsIgnoreCase("false") && !enteredScan.equalsIgnoreCase("true") && !enteredScan.equalsIgnoreCase(""));
					mapParam.put("debugMode", enteredScan);
					
					enteredScan = null;
					do{
						System.out.println("New Size of windows? [Actual value = "+ mapParam.get("sizeWindow") +"]" );
						enteredScan = sc.nextLine();
						if(enteredScan.equalsIgnoreCase("")) enteredScan = mapParam.get("sizeWindow");
					}while(!enteredScan.equalsIgnoreCase("") && !(isInteger(enteredScan)));
					mapParam.put("sizeWindow", enteredScan);
					
					enteredScan = null;
					do{
						System.out.println("New Delta? [Actual value = "+ mapParam.get("delta") +"]" );
						enteredScan = sc.nextLine();
						if(enteredScan.equalsIgnoreCase("")) enteredScan = mapParam.get("delta");
					}while(!enteredScan.equalsIgnoreCase("") && !(isInteger(enteredScan)));
					mapParam.put("delta", enteredScan);									
					
					enteredScan = null;
					do{
						System.out.println("New gamma1? [Actual value = "+ mapParam.get("gamma1") +"]" );
						enteredScan = sc.nextLine();
						if(enteredScan.equalsIgnoreCase("")) enteredScan = mapParam.get("gamma1");
					}while(!enteredScan.equalsIgnoreCase("") && !(isInteger(enteredScan)));					
					mapParam.put("gamma1", enteredScan);
					
					enteredScan = null;
					do{
						System.out.println("New gamma2? [Actual value = "+ mapParam.get("gamma2") +"]" );
						enteredScan = sc.nextLine();
						if(enteredScan.equalsIgnoreCase("")) enteredScan = mapParam.get("gamma2");
					}while(!enteredScan.equalsIgnoreCase("") && !(isInteger(enteredScan)));					
					mapParam.put("gamma2", enteredScan);					
					
					enteredScan = null;
					do{
						System.out.println("New thresholdE? [Actual value = "+ mapParam.get("thresholdE") +"]" );
						enteredScan = sc.nextLine();
						if(enteredScan.equalsIgnoreCase("")) enteredScan = mapParam.get("thresholdE");
					}while(enteredScan.equalsIgnoreCase(""));
					mapParam.put("thresholdE", enteredScan);					
					
					writeInFileParam();
					setParameters();
					break;
			case 3:					
					break;
			default:
				System.out.println("Choice not OK.");
				setParameters();		
		}
	}
    
    /**
     * Write the current values in the HashMap mapParam as parameters, into the file 'param'
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    private static void writeInFileParam() throws FileNotFoundException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
    	PrintWriter writer = new PrintWriter("param", "UTF-8");
        Iterator<?> it = mapParam.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry)it.next();
            writer.println(pairs.getKey() + " = " + pairs.getValue());
            //it.remove(); // avoids a ConcurrentModificationException
        }
    	writer.close();		
	}

	/**
	 * Check if the scanned string can be converted to a number
	 * @param string String to be checked
	 * @return
	 */
    private static boolean isInteger (String string)
    {
        try {
            Integer.parseInt (string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }               						
	
	/**
	 * Load the parameters, located in the file param, into a HashMap 'mapParam'
	 * @throws IOException
	 */
	public static void loadParam() throws IOException{
		FileReader fileReader = new FileReader("param");
		@SuppressWarnings("resource")
		BufferedReader buff = new BufferedReader(fileReader);
		String line = null;
		while ((line = buff.readLine()) != null) {
			String[] tab = line.split(" = ");
			mapParam.put(tab[0],tab[1]);
		}		
	}	
	
	/**
	 * Print the main menu of the application
	 * @throws NetworkException
	 * @throws Exception
	 */
    public static void printMenu() throws NetworkException, Exception{    	
    	System.out.println("\n==== MENU ====");    	
    	System.out.println("1 - Set Parameters");
    	System.out.println("2 - Treat keywords of a folder");								    	
    	System.out.println("3 - Quit");				    	
        int menuChoice = choiceMenu();
    	
    	switch (menuChoice){
    		case 1:
    			setParameters();
    			break;
    		case 2:
    			TreatSignals();    			
    			break;	      	   								
    		case 3:
    			System.exit(0);    			
    			break;					    		    	
    		default:
    			System.out.println("Choice not OK.");
    			printMenu();            
        }
    }		

    /**
     * a little module to print the menu (force the user to enter a number)
     * @return choice of the user 
     */
	public static int choiceMenu() {
        @SuppressWarnings("resource")
		Scanner entree = new Scanner(System.in);
        int choix = 0;
        do {
            try {
                System.out.println("Enter your choice :");
                choix = entree.nextInt();
            }
            catch(InputMismatchException e) {
                entree.next();
                System.out.println("Enter a number");
            }
        }
        while (choix == 0);
        //entree.close();
        return choix;
    }		
	
/**
 * Method to run the application, for printing the menus	
 * @param args
 * @throws NetworkException
 * @throws Exception
 */
	public static void main(String[] args) throws NetworkException, Exception{
		loadParam();
		do
			printMenu();
		while(true);
	}
	
	/**
	 * Treat the whole signals (nt, nwt) by splitting them into windows. The length of the window is an input from the user, defined in the 'param' file.
	 * - That is read the files,
	 * - Put them in the memory,
	 * - affect to a thread to treat a window
	 * - process the threads selected by the user after prompt
	 * - put in 'events' all the events detected in each window
	 * If debug mode is enabled, all the files (detected events, detected keywords) are stored in the folder 'results'.
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private static void TreatSignals() throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		int nbWindow=0, nb = 0;
		LinkedList<KeyWord> longKeyWords = new LinkedList<>();
		LinkedList<Event> events = new LinkedList<>();
		String ntFilePath=null;
		String nwtFolderPath=null;
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);	
				
		System.out.println("Filename containing nt signals? [./Twitter-data/Cen/Cen.nt] : ");
		ntFilePath = sc.nextLine();
		if(ntFilePath.equalsIgnoreCase(""))	ntFilePath = "./Twitter-data/Cen/Cen.nt";	
		KeyWord.setLongNt(ntFilePath);
		System.out.println("There are " + KeyWord.longNt.length + " signal points.");		
		
		nbWindow = (int)KeyWord.longNt.length/Integer.valueOf(mapParam.get("sizeWindow"));
				
		System.out.println("\nFolder containing nwt files? [./Twitter-data/Cen] : ");
		nwtFolderPath = sc.nextLine();
		if(nwtFolderPath.equalsIgnoreCase(""))	nwtFolderPath = "./Twitter-data/Cen";
		//create the folder "results"
		File fb = new File(nwtFolderPath+"/results"); 
		fb.mkdirs();
		fb = new File(nwtFolderPath+"/results/Jmod"); 
		fb.mkdirs();
		
		DiskFileExplorer expl = new DiskFileExplorer(nwtFolderPath, false);
		File[] filesNWT = expl.FilesNWT();		

		//KeyWord[] keyWords = new KeyWord[filesNWT.length];		
				
		if (filesNWT != null && filesNWT.length!=0) {
			System.out.println("There are "+filesNWT.length+1+" keywords found in the folder.");
			System.out.println("Storing the signals in memory ...");
			for(int i=0; i<filesNWT.length; i++){											
				String filePath = filesNWT[i].getPath();
				String fileName = filesNWT[i].getName();
				
				FileReader fileReadear = new FileReader(filePath);
				BufferedReader buff = new BufferedReader(fileReadear);				
				String firstLine = buff.readLine();
				/** separate the data with delimiter ; */			
				StringTokenizer st = new StringTokenizer(firstLine, ";");		
				nb = 0;
				double[] nwt = new double[st.countTokens()];
				while(st.hasMoreTokens()){				
					nwt[nb] = Double.parseDouble(st.nextToken());
					nb++;
				}
				buff.close();
				fileReadear.close();
				//computations();
				longKeyWords.add(new KeyWord(filePath, fileName, nwt, Integer.valueOf(mapParam.get("delta"))));							
			}
		}
		else{
			System.out.println("There is no files in the specified folder.");
		}
				
		ProcessWindow[] windows = new ProcessWindow[nbWindow];
		Thread[] threads = new Thread[nbWindow];
		
		for(int i=0; i<nbWindow; i++){											
			ProcessWindow window = new ProcessWindow(i,Integer.valueOf(mapParam.get("sizeWindow")), longKeyWords, Integer.valueOf(mapParam.get("delta")), Integer.valueOf(mapParam.get("gamma1")), Integer.valueOf(mapParam.get("gamma2")),  Double.valueOf(mapParam.get("thresholdE")), Boolean.valueOf(mapParam.get("debugMode")),nwtFolderPath);	
			windows[i] = window;
			threads[i] = new Thread(window);				
		}
		
		String enteredWindows="";
		do{
			System.out.println("\nThere are 0 to "+(nbWindow-1)+" windows. which ones to treat? [Form : 0,1,2,3]");
			enteredWindows = sc.nextLine();			
		}while(enteredWindows.equalsIgnoreCase(""));
		
		System.out.println("Launching Thread Treating window(s) ...");
		
		String[] tab = enteredWindows.split(",");
		for(int i=0; i<tab.length; i++){			
			threads[Integer.valueOf(tab[i])].start();
			threads[Integer.valueOf(tab[i])].join();			
		}
//		for(Thread t : threads){								
//			t.start();
//			t.join();				
//		}			
		System.out.println("END of window(s) treatment.");		
		for(int i=0; i<tab.length; i++){
			int y = Integer.valueOf(tab[i]);				
			events.addAll(windows[y].getEvents());			
		}
		
//		for(ProcessWindow pw : windows){
//			events.addAll(pw.getEvents());
//		}
		
		System.out.println("\n\n====  Detected Event(s) : ======");		
		
		if(events.size()>0){
			String snippetResult = "";
			for(Event ev : events){				
				System.out.println(ev.toString().replaceAll(".nwt", ""));
				if(snippetResult.equalsIgnoreCase(""))snippetResult = ev.toString().replaceAll(".nwt", "");
				else snippetResult = snippetResult+"\n"+ev.toString().replaceAll(".nwt", "");
				
				if(Boolean.valueOf(mapParam.get("debugMode"))){//////////////////////////////////////DEBUG MODE////////////////////////////////////////////					
					//Write in files SW1 and SW2 of Keywords of the events
					for(KeyWord kw : longKeyWords){
						for(String kwString : ev.getKeywords()){
							if(kw.getKeyWord().equalsIgnoreCase(kwString)){																
								kw.computations();								
								try {
									PrintWriter writer1 = new PrintWriter(nwtFolderPath+"/results/"+kwString+"_SW1_SW2.csv", "UTF-8");																											
									writer1.println(kwString +":" );
									writer1.println("SW1 : " + kw.getSW1ToString());
									writer1.println("SW2 : " + kw.getSW2ToString());
									writer1.close();
								} catch (FileNotFoundException | UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}					
																
					}
				} ////////////////////////////////////// END DEBUG MODE////////////////////////////////////////////				
			}
			
			if(Boolean.valueOf(mapParam.get("debugMode"))){//////////////////////////////////////DEBUG MODE////////////////////////////////////////////
			//writing information of the events in a file
				try {
					PrintWriter writer1 = new PrintWriter(nwtFolderPath+"/results/Events_"+mapParam.get("delta")+"_"+mapParam.get("gamma1")+"_"+mapParam.get("gamma2")+".txt", "UTF-8");																											
					writer1.println(snippetResult);
					writer1.close();
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}																					
		}

		else
			System.out.println("Clusters filtered with Epsilon, There is no event detected");
	} //END TreatSignals() 
}
