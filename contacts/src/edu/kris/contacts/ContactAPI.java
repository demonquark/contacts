package edu.kris.contacts;

import java.util.ArrayList;

import edu.kris.contacts.model.*;
import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

public class ContactAPI {
	
	private static final String TAG = "ContactAPI"; 
	
	private static ContactAPI api;
 	private ContentResolver cr;
 	
 	public static ContactAPI getInstance(Context context) {
 		if (api == null) {
 			try {
 				api = new ContactAPI (context);
 			} catch (Exception e) {
 				throw new IllegalStateException(e);
 			}
 		}
 		return api;
 	}
 	
 	private ContactAPI(Context context){
 		this.cr = context.getContentResolver();
 	}
 	
 	public ArrayList <RawContact> getContacts(){
 		return getContacts(null, -1);
 	}
 	
 	public ArrayList <RawContact> getContacts(MainActivity.ContactTask task, int max) {
 		
 		// First get the contact data
 		int i = 0;
 		long orgID = -1;
 		long dbsID = -1;
 		ArrayList<RawContact> contacts = getContactData(task,max);
 		ArrayList<RawContactInfo> infos = getRawContactInfo(null);
 		int size = contacts.size();
 		
 		for(RawContactInfo info : infos) {
   			// Get the raw contact ID
   			dbsID = info.id;
   			
   			// use the fact that the contacts list is ordered raw contact ID
   			while (dbsID > orgID && i < size) {
  	   			orgID = contacts.get(i).getId();
  	   			i++;
   			}
   			
   			// add the new data
   			if(dbsID == orgID) {
   				// There's already a raw contact with that ID, add the account info to that contact
   				contacts.get(i > 0 ? i - 1 : 0).setAccount(new AccountInfo(new String [] {info.name,info.type}));
   			} else if(!info.deleted) {
   				// There is no raw contact with that ID, create a new contact and add the account info
   				RawContact rawContact = new RawContact (info.id, info.contactID);
   				rawContact.setAccount(new AccountInfo(new String [] {info.name,info.type}));
   				
   				if(i >= size) {
  	   				contacts.add(i > 0 ? i - 1 : 0, rawContact);
   				} else {
  	   				contacts.add(rawContact);
   				}
   			}
 		}
 		
 		return contacts;
 	}
 	
 	public ArrayList <RawContact> getContactData(MainActivity.ContactTask task, int max) {
 		
 		int i = 0;
 		int a = 0;
 	    long rawID = -1;
 	    long contactID = -1;
 	    RawContact contact = null;
 	    String mimeType = null;
 		ArrayList<RawContact> contacts = new ArrayList<RawContact>();

 	    String[] projection = new String [] {
 	    		ContactsContract.Data._ID,
 	    		ContactsContract.Data.RAW_CONTACT_ID,
 	    		ContactsContract.RawContacts.CONTACT_ID,
 	    		ContactsContract.Contacts.DISPLAY_NAME,
 	    		ContactsContract.Data.MIMETYPE,
 	    		ContactsContract.Data.IS_PRIMARY,
 	    		ContactsContract.Data.DATA1,
 	    		ContactsContract.Data.DATA2,
 	    		ContactsContract.Data.DATA3,
 	    		ContactsContract.Data.DATA4,
 	    		ContactsContract.Data.DATA5,
 	    		ContactsContract.Data.DATA6,
 	    		ContactsContract.Data.DATA7,
 	    		ContactsContract.Data.DATA8,
 	    		ContactsContract.Data.DATA9,
 	    		ContactsContract.Data.DATA10,
 	    		ContactsContract.Data.DATA11,
 	    		ContactsContract.Data.DATA12,
 	    		ContactsContract.Data.DATA13,
 	    		ContactsContract.Data.DATA14,
 	    		ContactsContract.Data.DATA15
 	    };
 	    
 		String sortOder = ContactsContract.Data.RAW_CONTACT_ID + " ASC"; 
 	    Cursor cur= cr.query(ContactsContract.Data.CONTENT_URI, projection, null, null, sortOder);
 	    
 	   if (cur.moveToFirst()) {

 		   // set the maximum number of records
 		   if(max < 0) { max = cur.getCount(); }
 		   
 		   while (cur.moveToNext()) {

 			   // Increase the counter
 			   i++;
 			   
 			   // Check if we have reached a new raw contact
 			   if(rawID != cur.getLong(cur.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID))){
 				   
 	 			   // publish progress
 	 			   if(task != null) {
 	 	 			   task.updateProgress((i*100) / max);
 	 			   }
 				   
 				   // Create a new contact
 				   rawID = cur.getLong(cur.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
 				   contactID = cur.getLong(cur.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));
 				   contact = new RawContact(rawID, contactID);
 				   contact.setDisplayName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
 				   contact.setIsPrimary(cur.getInt(cur.getColumnIndex(ContactsContract.Data.IS_PRIMARY)));
				   contacts.add(contact);
				   a++;
 			   }
 			   
 			   // Determine what to do with the data based on the MIME
 			   mimeType = cur.getString(cur.getColumnIndex(ContactsContract.Data.MIMETYPE)); 

 			   if(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(mimeType)){
 				   contact.setStructuredName(new StructuredName(new String [] {
 						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA2)),
 						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA3)),
 						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA4)),
 						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA5)),
 						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA6)),
 						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA7)),
 						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA8)),
 						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA9))
 				   }));
 			   }else if(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(mimeType)){
 				   contact.addPhone(new Phone(new String [] {
 						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1)),
 	 					cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA2)),
 	 					cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA3))
 				   }));
 			   }else if(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(mimeType)){
  				   contact.addEmail(new Email(new String [] {
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA2)),
  	 	 				cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA3))
  				   }));
 			   }else if(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE.equals(mimeType)){
  				   contact.setOrganization(new Organization(new String [] {
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA2)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA3)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA4)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA5)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA6)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA7)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA8)),
  	 	 				cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA9))
  				   }));
 			   }else if(ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE.equals(mimeType)){
  				   contact.addImAddresses(new IM(new String [] {
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA2)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA3)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA5)),
  	 	 				cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA6))
  				   }));
 			   }else if(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE.equals(mimeType)){
  				   contact.addEmail(new Email(new String [] {
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA2)),
  	 	 				cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA3))
  				   }));
 			   }else if(ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE.equals(mimeType)){
  				   contact.addNote(new Note(new String [] {
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1))
  				   }));
 			   }else if(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE.equals(mimeType)){
  				   contact.addAddress(new StructuredPostal(new String [] {
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA2)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA3)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA4)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA5)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA6)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA7)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA8)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA9)),
  	 	 				cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA10))
  				   }));
 			   }else if(ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE.equals(mimeType)){
  				   contact.addWebsite(new Website(new String [] {
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA2)),
  	 	 				cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA3))
  				   }));
 			   }else if(ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE.equals(mimeType)){
  				   contact.addEvents(new Event(new String [] {
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA2)),
  	 	 				cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA3))
  				   }));
 			   }else if(ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE.equals(mimeType)){
  				   contact.setRelation(new Relation(new String [] {
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA2)),
  	 	 				cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA3))
  				   }));
 			   }else if(ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE.equals(mimeType)){
  				   contact.addSIPAddress(new SIPAddress(new String [] {
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1)),
  						cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA2)),
  	 	 				cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA3))
  				   }));
 			   }
 		   }
 		}
 	   
		if(cur != null) {
			cur.close();
		}
		Log.d(TAG, "There are " + a + " records and " + contacts.size() + " contacts. max=" + max);
 	   
 	   	return contacts;
 	}
 	
 	public boolean insertContacts(ArrayList <RawContact> people, MainActivity.ContactTask task) {

 		boolean success = false;
		
 		// remove duplicate contacts
		long now = System.currentTimeMillis();
 		people = removeContacts(people);
 		Log.i(TAG, "REMOVE PEOPLE " + (System.currentTimeMillis() - now) + " milliseconds.");

 		// Add the Raw contacts
		now = System.currentTimeMillis();
 		int peopleSize = people.size();
 		ArrayList<Long> contactIds = insertEmptyRawContacts(peopleSize);
 		int rawContactsSize = contactIds.size();
 		
 		// Bulk insert the data  
 		long contactID = -1;
 		RawContact contact;
 		ContentValues content;
 		ArrayList <ContentValues> values = new ArrayList <ContentValues> ();
 		Log.v(TAG, "Will insert data for " + rawContactsSize + "/" + peopleSize + " contacts.");
 		for(int i = 0; i < peopleSize && i < rawContactsSize; i++) {
 			contact = people.get(i);
 			contactID = contactIds.get(i);
 			
 			// Add email addresses
 			for(int j = 0; j < contact.getEmail().size(); j++){
 				if(contact.getEmail().get(j).getType() > -1){
 	 	 			content = new ContentValues();
 	 	 			content.put(ContactsContract.Data.RAW_CONTACT_ID, contactID);
 	 	 			content.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
 	 	 			content.put(ContactsContract.CommonDataKinds.Email.ADDRESS, 	contact.getEmail().get(j).getAddress());
 	 	 			content.put(ContactsContract.CommonDataKinds.Email.TYPE, 		contact.getEmail().get(j).getType());
 	 	 			if(contact.getEmail().get(j).getLabel() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.Email.LABEL, 	contact.getEmail().get(j).getLabel());	
 	 	 			}
 	 	 			values.add(content);
 				}
 			}
 	 		Log.v(TAG, contactID +  ") " + values.size()  + " email added.");
 			
 			// Add events
 			for(int j = 0; j < contact.getEvents().size(); j++){
 				if(contact.getEvents().get(j).getType() > -1){
 	 	 			content = new ContentValues();
 	 	 			content.put(ContactsContract.Data.RAW_CONTACT_ID, contactID);
 	 	 			content.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
 	 	 			content.put(ContactsContract.CommonDataKinds.Event.START_DATE, 	contact.getEvents().get(j).getDate());
 	 	 			content.put(ContactsContract.CommonDataKinds.Event.TYPE, 		contact.getEvents().get(j).getType());
 	 	 			if(contact.getEvents().get(j).getLabel() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.Event.LABEL, 	contact.getEvents().get(j).getLabel());	
 	 	 			}
 	 	 			values.add(content);
 				}
 			}
 	 		Log.v(TAG, contactID +  ") " + values.size()  + " Event added.");
 			
 			// Add IM addresses
 			for(int j = 0; j < contact.getImAddresses().size(); j++){
 				if(contact.getImAddresses().get(j).getType() > -1){
 	 	 			content = new ContentValues();
 	 	 			content.put(ContactsContract.Data.RAW_CONTACT_ID, contactID);
 	 	 			content.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE);
 	 	 			content.put(ContactsContract.CommonDataKinds.Im.DATA, 		contact.getImAddresses().get(j).getData());
 	 	 			content.put(ContactsContract.CommonDataKinds.Im.TYPE, 		contact.getImAddresses().get(j).getType());
 	 	 			if(contact.getImAddresses().get(j).getLabel() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.Im.LABEL, 	
 	 	 						contact.getImAddresses().get(j).getLabel());	
 	 	 			}
 	 	 			if(contact.getImAddresses().get(j).getProtocol() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.Im.PROTOCOL,
 	 	 						contact.getImAddresses().get(j).getProtocol());	
 	 	 			}
 	 	 			if(contact.getImAddresses().get(j).getCustomProtocol() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL, 
 	 	 						contact.getImAddresses().get(j).getCustomProtocol());	
 	 	 			}
 	 	 			values.add(content);
 				}
 			}
 	 		Log.v(TAG, contactID +  ") " + values.size()  + " Im added.");

 			// Add NickName
			if(contact.getNickName() != null && contact.getNickName().getType() > -1){
 	 			content = new ContentValues();
 	 			content.put(ContactsContract.Data.RAW_CONTACT_ID, contactID);
 	 			content.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
 	 			content.put(ContactsContract.CommonDataKinds.Nickname.NAME, 		contact.getNickName().getName());
 	 			content.put(ContactsContract.CommonDataKinds.Nickname.TYPE, 		contact.getNickName().getType());
 	 			if(contact.getNickName().getLabel() != null){
 	 				content.put(ContactsContract.CommonDataKinds.Nickname.LABEL, 	contact.getNickName().getLabel());	
 	 			}
 	 			values.add(content);
			}
 	 		Log.v(TAG, contactID +  ") " + values.size()  + " Nickname added.");
			
 			// Add notes
 			for(int j = 0; j < contact.getNotes().size(); j++){
 				if(contact.getNotes().get(j).getNote() != null){
 	 	 			content = new ContentValues();
 	 	 			content.put(ContactsContract.Data.RAW_CONTACT_ID, contactID);
 	 	 			content.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);
 	 	 			content.put(ContactsContract.CommonDataKinds.Note.NOTE, 	contact.getNotes().get(j).getNote());
 	 	 			values.add(content);
 				}
 			}
 	 		Log.v(TAG, contactID +  ") " + values.size()  + " Note added.");
 			
 			// Add Organization
			if(contact.getOrganization() != null && contact.getOrganization().getType() > -1){
 	 			content = new ContentValues();
 	 			content.put(ContactsContract.Data.RAW_CONTACT_ID, contactID);
 	 			content.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE);
 	 			content.put(ContactsContract.CommonDataKinds.Organization.COMPANY, 		contact.getOrganization().getCompany());
 	 			content.put(ContactsContract.CommonDataKinds.Organization.TYPE, 		contact.getOrganization().getType());
 	 			if(contact.getOrganization().getLabel() != null){
 	 				content.put(ContactsContract.CommonDataKinds.Organization.LABEL, 	
 	 						contact.getOrganization().getLabel());	
 	 			}
 	 			if(contact.getOrganization().getTitle() != null){
 	 				content.put(ContactsContract.CommonDataKinds.Organization.TITLE, 	
 	 						contact.getOrganization().getTitle());	
 	 			}
 	 			if(contact.getOrganization().getDepartment() != null){
 	 				content.put(ContactsContract.CommonDataKinds.Organization.DEPARTMENT, 	
 	 						contact.getOrganization().getDepartment());	
 	 			}
 	 			if(contact.getOrganization().getJobDescription() != null){
 	 				content.put(ContactsContract.CommonDataKinds.Organization.JOB_DESCRIPTION, 	
 	 						contact.getOrganization().getJobDescription());	
 	 			}
 	 			if(contact.getOrganization().getSymbol() != null){
 	 				content.put(ContactsContract.CommonDataKinds.Organization.SYMBOL, 	
 	 						contact.getOrganization().getSymbol());	
 	 			}
 	 			if(contact.getOrganization().getPhoneticName() != null){
 	 				content.put(ContactsContract.CommonDataKinds.Organization.PHONETIC_NAME, 	
 	 						contact.getOrganization().getPhoneticName());	
 	 			}
 	 			if(contact.getOrganization().getOfficeLocation() != null){
 	 				content.put(ContactsContract.CommonDataKinds.Organization.OFFICE_LOCATION, 	
 	 						contact.getOrganization().getOfficeLocation());	
 	 			}
 	 			values.add(content);
			}
 	 		Log.v(TAG, contactID +  ") " + values.size()  + " Organization added.");
			
 			// Add phones
 			for(int j = 0; j < contact.getPhone().size(); j++){
 				if(contact.getPhone().get(j).getType() > -1){
 	 	 			content = new ContentValues();
 	 	 			content.put(ContactsContract.Data.RAW_CONTACT_ID, contactID);
 	 	 			content.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
 	 	 			content.put(ContactsContract.CommonDataKinds.Phone.NUMBER, 		contact.getPhone().get(j).getNumber());
 	 	 			content.put(ContactsContract.CommonDataKinds.Phone.TYPE, 		contact.getPhone().get(j).getType());
 	 	 			if(contact.getPhone().get(j).getLabel() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.Phone.LABEL, 	contact.getPhone().get(j).getLabel());	
 	 	 			}
 	 	 			values.add(content);
 				}
 			}
 	 		Log.v(TAG, contactID +  ") " + values.size()  + " Phone added.");
 			
 			// Add relation
			if(contact.getRelation() != null && contact.getRelation().getType() > -1){
 	 			content = new ContentValues();
 	 			content.put(ContactsContract.Data.RAW_CONTACT_ID, contactID);
 	 			content.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE);
 	 			content.put(ContactsContract.CommonDataKinds.Relation.NAME, 		contact.getRelation().getName());
 	 			content.put(ContactsContract.CommonDataKinds.Relation.TYPE, 		contact.getRelation().getType());
 	 			if(contact.getRelation().getLabel() != null){
 	 				content.put(ContactsContract.CommonDataKinds.Relation.LABEL, 	contact.getRelation().getLabel());	
 	 			}
 	 			values.add(content);
			}
 	 		Log.v(TAG, contactID +  ") " + values.size()  + " Relation added.");
			
 			// Add SIP addresses
 			for(int j = 0; j < contact.getSips().size(); j++){
 				if(contact.getSips().get(j).getType() > -1){
 	 	 			content = new ContentValues();
 	 	 			content.put(ContactsContract.Data.RAW_CONTACT_ID, contactID);
 	 	 			content.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE);
 	 	 			content.put(ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS, contact.getSips().get(j).getData());
 	 	 			content.put(ContactsContract.CommonDataKinds.SipAddress.TYPE, 		contact.getSips().get(j).getType());
 	 	 			if(contact.getSips().get(j).getLabel() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.SipAddress.LABEL, 	contact.getSips().get(j).getLabel());	
 	 	 			}
 	 	 			values.add(content);
 				}
 			}
 	 		Log.v(TAG, contactID +  ") " + values.size()  + " SipAddress added.");
			
 			// Add Structured Name
			if(contact.getStructuredName() != null){
 	 			content = new ContentValues();
 	 			content.put(ContactsContract.Data.RAW_CONTACT_ID, contactID);
 	 			content.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
 	 			if(contact.getStructuredName().getGivenName() != null){
 	 				content.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, 	
 	 						contact.getStructuredName().getGivenName());	
 	 			}
 	 			if(contact.getStructuredName().getGivenName() != null){
 	 				content.put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, 	
 	 						contact.getStructuredName().getFamilyName());	
 	 			}
 	 			if(contact.getStructuredName().getGivenName() != null){
 	 				content.put(ContactsContract.CommonDataKinds.StructuredName.PREFIX, 	
 	 						contact.getStructuredName().getPrefix());	
 	 			}
 	 			if(contact.getStructuredName().getGivenName() != null){
 	 				content.put(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, 	
 	 						contact.getStructuredName().getMiddleName());	
 	 			}
 	 			if(contact.getStructuredName().getGivenName() != null){
 	 				content.put(ContactsContract.CommonDataKinds.StructuredName.SUFFIX, 	
 	 						contact.getStructuredName().getSuffix());	
 	 			}
 	 			if(contact.getStructuredName().getGivenName() != null){
 	 				content.put(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME, 	
 	 						contact.getStructuredName().getPhoneticGivenName());	
 	 			}
 	 			if(contact.getStructuredName().getGivenName() != null){
 	 				content.put(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME, 	
 	 						contact.getStructuredName().getMiddleName());
 	 			}
 	 			if(contact.getStructuredName().getGivenName() != null){
 	 				content.put(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME, 	
 	 						contact.getStructuredName().getFamilyName());	
 	 			}
 	 			values.add(content);
			}
 	 		Log.v(TAG, contactID +  ") " + values.size()  + " StructuredName added.");
 			
 			// Add postal addresses
 			for(int j = 0; j < contact.getAddresses().size(); j++){
 				if(contact.getAddresses().get(j).getType() > -1){
 	 	 			content = new ContentValues();
 	 	 			content.put(ContactsContract.Data.RAW_CONTACT_ID, contactID);
 	 	 			content.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
 	 	 			content.put(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, 
 	 	 					contact.getAddresses().get(j).toString());
 	 	 			content.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, 		
 	 	 					contact.getAddresses().get(j).getType());
 	 	 			if(contact.getAddresses().get(j).getLabel() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.StructuredPostal.LABEL, 	
 	 	 						contact.getAddresses().get(j).getLabel());	
 	 	 			}
 	 	 			if(contact.getAddresses().get(j).getStreet() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.StructuredPostal.STREET, 	
 	 	 						contact.getAddresses().get(j).getStreet());	
 	 	 			}
 	 	 			if(contact.getAddresses().get(j).getPobox() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.StructuredPostal.POBOX, 	
 	 	 						contact.getAddresses().get(j).getPobox());	
 	 	 			}
 	 	 			if(contact.getAddresses().get(j).getNeighborhood() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD, 	
 	 	 						contact.getAddresses().get(j).getNeighborhood());	
 	 	 			}
 	 	 			if(contact.getAddresses().get(j).getCity() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.StructuredPostal.CITY, 	
 	 	 						contact.getAddresses().get(j).getCity());	
 	 	 			}
 	 	 			if(contact.getAddresses().get(j).getRegion() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.StructuredPostal.REGION, 	
 	 	 						contact.getAddresses().get(j).getRegion());	
 	 	 			}
 	 	 			if(contact.getAddresses().get(j).getPostcode() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, 	
 	 	 						contact.getAddresses().get(j).getPostcode());
 	 	 			}
 	 	 			if(contact.getAddresses().get(j).getCountry() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, 	
 	 	 						contact.getAddresses().get(j).getCountry());	
 	 	 			}
 	 	 			values.add(content);
 				}
 			}
 	 		Log.v(TAG, contactID +  ") " + values.size()  + " StructuredPostal added.");

 			// Add web sites
 			for(int j = 0; j < contact.getWebsites().size(); j++){
 				if(contact.getWebsites().get(j).getType() > -1){
 	 	 			content = new ContentValues();
 	 	 			content.put(ContactsContract.Data.RAW_CONTACT_ID, contactID);
 	 	 			content.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
 	 	 			content.put(ContactsContract.CommonDataKinds.Website.URL, contact.getWebsites().get(j).getURL());
 	 	 			content.put(ContactsContract.CommonDataKinds.Website.TYPE, 		contact.getWebsites().get(j).getType());
 	 	 			if(contact.getWebsites().get(j).getLabel() != null){
 	 	 				content.put(ContactsContract.CommonDataKinds.Website.LABEL, 	contact.getWebsites().get(j).getLabel());	
 	 	 			}
 	 	 			values.add(content);
 				}
 			}
 		}
	 	Log.v(TAG, contactID +  ") " + values.size()  + " Website added.");
 		
 		ContentValues[] contentArray = new ContentValues[values.size() > 0 ? values.size() : 0];
 		for(int i =0; i < contentArray.length; i++){
 			contentArray[i] = values.get(i);
 		}
 		
 		Log.v(TAG, contactID +  ") pre insert " + values.size() + "/" + contentArray.length );
 		
 		Log.v(TAG, contactID +  "Inserted to " + cr.insert(ContactsContract.Data.CONTENT_URI, contentArray[0]));
 		;
 		// success = cr.bulkInsert( ContactsContract.Data.CONTENT_URI, contentArray) > 0;

 		
		return success;
 	}
 	
 	private ArrayList<Long> insertEmptyRawContacts(int numberOfContacts) {
 		
 		int success = 0;
 		ArrayList<Long> ids = new ArrayList<Long> ();
 		Account csvAccount = new Account ("CSVContacts","edu.kris.contacts");
 		ContentValues[] values = new ContentValues[numberOfContacts > 0 ? numberOfContacts : 0];

 		// Insert the raw contacts
 		if(numberOfContacts > 0){
 	 		for(int i = 0; i < numberOfContacts; i++) {
 	 			ContentValues content = new ContentValues();
 	 			content.put(ContactsContract.RawContacts.ACCOUNT_NAME, csvAccount.name);
 	 			content.put(ContactsContract.RawContacts.ACCOUNT_TYPE, csvAccount.type);
 	 			values[i] = content;  
 	 		}
 	 		success = cr.bulkInsert( ContactsContract.RawContacts.CONTENT_URI, values);
 		}
 		
 		// Retrieve the IDs of the newly inserted raw Contacts
 		if(success > 0 ){
 	 		ArrayList<RawContactInfo> infos = getRawContactInfo(csvAccount);
 	 		for(int i = infos.size() - success; i >=0 && i < infos.size(); i++){
 	 			ids.add(0, Long.valueOf(infos.get(i).contactID));
 	 	 		Log.v(TAG,i + ") Assigned contact id = " + ids.get(0));
 	 		}
 	 		
 	 		for(int i= 0; i < infos.size(); i++){
 	 	 		Log.v(TAG,i + ") contact id = " + infos.get(i).contactID + " | " + infos.get(i).id);
 	 		}
 		}
 		
 		return ids;
 	}
 	
 	private ArrayList <RawContact> removeContacts(ArrayList <RawContact> people){
 		ArrayList <String> names = getDisplayNames(); 
 		int duplicates = 0;
 		int peopleSize = people.size();
 		
 		// Remove duplicate entries
 		for(int i = peopleSize-1; i >= 0; i--){
 			if(names.contains(people.get(i).getDisplayName())) {
 				people.remove(i);
 				duplicates++;
 			} else {
 		 		Log.d(TAG,"Not a duplicate: " + people.get(i).getDisplayName());
 			}
 		}
 		
 		Log.d(TAG+"[removeContacts]","People: " + peopleSize + " | Duplicates: " + duplicates + " | will insert : " + people.size());

 		return people;
 	} 
 	
 	private ArrayList <String> getDisplayNames () {
 		
 		String sortOder = ContactsContract.Contacts._ID + " DESC";
 		String [] projection = new String [] {ContactsContract.Data._ID, ContactsContract.Contacts.DISPLAY_NAME };
 		ArrayList <String> names = new ArrayList <String> ();
 		
 		Cursor cur = this.cr.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, sortOder);
 		if (cur.getCount() > 0) {
 			while (cur.moveToNext()) {
 				names.add(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
 			}
 		}
 		
 		return names;
 	}
 	
 	private ArrayList<RawContactInfo> getRawContactInfo(Account account) {
 		
 	    String[] projection = new String [] {
 	    		ContactsContract.RawContacts._ID,
 	    		ContactsContract.RawContacts.CONTACT_ID,
 	    		ContactsContract.RawContacts.ACCOUNT_NAME,
 	    		ContactsContract.RawContacts.ACCOUNT_TYPE,
 	    		ContactsContract.RawContacts.DELETED
 	    };
 	    
 	    String selection = null;
 	    String [] selectionArgs = null;
 		String sortOder = ContactsContract.RawContacts._ID + " ASC"; 

 		ArrayList<RawContactInfo> rawContacts = new ArrayList<RawContactInfo> ();
 		
 	    // Filter out specific accounts 
 	    if(account != null){
 	    	selection = ContactsContract.RawContacts.ACCOUNT_NAME + "=? AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + "=?";
 	    	selectionArgs = new String [] { account.name, account.type};  
 	    }
 	    
 	    // Next get the raw contact account info (we need this to identify unique accounts)
 	    Cursor cur= cr.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, sortOder);
  	   	if (cur.moveToFirst()) {
  	   		while(cur.moveToNext()) {
  	   			rawContacts.add(new RawContactInfo(
  	   				cur.getLong(cur.getColumnIndex(ContactsContract.RawContacts._ID)),
  	   				cur.getLong(cur.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID)),
					cur.getString(cur.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME)),
 					cur.getString(cur.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE)),
 					(cur.getInt(cur.getColumnIndex(ContactsContract.RawContacts.DELETED)) != 0)
  	   				));
  	   		}
  	   	}
  	   	
 		return rawContacts;
 	}
 	
 	public ArrayList<Phone> getPhoneNumbers(String id) {

 		ArrayList<Phone> phones = new ArrayList<Phone>();
 		String where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?"; 
 		String[] whereParameters = new String[]{ id };
 		
 		Cursor pCur = this.cr.query(
 				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, where, whereParameters, null);
 		while (pCur.moveToNext()) {
		   phones.add(new Phone(new String [] {
				pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)), 
 				pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)),
 				pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL))
		   }));
 		} 
 		pCur.close();
 		return(phones);
 	}
 	
 	public ArrayList<Email> getEmailAddresses(String id) {
 		
 		ArrayList<Email> emails = new ArrayList<Email>();
 		String where = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?"; 
 		String[] whereParameters = new String[]{ id };
 		
 		Cursor emailCur = this.cr.query(
 				ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, where, whereParameters, null);
 		while (emailCur.moveToNext()) { 
 			emails.add(new Email(new String [] {
 				emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)), 
 				emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE)),
 	 			emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL))
 			}));
 		} 
 		emailCur.close();
 		
 		return(emails);
 	}
 	
 	public ArrayList<Note> getContactNotes(String id) {
 		
 		ArrayList<Note> notes = new ArrayList<Note>();
 		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
 		String[] whereParameters = new String[]{id, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
 		
 		Cursor noteCur = this.cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null); 
 		if (noteCur.moveToFirst()) { 
 			String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
 			if (note.length() > 0) {
 				notes.add(new Note(new String [] { note }));
 			}
 		} 
 		noteCur.close();
 		
 		return(notes);
 	}
 	
 	public ArrayList<StructuredPostal> getContactAddresses(String id) {
 		ArrayList<StructuredPostal> addrList = new ArrayList<StructuredPostal>();
 		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
 		String[] whereParameters = new String[]{id, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}; 
 		
 		Cursor addrCur = this.cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null); 
 		while(addrCur.moveToNext()) {
 			addrList.add(new StructuredPostal(new String [] {
 				addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)), 
 				addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE)),
 				addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.LABEL)), 
 				addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET)), 
 				addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX)), 
 				addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD)), 
 				addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY)), 
 				addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION)), 
 				addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE)), 
 				addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY))
 			}));
 		} 
 		addrCur.close();
 		return(addrList);
 	}
 	
 	public ArrayList<IM> getIM(String id) {
 		ArrayList<IM> imList = new ArrayList<IM>();
 		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
 		String[] whereParameters = new String[]{id, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE}; 
 		
 		Cursor imCur = this.cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null); 
 		if (imCur.moveToFirst()) { 
 			String imName = imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
 			if (imName.length() > 0) {
 				imList.add(new IM(new String [] {
 						imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA)),
 						imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE)),
 						imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.LABEL)),
 						imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL)),
 						imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL)),
 				}));
 			}
 		} 
 		imCur.close();
 		return(imList);
 	}
 	
 	public StructuredName getStructuredName(String id) {
 		StructuredName name = new StructuredName();
 		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
 		String[] whereParameters = new String[]{id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}; 
 		
 		Cursor nameCur = this.cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
 		if (nameCur.moveToFirst()) {  			
 			name.setGivenName(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));
 			name.setFamilyName(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)));
 			name.setPrefix(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PREFIX)));
 			name.setMiddleName(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME)));
 			name.setSuffix(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.SUFFIX)));
 			name.setPhoneticGivenName(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME)));
 			name.setPhoneticMiddleName(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME)));
 			name.setPhoneticFamilyName(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME)));
 		} 
 		nameCur.close();
 		
 		return name;
 	}
 	
 	public AccountInfo getAccount(String id) {
 		AccountInfo account = new AccountInfo();
 		String where = ContactsContract.Data.CONTACT_ID + " = ?"; 
 		String[] whereParameters = new String[]{ id }; 
 		
 		Cursor accCur = this.cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
 		if (accCur.moveToFirst()) {
 			account.setAccountName(accCur.getString(accCur.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME)));
 			account.setAccountType(accCur.getString(accCur.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE)));
 		} 
 		accCur.close();
 		
 		return account;
 	}
 	
 	private class RawContactInfo {
 		private long id;
 		private long contactID;
 		private String name;
 		private String type;
 		private boolean deleted;
 		
 		public RawContactInfo(long id, long contactId, String name, String type, boolean deleted){
 			this.id = id;
 			this.contactID = contactId;
 			this.name = name;
 			this.type = type;
 			this.deleted = deleted;
 		}
 	}
}
