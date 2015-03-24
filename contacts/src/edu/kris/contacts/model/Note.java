package edu.kris.contacts.model;

public class Note {
	private String data 	= null;

	public Note (){
	}
	
	public Note(String [] values) {
		readValues(values);
	}

	public String getNote() {
		return data;
	}
	
	public void setNote(String note) {
		this.data = note;
	}
	
	/**
	 * Returns an array of the class variables as elements of the array. 
	 * @return an array of the class variables as elements of the array.
	 */
	public String [] getValues(){
		String [] values = new String [1];
		values[0] = data;
		return values;
	}
 	
	public void readValues(String [] values){
		if(values != null && values.length >= 1){
			data 	= values[0];
		}
	}
 	
	public static String [] getHeader(){
		String [] header = new String [1];
		header[0] = "note";
		return header;
	}

}
