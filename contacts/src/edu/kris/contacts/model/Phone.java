package edu.kris.contacts.model;

public class Phone {
 	private String number	= null;
 	private int type		= -1;
 	private String label	= null;
 	
 	public String getNumber() {
 		return number;
 	}
 
 	public void setNumber(String number) {
 		this.number = number;
 	}
 
 	public int getType() {
 		return type;
 	}
 
 	public void setType(int type) {
 		this.type = type;
 	}
 
 	public String getLabel() {
 		return label;
 	}
 
 	public void setLabel(String label) {
 		this.label = label;
 	}

 	public Phone () {
 		
 	}

 	public Phone(String [] values) {
 		readValues(values);
 	}
 	
	/**
	 * Returns an array of the class variables as elements of the array. 
	 * @return an array of the class variables as elements of the array.
	 */
	public String [] getValues(){
		String [] values = new String [3];
		values[0] = number;
		values[1] = (number != null || type >= 0) ? String.valueOf(type) : null;
		values[2] = label;
		return values;
	}
 	
	public void readValues(String [] values){
		if(values != null && values.length >= 3){
			number 	= values[0];
			type	= values[1] != null ? Integer.valueOf(values[1]) : -1;
			label	= values[2];
		}
	}
 	
	public static String [] getHeader(){
		String [] header = new String [3];
		header[0] = "number";
		header[1] = "type";
		header[2] = "label";
		return header;
	}
}
