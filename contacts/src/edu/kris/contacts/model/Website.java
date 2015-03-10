package edu.kris.contacts.model;

public class Website {
	
	private String url 	= null;
	private int type		= -1;
	private String label	= null;

	public Website (){
	}
	
	public Website(String [] values) {
		readValues(values);
	}

	public String getURL() {
		return url;
	}
	
	public void setURL(String url) {
		this.url = url;
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
		values[0] = url;
		values[1] = String.valueOf(type);
		values[2] = label;
		return values;
	}
 	
	public void readValues(String [] values){
		if(values != null && values.length >= 3){
			url 	= values[0];
			type	= values[1] != null ? Integer.valueOf(values[1]) : -1;
			label	= values[2];
		}
	}
 	
	public static String [] getHeader(){
		String [] header = new String [3];
		header[0] = "url";
		header[1] = "type";
		header[2] = "label";
		return header;
	}
}
