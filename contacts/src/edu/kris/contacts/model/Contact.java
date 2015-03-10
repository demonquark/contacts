package edu.kris.contacts.model;

import java.util.ArrayList;
import java.util.Arrays;

public class Contact {
 	private String id 						= null;
 	private String displayName 				= null;
 	private ArrayList<Phone> phone 			= new ArrayList<Phone>();
 	private ArrayList<Email> email 			= new ArrayList<Email>();
 	private ArrayList<String> notes 		= new ArrayList<String>();
 	private ArrayList<Address> addresses 	= new ArrayList<Address>();
 	private ArrayList<IM> imAddresses 		= new ArrayList<IM>();
 	private Organization organization 		= new Organization();
 	private StructuredName structuredName 	= new StructuredName ();
 	private AccountInfo account 			= new AccountInfo ();
 	
 	public Contact(){}
 	public Contact(String [] csvValues) {
 		readValues(csvValues);
 	}
 	
 	public AccountInfo getAccount() {
		return account;
	}
	public void setAccount(AccountInfo account) {
		this.account = account;
	}
	public Organization getOrganization() {
 		return organization;
 	}
 	public void setOrganization(Organization organization) {
 		this.organization = organization;
 	}
 	public ArrayList<IM> getImAddresses() {
 		return imAddresses;
 	}
 	public void setImAddresses(ArrayList<IM> imAddresses) {
 		this.imAddresses = imAddresses;
  	}
 	public void addImAddresses(IM imAddr) {
 		this.imAddresses.add(imAddr);
 	}
 	public ArrayList<String> getNotes() {
 		return notes;
 	}
 	public void setNotes(ArrayList<String> notes) {
 		this.notes = notes;
 	}
 	public void addNote(String note) {
 		this.notes.add(note);
 	}
 	public ArrayList<Address> getAddresses() {
 		return addresses;
 	}
 	public void setAddresses(ArrayList<Address> addresses) {
 		this.addresses = addresses;
 	}
 	public void addAddress(Address address) {
 		this.addresses.add(address);
 	}
 	public ArrayList<Email> getEmail() {
 		return email;
 	}
 	public void setEmail(ArrayList<Email> email) {
 		this.email = email;
 	}
 	public void addEmail(Email e) {
 		this.email.add(e);
 	}	
 	public String getId() {
 		return id;
 	}
 	public void setId(String id) {
  		this.id = id;
 	}
 	public String getDisplayName() {
 		return displayName;
 	}
 	public void setDisplayName(String dName) {
 		this.displayName = dName;
 	}
 	public ArrayList<Phone> getPhone() {
 		return phone;
 	}
 	public void setPhone(ArrayList<Phone> phone) {
 		this.phone = phone;
 	}
 	public void addPhone(Phone phone) {
 		this.phone.add(phone);
 	}
	public StructuredName getStructuredName() {
		return structuredName;
	}
	public void setStructuredName(StructuredName structuredName) {
		this.structuredName = structuredName;
	}
	
	public static final String MARK				= "###";
	public static final String CSVphoneMark 	= MARK+"phone"+MARK;
	public static final String CSVemailMark 	= MARK+"email"+MARK;
	public static final String CSVnotesMark 	= MARK+"notes"+MARK;
	public static final String CSVaddressMark 	= MARK+"address"+MARK;
	public static final String CSVimMark 		= MARK+"im"+MARK;

	public void readValues(String [] values){
		
		int i = 0;
		int sublen = 0;

		// Read the class values from the array
		id = values.length < 1 ? "" : values[0]; 
		displayName = values.length < 2 ? "" : values[1]; 
		i += 2;
		
		// Read 1 organization from the array
		organization = new Organization();
		if(values.length >= i + Organization.getHeader().length)
			organization.readValues(Arrays.copyOfRange(values, i, i + Organization.getHeader().length));
		i += Organization.getHeader().length;
		
		// Read 1 StructuredName from the array
		structuredName = new StructuredName();
		if(values.length >= i + StructuredName.getHeader().length)
			structuredName.readValues(Arrays.copyOfRange(values, i, i + StructuredName.getHeader().length));
		i += StructuredName.getHeader().length;

		// Read 1 Account from the array
		account = new AccountInfo();
		if(values.length >= i + AccountInfo.getHeader().length)
			account.readValues(Arrays.copyOfRange(values, i, i + AccountInfo.getHeader().length));
		i += AccountInfo.getHeader().length;

		// Read 1 Phone from the array
		Phone newPhone = new Phone ();
		if(values.length >= i + Phone.getHeader().length)
			newPhone.readValues(Arrays.copyOfRange(values, i, i + Phone.getHeader().length));
		i += Phone.getHeader().length;
		phone = new ArrayList <Phone> ();
		if(newPhone.getType() != null)
			phone.add(newPhone);
		
		// Read 1 Email from the array
		Email newEmail = new Email ();
		if(values.length >= i + Email.getHeader().length)
			newEmail.readValues(Arrays.copyOfRange(values, i, i + Email.getHeader().length));
		i += Email.getHeader().length;
		email = new ArrayList <Email> ();
		if(newEmail.getType() != null)
			email.add(newEmail);

		// Read 1 Address from the array
		Address newAddress = new Address ();
		if(values.length >= i + Address.getHeader().length)
			newAddress.readValues(Arrays.copyOfRange(values, i, i + Address.getHeader().length));
		i += Address.getHeader().length;
		addresses = new ArrayList <Address> ();
		addresses.add(newAddress);
		if(newAddress.getType() != null)
			addresses.add(newAddress);
		
		// Read 1 IM from the array
		IM newIM = new IM ();
		if(values.length >= i + IM.getHeader().length)
			newIM.readValues(Arrays.copyOfRange(values, i, i + IM.getHeader().length));
		i += IM.getHeader().length;
		imAddresses = new ArrayList <IM> ();
		if(newIM.getType() != null)
			imAddresses.add(newIM);
				
		// Read 1 Note from the array
		notes = new ArrayList <String> ();
		if(values.length >= i + 1 && values[i] != null)
			notes.add(values[i]);
		i += 1;
		
		// Read the extra values at the end of the array
		String mark = null;
		while(values.length > i) {
			// Read the mark
			mark = values[i];
			i++;
			
			// Read the values that follow after the mark
			if(CSVphoneMark.equals(mark)) {
				sublen = Phone.getHeader().length;
				while(values.length >= i + sublen && isValid(values, i, sublen)) {
					newPhone = new Phone ();
					newPhone.readValues(Arrays.copyOfRange(values, i, i + sublen));
					phone.add(newPhone);
					i += sublen;
				}
			} else if(CSVemailMark.equals(mark)){
				sublen = Email.getHeader().length;
				while(values.length >= i + sublen && isValid(values, i, sublen)) {
					newEmail = new Email ();
					newEmail.readValues(Arrays.copyOfRange(values, i, i + sublen));
					email.add(newEmail);
					i += sublen;
				}
			} else if(CSVaddressMark.equals(mark)){
				sublen = Address.getHeader().length;
				while(values.length >= i + sublen && isValid(values, i, sublen)) {
					newAddress = new Address ();
					newAddress.readValues(Arrays.copyOfRange(values, i, i + sublen));
					addresses.add(newAddress);
					i += sublen;
				}
			} else if (CSVimMark.equals(mark)){
				sublen = IM.getHeader().length;
				while(values.length >= i + sublen && isValid(values, i, sublen)) {
					newIM = new IM ();
					newIM.readValues(Arrays.copyOfRange(values, i, i + sublen));
					imAddresses.add(newIM);
					i += sublen;
				}
			} else if (CSVnotesMark.equals(mark)){
				while(values.length >= i + 1 && isValid(values, i, 1)) {
					notes.add(values[i]);
					i++;
				}
			}
		}
	}
	
	public String [] getValues(){
		// Calculate the length of the values array
		int length = Contact.getHeaderLength ();
		
		// Increase the length by the variables for additional list values
		length += (phone != null && phone.size() > 1) ? 1 + ((phone.size() - 1) * Phone.getHeader().length) : 0;
		length += (email != null && email.size() > 1) ? 1 + ((email.size() - 1) * Email.getHeader().length) : 0;
		length += (addresses != null && addresses.size() > 1) ? 1 + ((addresses.size() - 1) * Address.getHeader().length) : 0;
		length += (imAddresses != null && imAddresses.size() > 1) ? 1 + ((imAddresses.size() - 1) * IM.getHeader().length) : 0;
		length += (notes != null && notes.size() > 1) ? 1 + ((notes.size() - 1) * 1) : 0;
		
		// Create an array to hold the CSV values
		String [] values = new String [length];
		
		// Keep track of our position in the array
		int i = 0;
		String [] subvalues = null;
		
		// Add the variables in this class to the array
		values[0] = id;
		values[1] = displayName;
		i += 2;
		
		// Add the variables of 1 organization to the array
		subvalues = (organization != null) ? organization.getValues() : null;
		addValues(values, subvalues, i, Organization.getHeader().length);
		i += Organization.getHeader().length;
		
		// Add the variables of 1 StructuredName to the array
		subvalues = (structuredName != null) ? structuredName.getValues() : null;
		addValues(values, subvalues, i, StructuredName.getHeader().length);
		i += StructuredName.getHeader().length;
		
		// Add the variables of 1 Account to the array
		subvalues = (account != null) ? account.getValues() : null;
		addValues(values, subvalues, i, AccountInfo.getHeader().length);
		i += AccountInfo.getHeader().length;
		
		// Add the variables of 1 Phone to the array
		subvalues = (phone != null && phone.size() > 0) ? phone.get(0).getValues() : null;
		addValues(values, subvalues, i, Phone.getHeader().length);
		i += Phone.getHeader().length;
		
		// Add the variables of 1 Email to the array
		subvalues = (email != null && email.size() > 0) ? email.get(0).getValues() : null;
		addValues(values, subvalues, i, Email.getHeader().length);
		i += Email.getHeader().length;
		
		// Add the variables of 1 Address to the array
		subvalues = (addresses != null && addresses.size() > 0) ? addresses.get(0).getValues() : null;
		addValues(values, subvalues, i, Address.getHeader().length);
		i += Address.getHeader().length;
		
		// Add the variables of 1 IM to the array
		subvalues = (imAddresses != null && imAddresses.size() > 0) ? imAddresses.get(0).getValues() : null;
		addValues(values, subvalues, i, IM.getHeader().length);
		i += IM.getHeader().length;
		
		// Add the variables of 1 Note to the array
		if(i + 1 <= values.length){
			values[i] = (notes != null && notes.size() > 0) ? notes.get(0) : null;
		}
		i += 1;
		
		// Add extra phone list values to the end of the array
		if(phone != null && phone.size() > 1 && i + 1 + Phone.getHeader().length <= values.length){
			// Mark the start of phone numbers
			values[i] = CSVphoneMark;
			i++;
			
			// Add the phone numbers
			for(int j = 1; j < phone.size(); j++){
				subvalues = (phone.get(j) != null) ? phone.get(j).getValues(): null;
				addValues(values, subvalues, i, Phone.getHeader().length);
				i += Phone.getHeader().length;
			}
		}
		
		// Add extra email list values to the end of the array
		if(email != null && email.size() > 1 && i + 1 + Email.getHeader().length <= values.length){
			// Mark the start of emails
			values[i] = CSVemailMark;
			i++;
			
			// Add the email numbers
			for(int j = 1; j < email.size(); j++){
				subvalues = (email.get(j) != null) ? email.get(j).getValues(): null;
				addValues(values, subvalues, i, Email.getHeader().length);
				i += Email.getHeader().length;
			}
		}

		// Add extra Addresses list values to the end of the array
		if(addresses != null && addresses.size() > 1 && i + 1 + Address.getHeader().length <= values.length){
			// Mark the start of Addresses 
			values[i] = CSVaddressMark;
			i++;
			
			// Add the Addresses numbers
			for(int j = 1; j < addresses.size(); j++){
				subvalues = (addresses.get(j) != null) ? addresses.get(j).getValues(): null;
				addValues(values, subvalues, i, Address.getHeader().length);
				i += Address.getHeader().length;
			}
		}

		// Add extra IM list values to the end of the array
		if(imAddresses != null && imAddresses.size() > 1 && i + 1 + IM.getHeader().length <= values.length){
			// Mark the start of IM addresses
			values[i] = CSVimMark;
			i++;
			
			// Add the IM Addresses
			for(int j = 1; j < imAddresses.size(); j++){
				subvalues = (imAddresses.get(j) != null) ? imAddresses.get(j).getValues(): null;
				addValues(values, subvalues, i, IM.getHeader().length);
				i += IM.getHeader().length;
			}
		}

		// Add extra note list values to the end of the array
		if(notes != null && notes.size() > 1 && i + 1 + (notes.size() - 1) <= values.length){
			// Mark the start of notes
			values[i] = CSVnotesMark;
			i++;
			
			// Add the notes
			for(int j = 1; j < notes.size(); j++){
				values[i] = notes.get(j);
			}
		}

		return values;
	}
	
	public static String [] getHeader(){
		int length = Contact.getHeaderLength ();
		String [] header = new String [length];
		int i = 0;
		
		// Add the header values of this class to the array
		header[0] = "id";
		header[1] = "displayName";
		i += 2;
		
		// Add the header of 1 organization to the array
		addValues(header, Organization.getHeader(), i, Organization.getHeader().length);
		i += Organization.getHeader().length;
		
		// Add the header of 1 StructuredName to the array
		addValues(header, StructuredName.getHeader(), i, StructuredName.getHeader().length);
		i += StructuredName.getHeader().length;
		
		// Add the header of 1 Account to the array
		addValues(header, AccountInfo.getHeader(), i, AccountInfo.getHeader().length);
		i += AccountInfo.getHeader().length;
		
		// Add the header of 1 Phone to the array
		addValues(header, Phone.getHeader(), i, Phone.getHeader().length);
		i += Phone.getHeader().length;
		
		// Add the header of 1 Email to the array
		addValues(header, Email.getHeader(), i, Email.getHeader().length);
		i += Email.getHeader().length;
		
		// Add the header of 1 Address to the array
		addValues(header, Address.getHeader(), i, Address.getHeader().length);
		i += Address.getHeader().length;
		
		// Add the header of 1 IM to the array
		addValues(header, IM.getHeader(), i, IM.getHeader().length);
		i += IM.getHeader().length;
		
		// Add the header of 1 Note to the array
		if(i + 1 <= header.length){ header[i] = "notes"; }
		i += 1;
		
		return header;
	}
	
	private static int getHeaderLength (){
		// Calculate the length of the values array
		int length = 2;									// number of variables in this class 
		length += Organization.getHeader().length;  	// at least 1 Organization
		length += StructuredName.getHeader().length;	// at least 1 StructuredName
		length += AccountInfo.getHeader().length;  		// at least 1 AccountInfo
		length += Phone.getHeader().length;  			// at least 1 Phone 
		length += Email.getHeader().length;  			// at least 1 Email 
		length += Address.getHeader().length;  			// at least 1 Address 
		length += IM.getHeader().length;  				// at least 1 IM 
		length += 1;  									// at least 1 note

		return length;
	}
	
	private static boolean isValid(String [] values, int start, int len){
		
		boolean valid = true;
		
		// Check for a mark. An entry becomes invalid if it contains a mark
		for(int j = start; j < start+len; j++ ){
			valid = valid && !(CSVphoneMark.equals(values[j]) || CSVemailMark.equals(values[j])
							|| CSVaddressMark.equals(values[j]) || CSVimMark.equals(values[j])
							|| CSVnotesMark.equals(values[j]));
		}

		return valid;
	}
	
	private static String [] addValues(String [] mainArray, String [] values, int start, int maxlen) {
		if(mainArray != null && values != null) {

			// Get the length of the values loop
			int length = values.length < maxlen ? values.length : maxlen;
			length = mainArray.length < start + length ? mainArray.length - start : length;
			
			// Add the values to the main array
			if(start < mainArray.length){
				for(int i = 0; i < length; i++){
					mainArray[start + i] = values[i];
				}
			}
		}
		return mainArray;
	}
}
