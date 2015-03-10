package edu.kris.contacts.model;

public class IM {
	
	private String name	= null;
	private String type	= null;

	public IM() {
 	}

	public IM(String name, String type) {
 		this.name = name;
 		this.type = type;
 	}
 	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Returns an array of the class variables as elements of the array. 
	 * @return an array of the class variables as elements of the array.
	 */
	public String [] getValues(){
		String [] values = new String [2];
		values[0] = name;
		values[1] = type;
		return values;
	}
 	
	public void readValues(String [] values){
		if(values != null && values.length >= 2){
			name 	= values[0];
			type	= values[1]; 
		}
	}
 	
	public static String [] getHeader(){
		String [] header = new String [2];
		header[0] = "name";
		header[1] = "type";
		return header;
	}
}
