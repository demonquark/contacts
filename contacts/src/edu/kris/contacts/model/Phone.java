package edu.kris.contacts.model;

public class Phone {
 	private String number	= null;
 	private String type		= null;
 	
 	public String getNumber() {
 		return number;
 	}
 
 	public void setNumber(String number) {
 		this.number = number;
 	}
 
 	public String getType() {
 		return type;
 	}
 
 	public void setType(String type) {
 		this.type = type;
 	}
 
 	public Phone () {
 		
 	}

 	public Phone(String n, String t) {
 		this.number = n;
 		this.type = t;
 	}
 	
	/**
	 * Returns an array of the class variables as elements of the array. 
	 * @return an array of the class variables as elements of the array.
	 */
	public String [] getValues(){
		String [] values = new String [2];
		values[0] = number;
		values[1] = type;
		return values;
	}
 	
	public void readValues(String [] values){
		if(values != null && values.length >= 2){
			number 	= values[0];
			type	= values[1]; 
		}
	}
 	
	public static String [] getHeader(){
		String [] header = new String [2];
		header[0] = "number";
		header[1] = "type";
		return header;
	}
}
