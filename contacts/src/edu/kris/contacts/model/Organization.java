package edu.kris.contacts.model;

public class Organization {
 	private String organization 	= null;
 	private String title 			= null;
 	
 	public String getOrganization() {
 		return organization;
 	}
 	public void setOrganization(String organization) {
 		this.organization = organization;
 	}
 	public String getTitle() {
 		return title;
 	}
 	public void setTitle(String title) {
 		this.title = title;
 	}
 	
 	public Organization() {
 		
 	}
 	public Organization(String org, String title) {
 		this.organization = org;
 		this.title = title;
 	}
 	
	/**
	 * Returns an array of the class variables as elements of the array. 
	 * @return an array of the class variables as elements of the array.
	 */
	public String [] getValues(){
		String [] values = new String [2];
		values[0] = organization;
		values[1] = title;
		return values;
	}
 	
	public void readValues(String [] values){
		if(values != null && values.length >= 2){
			organization 	= values[0];
			title 			= values[1]; 
		}
	}
	
	public static String [] getHeader(){
		String [] header = new String [2];
		header[0] = "organization";
		header[1] = "title";
		return header;
	}
}
