package Dwt;

import java.util.LinkedList;

/**
 * This class is to easily get the information about an event.
 * @author Yue HE & Falitokiniaina RABEARISON
 *
 */
public class Event implements Comparable<Event>{
    public LinkedList<String> keywords;
    public double epsylon;
    public float startDay;
    public float endDay;
    public int window;
    
    /**
     * Constructor
     * @param keywords_ List of the keywords corresponding to the event
     * @param startDay_ start index of the event
     * @param endDay_ end index of the event
     * @param window_ number of window containing the event
     */
    public Event(LinkedList<String> keywords_, float startDay_, float endDay_,int window_){
        keywords = keywords_;
        startDay = startDay_;
        endDay = endDay_;
        window = window_;
    }
    
    public Event(){
        keywords = new LinkedList<>();
    }
    
    public String getKeywordsAsString(){
        String str = "";
        for(String keyword : keywords){
            str += keyword+" ";
        }
        return str;
    } 
    public String getIntervalAsString(){
    	return startDay+";"+endDay;
    }

	public void setEpsylon(double epsylon) {
		this.epsylon = epsylon;
	}

	public void setStartDay(float startDay) {
		this.startDay = startDay;
	}

	public void setEndDay(float endDay) {
		this.endDay = endDay;
	}

	public double getEpsylon() {
		return epsylon;
	}

	@Override
	public int compareTo(Event arg0) {
		// TODO Auto-generated method stub
		if(this.toString().equalsIgnoreCase(arg0.toString()))
		return 0;
		else return 1;
	}
    
	/**
	 * Format the display of an event
	 */
	public String toString(){
		String listWords = "";
		for(String kw : keywords){
			if(listWords.equalsIgnoreCase(""))listWords = kw+", ";
			else listWords = listWords + kw+", ";
		}
		return  "[window "+window+"] ["+startDay+","+endDay+"] / Epsylon: "+epsylon+" / Keywords : "+listWords;
	}

	public void setWindow(int window_) {
		// TODO Auto-generated method stub
		window = window_;
	}

	public LinkedList<String> getKeywords() {
		return keywords;
	}			
}
