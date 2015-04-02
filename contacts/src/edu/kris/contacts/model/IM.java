package edu.kris.contacts.model;

public class IM {
	
	private String data				= null;
	private int type				= -1;
	private String label			= null;
	private String protocol 		= null;
	private String customProtocol 	= null;

	public IM() {
 	}

	public String getData() {
		return data;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public String getCustomProtocol() {
		return customProtocol;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public IM(String [] values) {
		readValues(values);
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
		String [] values = new String [5];
		values[0] = data;
		values[1] = (data != null || type >= 0) ? String.valueOf(type) : null;
		values[2] = label;
		values[3] = protocol;
		values[4] = customProtocol;
		return values;
	}
 	
	public void readValues(String [] values){
		if(values != null && values.length >= 5){
			data 			= values[0];
			type			= values[1] != null ? Integer.valueOf(values[1]) : -1;
			label			= values[2]; 
			protocol		= values[3]; 
			customProtocol	= values[4]; 
		}
	}
 	
	public static String [] getHeader(){
		String [] header = new String [5];
		header[0] = "data";
		header[1] = "type";
		header[2] = "label";
		header[3] = "protocol";
		header[4] = "customProtocol";
		return header;
	}
}
