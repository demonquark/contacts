package edu.kris.contacts.model;

public class Email {
	
	private String address 	= null;
	private int type		= -1;
	private String label	= null;

	public Email (){
	}
	
	public Email(String [] values) {
		readValues(values);
	}

	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int t) {
		this.type = t;
	}

 	public String getLabel() {
 		return label;
 	}
 
 	public void setLabel(String label) {
 		this.label = label;
 	}
	
	/**
	 * Returns an array of the class variables as elements of the array. 
	 * @return an array of the class variables as elements of the array.
	 */
	public String [] getValues(){
		String [] values = new String [3];
		values[0] = address;
		values[1] = (address != null || type >= 0) ? String.valueOf(type) : null;
		values[2] = label;
		return values;
	}
 	
	public void readValues(String [] values){
		if(values != null && values.length >= 3){
			address 	= values[0];
			type		= values[1] != null ? Integer.valueOf(values[1]) : -1;
			label		= values[2];
		}
	}
 	
	public static String [] getHeader(){
		String [] header = new String [3];
		header[0] = "address";
		header[1] = "type";
		header[2] = "label";
		return header;
	}
}
