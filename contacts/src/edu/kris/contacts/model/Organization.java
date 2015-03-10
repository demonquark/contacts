package edu.kris.contacts.model;

public class Organization {
 	private String company 			= null;
 	private int type				= -1;
 	private String label			= null;
 	private String title 			= null;
 	private String department 		= null;
 	private String jobDescription	= null;
 	private String symbol			= null;
 	private String phoneticName		= null;
 	private String officeLocation	= null;
 	

 	public Organization() {
 	}
 	public Organization(String [] values){
 		readValues(values);
 	}
 	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
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
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getJobDescription() {
		return jobDescription;
	}
	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getPhoneticName() {
		return phoneticName;
	}
	public void setPhoneticName(String phoneticName) {
		this.phoneticName = phoneticName;
	}
	public String getOfficeLocation() {
		return officeLocation;
	}
	public void setOfficeLocation(String officeLocation) {
		this.officeLocation = officeLocation;
	}
	public String getTitle() {
 		return title;
 	}
 	public void setTitle(String title) {
 		this.title = title;
 	}
 	
	/**
	 * Returns an array of the class variables as elements of the array. 
	 * @return an array of the class variables as elements of the array.
	 */
	public String [] getValues(){
		String [] values = new String [9];
		values[0] = company;
		values[1] = String.valueOf(type);
		values[2] = label;
		values[3] = title;
		values[4] = department;
		values[5] = jobDescription;
		values[6] = symbol;
		values[7] = phoneticName;
		values[8] = officeLocation;
		return values;
	}
 	
	public void readValues(String [] values){
		if(values != null && values.length >= 9){
			company		 	= values[0];
			type			= values[1] != null ? Integer.valueOf(values[1]) : -1;
			label 			= values[2]; 
			title 			= values[3]; 
			department		= values[4]; 
			jobDescription	= values[5]; 
			symbol 			= values[6];
			phoneticName	= values[7]; 
			officeLocation	= values[8]; 
		}
	}
	
	public static String [] getHeader(){
		String [] header = new String [9];
		header[0] = "compay";
		header[1] = "type";
		header[2] = "label";
		header[3] = "title";
		header[4] = "department";
		header[5] = "jobDescription";
		header[6] = "symbol";
		header[7] = "phoneticName";
		header[8] = "officeLocation";
		return header;
	}
}
