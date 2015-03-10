package edu.kris.contacts.model;

public class AccountInfo {
	private String accountName = null;
	private String accountType = null;
		
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
	/**
	 * Returns an array of the class variables as elements of the array. 
	 * @return an array of the class variables as elements of the array.
	 */
	public String [] getValues(){
		String [] values = new String [2];
		values[0] = accountName;
		values[1] = accountType;
		return values;
	}
	
	public void readValues(String [] values){
		if(values != null && values.length >= 2){
			accountName	= values[0];
			accountType	= values[1]; 
		}
	}
	 	
	public static String [] getHeader(){
		String [] header = new String [2];
		header[0] = "accountName";
		header[1] = "accountType";
		return header;
	}
}
