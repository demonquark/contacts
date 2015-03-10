package edu.kris.contacts.model;

public class StructuredName {
	private String givenName 			= null;
	private String familyName 			= null;
	private String prefix 				= null;
	private String middleName 			= null;
	private String suffix 				= null;
	private String phoneticGivenName 	= null;
	private String phoneticMiddleName 	= null;
	private String phoneticFamilyName 	= null;
	
	public String getPrefix() {
		return prefix;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getPhoneticGivenName() {
		return phoneticGivenName;
	}
	public void setPhoneticGivenName(String phoneticGivenName) {
		this.phoneticGivenName = phoneticGivenName;
	}
	public String getPhoneticMiddleName() {
		return phoneticMiddleName;
	}
	public void setPhoneticMiddleName(String phoneticMiddleName) {
		this.phoneticMiddleName = phoneticMiddleName;
	}
	public String getPhoneticFamilyName() {
		return phoneticFamilyName;
	}
	public void setPhoneticFamilyName(String phoneticFamilyName) {
		this.phoneticFamilyName = phoneticFamilyName;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	
	/**
	 * Returns an array of the class variables as elements of the array. 
	 * @return an array of the class variables as elements of the array.
	 */
	public String [] getValues(){
		String [] values = new String [8];
		values[0] = givenName;
		values[1] = familyName;
		values[2] = prefix;
		values[3] = middleName;
		values[4] = suffix;
		values[5] = phoneticGivenName;
		values[6] = phoneticMiddleName;
		values[7] = phoneticFamilyName;
		return values;
	}
 	
	public void readValues(String [] values){
		if(values != null && values.length >= 8){
			givenName			= values[0];
			familyName			= values[1]; 
			prefix				= values[2]; 
			middleName			= values[3]; 
			suffix				= values[4]; 
			phoneticGivenName	= values[5]; 
			phoneticMiddleName	= values[6]; 
			phoneticFamilyName	= values[7]; 
		}
	}

	public static String [] getHeader(){
		String [] header = new String [8];
		header[0] = "givenName";
		header[1] = "familyName";
		header[2] = "prefix";
		header[3] = "middleName";
		header[4] = "suffix";
		header[5] = "phoneticGivenName";
		header[6] = "phoneticMiddleName";
		header[7] = "phoneticFamilyName";
		return header;
	}
}
