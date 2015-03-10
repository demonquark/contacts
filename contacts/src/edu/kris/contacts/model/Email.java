package edu.kris.contacts.model;

public class Email {
	
	private String address 	= null;
	private String type		= null;

	public Email (){
	}
	
	public Email(String a, String t) {
		this.address = a;
		this.type = t;
	}

	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String t) {
		this.type = t;
	}
	
	/**
	 * Returns an array of the class variables as elements of the array. 
	 * @return an array of the class variables as elements of the array.
	 */
	public String [] getValues(){
		String [] values = new String [2];
		values[0] = address;
		values[1] = type;
		return values;
	}
 	
	public void readValues(String [] values){
		if(values != null && values.length >= 2){
			address 	= values[0];
			type		= values[1]; 
		}
	}
 	
	public static String [] getHeader(){
		String [] header = new String [2];
		header[0] = "address";
		header[1] = "type";
		return header;
	}
}
