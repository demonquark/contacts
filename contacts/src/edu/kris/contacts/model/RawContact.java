package edu.kris.contacts.model;

import java.util.ArrayList;
import java.util.Arrays;

public class RawContact {
 	private long id 						= -1;		// Raw contact ID
 	private long contact_id					= -1;		// Contact ID identifies an aggregation of raw contacts
 	private int isPrimary					= 0;
 	private String displayName 				= null;
 	private ArrayList<Phone> phones			= new ArrayList<Phone>();
 	private ArrayList<Email> emails 		= new ArrayList<Email>();
 	private ArrayList<Note> notes 			= new ArrayList<Note>();
 	private ArrayList<StructuredPostal> addresses 	= new ArrayList<StructuredPostal>();
 	private ArrayList<SIPAddress> sips		= new ArrayList<SIPAddress> ();
 	private ArrayList<IM> imAddresses 		= new ArrayList<IM>();
 	private ArrayList<Event> events			= new ArrayList<Event>();
 	private ArrayList<Website> websites		= new ArrayList<Website>();
 	private Organization organization 		= new Organization();
 	private StructuredName structuredName 	= new StructuredName ();
 	private AccountInfo account 			= new AccountInfo ();
 	private NickName nickName				= new NickName();
 	private Relation relation				= new Relation();
 	
 	public RawContact(){}
 	public int getIsPrimary() {
		return isPrimary;
	}
	public void setIsPrimary(int isPrimary) {
		this.isPrimary = isPrimary;
	}
	public RawContact(long rawID, long contactID){
 		this.id = rawID; 
 		this.contact_id = contactID;
 	}
 	public RawContact(String [] csvValues) {
 		readValues(csvValues, true);
 	}
 	
 	public long getContact_id() {
		return contact_id;
	}
	public void setContact_id(long contact_id) {
		this.contact_id = contact_id;
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
 		if(this.imAddresses == null){
 			this.imAddresses = new ArrayList<IM> ();
 		}
 		if(imAddr != null) {
 			this.imAddresses.add(imAddr);
 		}
 	}
 	public ArrayList<Note> getNotes() {
 		return notes;
 	}
 	public void setNotes(ArrayList<Note> notes) {
 		this.notes = notes;
 	}
 	public void addNote(Note note) {
 		if(this.notes == null){
 			this.notes = new ArrayList<Note> ();
 		}
 		if(note != null) {
 			this.notes.add(note);
 		}
 	}
 	public ArrayList<SIPAddress> getSips() {
		return sips;
	}
	public void setSips(ArrayList<SIPAddress> sips) {
		this.sips = sips;
	}
 	public void addSIPAddress(SIPAddress sip) {
 		if(this.sips == null){
 			this.sips = new ArrayList<SIPAddress> ();
 		}
 		if(sip != null) {
 			this.sips.add(sip);
 		}
 	}
	public ArrayList<Event> getEvents() {
		return events;
	}
	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}
 	public void addEvents(Event event) {
 		if(this.events == null){
 			this.events = new ArrayList<Event> ();
 		}
 		if(event != null) {
 			this.events.add(event);
 		}
 	}
	public NickName getNickName() {
		return nickName;
	}
	public void setNickName(NickName nickName) {
		this.nickName = nickName;
	}
	public ArrayList<Website> getWebsites() {
		return websites;
	}
	public void setWebsite(ArrayList<Website> websites) {
		this.websites = websites;
	}
	public void addWebsite(Website website){
 		if(this.websites == null){
 			this.websites = new ArrayList<Website> ();
 		}
 		if(website != null) {
 			this.websites.add(website);
 		}
	}
	public Relation getRelation() {
		return relation;
	}
	public void setRelation(Relation relation) {
		this.relation = relation;
	}
	public ArrayList<StructuredPostal> getAddresses() {
 		return addresses;
 	}
 	public void setAddresses(ArrayList<StructuredPostal> addresses) {
 		this.addresses = addresses;
 	}
 	public void addAddress(StructuredPostal address) {
 		if(this.addresses == null){
 			this.addresses = new ArrayList<StructuredPostal> ();
 		}
 		if(address != null) {
 			this.addresses.add(address);
 		}
 	}
 	public ArrayList<Email> getEmail() {
 		return emails;
 	}
 	public void setEmail(ArrayList<Email> email) {
 		this.emails = email;
 	}
 	public void addEmail(Email e) {
 		if(this.emails == null){
 			this.emails = new ArrayList<Email> ();
 		}
 		if(e != null) {
 			this.emails.add(e);
 		}
 	}	
 	public long getId() {
 		return id;
 	}
 	public void setId(long id) {
  		this.id = id;
 	}
 	public String getDisplayName() {
 		return displayName;
 	}
 	public void setDisplayName(String dName) {
 		this.displayName = dName;
 	}
 	public ArrayList<Phone> getPhone() {
 		return phones;
 	}
 	public void setPhone(ArrayList<Phone> phone) {
 		this.phones = phone;
 	}
 	public void addPhone(Phone phone) {
 		if(this.phones == null){
 			this.phones = new ArrayList<Phone> ();
 		}
 		if(phone != null) {
 			this.phones.add(phone);
 		}
 	}
	public StructuredName getStructuredName() {
		return structuredName;
	}
	public void setStructuredName(StructuredName structuredName) {
		this.structuredName = structuredName;
	}

	public void readValues(String [] values, boolean overwrite){
		
		int i = 0;
		if(overwrite){
			// Create new objects
			account = new AccountInfo();
			emails = new ArrayList <Email> ();
			events = new ArrayList <Event> ();
			imAddresses = new ArrayList <IM> ();
			nickName = new NickName();
			notes = new ArrayList <Note> ();
			organization = new Organization();
			phones = new ArrayList <Phone> ();
			relation = new Relation();
			sips = new ArrayList<SIPAddress> ();
			structuredName = new StructuredName();
			addresses = new ArrayList <StructuredPostal> ();
			websites = new ArrayList <Website> ();

			// Read the class values from the array
			id 			= values.length < 1 ? -1 : Long.valueOf(values[0]);
			contact_id 	= values.length < 2 ? -1 : Long.valueOf(values[1]);
			displayName = values.length < 3 ? "" : values[2]; 
		}
		isPrimary 	= values.length < 4 ? -1 : Integer.valueOf(values[3]);
		i += 4;
		
		// Read 1 Account from the array
		if(overwrite && values.length >= i + AccountInfo.getHeader().length)
			account.readValues(Arrays.copyOfRange(values, i, i + AccountInfo.getHeader().length));
		i += AccountInfo.getHeader().length;

		// Read 1 Email from the array
		Email newEmail = new Email ();
		if(values.length >= i + Email.getHeader().length)
			newEmail.readValues(Arrays.copyOfRange(values, i, i + Email.getHeader().length));
		if(newEmail.getType() >= 0)
			emails.add(newEmail);
		i += Email.getHeader().length;

		// Read 1 Event from the array
		Event newEvent = new Event ();
		if(values.length >= i + Event.getHeader().length)
			newEvent.readValues(Arrays.copyOfRange(values, i, i + Event.getHeader().length));
		if(newEvent.getType() >= 0)
			events.add(newEvent);
		i += Event.getHeader().length;

		// Read 1 IM from the array
		IM newIM = new IM ();
		if(values.length >= i + IM.getHeader().length)
			newIM.readValues(Arrays.copyOfRange(values, i, i + IM.getHeader().length));
		if(newIM.getType() >= 0)
			imAddresses.add(newIM);
		i += IM.getHeader().length;
				
		// Read 1 nickname from the array
		if(overwrite && values.length >= i + NickName.getHeader().length)
			nickName.readValues(Arrays.copyOfRange(values, i, i + NickName.getHeader().length));
		i += NickName.getHeader().length;
		
		// Read 1 Note from the array
		Note newNote = new Note ();
		if(values.length >= i + Note.getHeader().length)
			newNote.readValues(Arrays.copyOfRange(values, i, i + Note.getHeader().length));
		if(newNote != null && newNote.getNote() != null && !"".equals(newNote.getNote()))
			notes.add(newNote);
		i += Note.getHeader().length;

		// Read 1 organization from the array
		if(overwrite && values.length >= i + Organization.getHeader().length)
			organization.readValues(Arrays.copyOfRange(values, i, i + Organization.getHeader().length));
		i += Organization.getHeader().length;
		
		// Read 1 Phone from the array
		Phone newPhone = new Phone ();
		if(values.length >= i + Phone.getHeader().length)
			newPhone.readValues(Arrays.copyOfRange(values, i, i + Phone.getHeader().length));
		if(newPhone.getType() >= 0)
			phones.add(newPhone);
		i += Phone.getHeader().length;
		
		// Read 1 organization from the array
		if(overwrite && values.length >= i + Relation.getHeader().length)
			relation.readValues(Arrays.copyOfRange(values, i, i + Relation.getHeader().length));
		i += Relation.getHeader().length;
		
		// Read 1 SIP address from the array
		SIPAddress sip = new SIPAddress ();
		if(values.length >= i + SIPAddress.getHeader().length)
			sip.readValues(Arrays.copyOfRange(values, i, i + SIPAddress.getHeader().length));
		if(sip.getType() >= 0)
			sips.add(sip);
		i += SIPAddress.getHeader().length;
		
		// Read 1 StructuredName from the array
		if(overwrite && values.length >= i + StructuredName.getHeader().length)
			structuredName.readValues(Arrays.copyOfRange(values, i, i + StructuredName.getHeader().length));
		i += StructuredName.getHeader().length;

		// Read 1 Address from the array
		StructuredPostal newAddress = new StructuredPostal ();
		if(values.length >= i + StructuredPostal.getHeader().length)
			newAddress.readValues(Arrays.copyOfRange(values, i, i + StructuredPostal.getHeader().length));
		if(newAddress.getType() >= 0)
			addresses.add(newAddress);
		i += StructuredPostal.getHeader().length;
		
		// Read 1 Web site from the array
		Website site = new Website ();
		if(values.length >= i + Website.getHeader().length)
			site.readValues(Arrays.copyOfRange(values, i, i + Website.getHeader().length));
		if(site.getType() >= 0)
			websites.add(site);
		i += SIPAddress.getHeader().length;
		
	}
	
	/**
	 * Get the values from the raw contact in a string array. 
	 * The zeroth index returns all the raw contact info (ID, contactID, displayname, etc)
	 * Further indexes only returns the values at the index  
	 * 
	 * @param index - start at zero
	 * @return String array
	 */
	public String [] getValues(int index){

		// Create an array to hold the CSV values
		String [] values = new String [RawContact.getHeaderLength()];
		
		// Keep track of our position in the array
		int i = 0;
		int p = index > 0 ? index : 0;
		String [] subvalues = null;
		
		// Add the variables in this class to the array
		if(p == 0){
			values[0] = String.valueOf(id);
			values[1] = String.valueOf(contact_id);
			values[2] = displayName;
		}
		values[3] = String.valueOf(isPrimary);
		i += 4;
		
		// Add the variables of 1 Account to the array
		subvalues = (p == 0 && account != null) ? account.getValues() : null;
		addValues(values, subvalues, i, AccountInfo.getHeader().length);
		i += AccountInfo.getHeader().length;
		
		// Add the variables of 1 Email to the array
		subvalues = (emails != null && emails.size() > p) ? emails.get(p).getValues() : null;
		addValues(values, subvalues, i, Email.getHeader().length);
		i += Email.getHeader().length;
		
		// Add the variables of 1 Event to the array
		subvalues = (events != null && events.size() > p) ? events.get(p).getValues() : null;
		addValues(values, subvalues, i, Event.getHeader().length);
		i += Event.getHeader().length;
		
		// Add the variables of 1 IM to the array
		subvalues = (imAddresses != null && imAddresses.size() > p) ? imAddresses.get(p).getValues() : null;
		addValues(values, subvalues, i, IM.getHeader().length);
		i += IM.getHeader().length;
		
		// Add the variables of 1 organization to the array
		subvalues = (p == 0 && nickName != null) ? nickName.getValues() : null;
		addValues(values, subvalues, i, NickName.getHeader().length);
		i += NickName.getHeader().length;
		
		// Add the variables of 1 Note to the array
		subvalues = (notes != null && notes.size() > p) ? notes.get(p).getValues() : null;
		addValues(values, subvalues, i, Note.getHeader().length);
		i += Note.getHeader().length;
		
		// Add the variables of 1 organization to the array
		subvalues = (p == 0 && organization != null) ? organization.getValues() : null;
		addValues(values, subvalues, i, Organization.getHeader().length);
		i += Organization.getHeader().length;
		
		// Add the variables of 1 Phone to the array
		subvalues = (phones != null && phones.size() > p) ? phones.get(p).getValues() : null;
		addValues(values, subvalues, i, Phone.getHeader().length);
		i += Phone.getHeader().length;
		
		// Add the variables of 1 organization to the array
		subvalues = (p == 0 && relation != null) ? relation.getValues() : null;
		addValues(values, subvalues, i, Relation.getHeader().length);
		i += Relation.getHeader().length;
		
		// Add the variables of 1 SIP address to the array
		subvalues = (sips != null && sips.size() > p) ? sips.get(p).getValues() : null;
		addValues(values, subvalues, i, SIPAddress.getHeader().length);
		i += SIPAddress.getHeader().length;
		
		// Add the variables of 1 StructuredName to the array
		subvalues = (p == 0 && structuredName != null) ? structuredName.getValues() : null;
		addValues(values, subvalues, i, StructuredName.getHeader().length);
		i += StructuredName.getHeader().length;
		
		// Add the variables of 1 Address to the array
		subvalues = (addresses != null && addresses.size() > p) ? addresses.get(p).getValues() : null;
		addValues(values, subvalues, i, StructuredPostal.getHeader().length);
		i += StructuredPostal.getHeader().length;
		
		// Add the variables of 1 Address to the array
		subvalues = (websites != null && websites.size() > p) ? websites.get(p).getValues() : null;
		addValues(values, subvalues, i, Website.getHeader().length);
		i += Website.getHeader().length;
		
		return values;
	}
	
	public static String [] getHeader(){
		int length = RawContact.getHeaderLength ();
		String [] header = new String [length];
		int i = 0;
		
		// Add the header values of this class to the array
		header[0] = "id";
		header[1] = "contactId";
		header[2] = "displayName";
		header[3] = "isPrimary";
		i += 4;
		
		// Add the header of 1 Account to the array
		addValues(header, AccountInfo.getHeader(), i, AccountInfo.getHeader().length);
		i += AccountInfo.getHeader().length;
		
		// Add the header of 1 Email to the array
		addValues(header, Email.getHeader(), i, Email.getHeader().length);
		i += Email.getHeader().length;
		
		// Add the header of 1 Email to the array
		addValues(header, Event.getHeader(), i, Event.getHeader().length);
		i += Event.getHeader().length;
		
		// Add the header of 1 IM to the array
		addValues(header, IM.getHeader(), i, IM.getHeader().length);
		i += IM.getHeader().length;
		
		// Add the header of 1 NickName to the array
		addValues(header, NickName.getHeader(), i, NickName.getHeader().length);
		i += NickName.getHeader().length;
		
		// Add the header of 1 Note to the array
		addValues(header, Note.getHeader(), i, Note.getHeader().length);
		i += Note.getHeader().length;
		
		// Add the header of 1 organization to the array
		addValues(header, Organization.getHeader(), i, Organization.getHeader().length);
		i += Organization.getHeader().length;
		
		// Add the header of 1 Phone to the array
		addValues(header, Phone.getHeader(), i, Phone.getHeader().length);
		i += Phone.getHeader().length;
		
		// Add the header of 1 Phone to the array
		addValues(header, Relation.getHeader(), i, Relation.getHeader().length);
		i += Relation.getHeader().length;
		
		// Add the header of 1 StructuredName to the array
		addValues(header, SIPAddress.getHeader(), i, SIPAddress.getHeader().length);
		i += SIPAddress.getHeader().length;

		// Add the header of 1 StructuredName to the array
		addValues(header, StructuredName.getHeader(), i, StructuredName.getHeader().length);
		i += StructuredName.getHeader().length;
		
		// Add the header of 1 Address to the array
		addValues(header, StructuredPostal.getHeader(), i, StructuredPostal.getHeader().length);
		i += StructuredPostal.getHeader().length;
		
		return header;
	}
	
	public int maxListSize(){
		int size = 0;
		size = emails.size() > size ? emails.size() : size;
		size = events.size() > size ? events.size() : size;
		size = imAddresses.size() > size ? imAddresses.size() : size;
		size = notes.size() > size ? notes.size() : size;
		size = phones.size() > size ? phones.size() : size;
		size = sips.size() > size ? sips.size() : size;
		size = addresses.size() > size ? addresses.size() : size;
		size = websites.size() > size ? websites.size() : size;
		
		return size > 0 ? size : 1;
	}
	
	private static int getHeaderLength (){
		// Calculate the length of the values array
		int length = 4;									// number of variables in this class 
		length += AccountInfo.getHeader().length;  		// at least 1 AccountInfo
		length += Email.getHeader().length;  			// at least 1 Email 
		length += Event.getHeader().length;				// at least 1 event
		length += IM.getHeader().length;  				// at least 1 IM 
		length += NickName.getHeader().length;  		// at least 1 Nickname
		length += Note.getHeader().length;				// at least 1 note
		length += Organization.getHeader().length;  	// at least 1 Organization
		length += Phone.getHeader().length;  			// at least 1 Phone 
		length += Relation.getHeader().length;			// at least 1 Relation
		length += SIPAddress.getHeader().length;		// at least 1 SIP Address
		length += StructuredName.getHeader().length;	// at least 1 StructuredName
		length += StructuredPostal.getHeader().length;  // at least 1 Address 
		length += Website.getHeader().length;			// at least 1 Website

		return length;
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
