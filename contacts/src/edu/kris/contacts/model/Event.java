package edu.kris.contacts.model;

public class Event {
	
	private String date		= null;
	private int type		= -1;
	private String label	= null;

	public Event (){
	}
	
	public Event(String [] values) {
		readValues(values);
	}

	public String getDate() {
		return date;
	}
	
	public void setURL(String date) {
		this.date = date;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int t) {
		this.type = t;
	}
	
	/**
	 * Returns an array of the class variables as elements of the array. 
	 * @return an array of the class variables as elements of the array.
	 */
	public String [] getValues(){
		String [] values = new String [3];
		values[0] = date;
		values[1] = String.valueOf(type);
		values[2] = label;
		return values;
	}
 	
	public void readValues(String [] values){
		if(values != null && values.length >= 3){
			date 	= values[0];
			type	= values[1] != null ? Integer.valueOf(values[1]) : -1;
			label	= values[2];
		}
	}
 	
	public static String [] getHeader(){
		String [] header = new String [3];
		header[0] = "date";
		header[1] = "type";
		header[2] = "label";
		return header;
	}
}
