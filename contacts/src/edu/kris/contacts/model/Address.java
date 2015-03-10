package edu.kris.contacts.model;

public class Address {
 	private String poBox 		= null;
 	private String street 		= null;
 	private String city 		= null;
 	private String state 		= null;
 	private String postalCode 	= null;
 	private String country 		= null;
 	private String type			= null;
 	private String asString 	= null;

 	public String getType() {
 		return type;
 	}
 	public void setType(String type) {
 		this.type = type;
 	}
 	public String getPoBox() {
 		return poBox;
 	}
 	public void setPoBox(String poBox) {
 		this.poBox = poBox;
 	}
 	public String getStreet() {
 		return street;
 	}
 	public void setStreet(String street) {
 		this.street = street;
 	}
 	public String getCity() {
 		return city;
 	}
 	public void setCity(String city) {
 		this.city = city;
 	}
 	public String getState() {
 		return state;
 	}
 	public void setState(String state) {
 		this.state = state;
 	}
 	public String getPostalCode() {
 		return postalCode;
 	}
 	public void setPostalCode(String postalCode) {
 		this.postalCode = postalCode;
 	}
 	public String getCountry() {
 		return country;
 	}
 	public void setCountry(String country) {
 		this.country = country;
 	}
 	public String toString() {
 		if (this.asString.length() > 0) {
 			return(this.asString);
 		} else {
 			String addr = "";
 			if (this.getPoBox() != null) {
 				addr = addr + this.getPoBox() + "\n";
 			}
 			if (this.getStreet() != null) {
 				addr = addr + this.getStreet() + "\n";
 			}
 			if (this.getCity() != null) {
 				addr = addr + this.getCity() + ", ";
 			}
 			if (this.getState() != null) {
 				addr = addr + this.getState() + " ";
 			}
 			if (this.getPostalCode() != null) {
 				addr = addr + this.getPostalCode() + " ";
 			}
 			if (this.getCountry() != null) {
 				addr = addr + this.getCountry();
 			}
 			return(addr);
 		}
 	}
 	
 	public Address() {
 	}
 	
 	public Address(String poBox, String street, String city, String state, 
 			String postal, String country, String type) {
 		this.setPoBox(poBox);
  		this.setStreet(street);
 		this.setCity(city);
 		this.setState(state);
 		this.setPostalCode(postal);
 		this.setCountry(country);
 		this.setType(type);
 	} 	
 	
	/**
	 * Returns an array of the class variables as elements of the array. 
	 * @return an array of the class variables as elements of the array.
	 */
	public String [] getValues(){
		String [] values = new String [8];
		values[0] = poBox;
		values[1] = street;
		values[2] = city;
		values[3] = state;
		values[4] = postalCode;
		values[5] = country;
		values[6] = type;
		values[7] = asString;
		return values;
	}
	
	public void readValues(String [] values){
		if(values != null && values.length >= 8){
			poBox		= values[0];
			street		= values[1]; 
			city		= values[2]; 
			state		= values[3]; 
			postalCode	= values[4]; 
			country		= values[5]; 
			type		= values[6]; 
			asString	= values[7]; 
		}
	}
	 	
	public static String [] getHeader(){
		String [] header = new String [8];
		header[0] = "poBox";
		header[1] = "street";
		header[2] = "city";
		header[3] = "state";
		header[4] = "postalCode";
		header[5] = "country";
		header[6] = "type";
		header[7] = "asString";
		return header;
	}
}
