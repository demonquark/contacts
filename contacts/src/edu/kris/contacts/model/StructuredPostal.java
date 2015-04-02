package edu.kris.contacts.model;

public class StructuredPostal {
 	private String asString 	= null;
 	private int type			= -1;
 	private String label 		= null;
 	private String street 		= null;
 	private String pobox 		= null;
 	private String neighborhood	= null;
 	private String city		 	= null;
 	private String region 		= null;
 	private String postcode		= null;
 	private String country		= null;
 	
 	public int getType() {
 		return type;
 	}
 	public void setType(int type) {
 		this.type = type;
 	}
 	public String getLabel() {
		return label;
	}
	public String getStreet() {
		return street;
	}
	public String getPobox() {
		return pobox;
	}
	public String getNeighborhood() {
		return neighborhood;
	}
	public String getCity() {
		return city;
	}
	public String getRegion() {
		return region;
	}
	public String getPostcode() {
		return postcode;
	}
	public String getCountry() {
		return country;
	}
	public String toString() {
 		if (this.asString.length() > 0) {
 			return(this.asString);
 		} else {
 			String addr = "";
 			if (this.pobox != null) {
 				addr = addr + this.pobox + "\n";
 			}
 			if (this.street != null) {
 				addr = addr + this.street + "\n";
 			}
 			if (this.city != null) {
 				addr = addr + this.city + ", ";
 			}
 			if (this.region != null) {
 				addr = addr + this.region + " ";
 			}
 			if (this.postcode != null) {
 				addr = addr + this.postcode + " ";
 			}
 			if (this.country != null) {
 				addr = addr + this.country;
 			}
 			return(addr);
 		}
 	}
 	
 	public StructuredPostal() {
 	}
 	
 	public StructuredPostal(String [] values) {
 		readValues(values);
 	} 	
 	
	/**
	 * Returns an array of the class variables as elements of the array. 
	 * @return an array of the class variables as elements of the array.
	 */
	public String [] getValues(){
		String [] values = new String [10];
		values[0] = asString;
		values[1] = (!toString().endsWith("") || type >= 0) ? String.valueOf(type) : null;
		values[2] = label;
		values[3] = street;
		values[4] = pobox;
		values[5] = neighborhood;
		values[6] = city;
		values[7] = region;
		values[8] = postcode;
		values[9] = country;
		return values;
	}
	
	public void readValues(String [] values){
		if(values != null && values.length >= 10){
			asString	= values[0]; 
			type		= values[1] != null ? Integer.valueOf(values[1]) : -1;
			label		= values[2];
			street		= values[3]; 
			pobox		= values[4]; 
			neighborhood= values[5]; 
			city		= values[6]; 
			region		= values[7]; 
			postcode	= values[8]; 
			country		= values[9]; 
		}
	}
	 	
	public static String [] getHeader(){
		String [] header = new String [10];
		header[0] = "Address";
		header[1] = "type";
		header[2] = "label";
		header[3] = "street";
		header[4] = "pobox";
		header[5] = "neighborhood";
		header[6] = "city";
		header[7] = "region";
		header[8] = "postcode";
		header[9] = "country";
		return header;
	}
}
