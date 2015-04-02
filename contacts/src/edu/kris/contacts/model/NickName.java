package edu.kris.contacts.model;

public class NickName {
	
	private String name 	= null;
	private int type		= -1;
	private String label	= null;

	public NickName (){
	}
	
	public NickName(String [] values) {
		readValues(values);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
		values[0] = name;
		values[1] = (name != null || type >= 0) ? String.valueOf(type) : null;
		values[2] = label;
		return values;
	}
 	
	public void readValues(String [] values){
		if(values != null && values.length >= 3){
			name 		= values[0];
			type		= values[1] != null ? Integer.valueOf(values[1]) : -1;
			label		= values[2];
		}
	}
 	
	public static String [] getHeader(){
		String [] header = new String [3];
		header[0] = "name";
		header[1] = "type";
		header[2] = "label";
		return header;
	}
}
