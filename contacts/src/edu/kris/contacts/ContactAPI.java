package edu.kris.contacts;

import java.util.ArrayList;

import edu.kris.contacts.model.*;
import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
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
 	
 	public ArrayList <RawContact> getContacts(MainActivity.ContactTask task, int max) {
 		
 		// First get the contact data
 		int i = 0;
 		long orgID = -1;
 		long dbsID = -1;
 		ArrayList<RawContact> contacts = getContactData(task, max);
 		ArrayList<RawContactInfo> infos = getRawContactInfo(null, max);
 		int size = contacts.size();
 		
 		for(RawContactInfo info : infos) {
   			// Get the raw contact ID
   			dbsID = info._id;
   			// use the fact that the contacts and info are sorted in RAW_CONTACT_ID DESCENDING
   			while ((orgID < 0 || dbsID < orgID) && i < size) {
  	   			orgID = contacts.get(i).getId();
  	   			i++;
   			}
   			
   			// add the new data
   			if(dbsID == orgID) {
   				// There's already a raw contact with that ID, add the account info to that contact
   				contacts.get(i > 0 ? i - 1 : 0).setAccount(new AccountInfo(new String [] {info.name,info.type}));
   			} else if(!info.deleted) {
   				// There is no raw contact with that ID, create a new contact and add the account info
   				RawContact rawContact = new RawContact (info._id, info.contactID);
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
 	
 	private ArrayList <RawContact> getContactData(MainActivity.ContactTask task, int maxContacts) {
 		
 		int recordIndex = 0;
 		int rawContactsIndex = 0;
 		int maxRecords = 0;
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
 	    
 		String sortOder = ContactsContract.Data.RAW_CONTACT_ID + " DESC"; 
 	    Cursor cur= cr.query(ContactsContract.Data.CONTENT_URI, projection, null, null, sortOder);
 	    
 	   if ((maxRecords = cur.getCount()) > 0) {

 		   while (cur.moveToNext() && (rawContactsIndex < maxContacts || maxContacts < 0)) {

 			   // Increase the counter
 			   if(task != null) { task.updateProgress((recordIndex*100) / maxRecords); }
 			   recordIndex++;
 			    			   
 			   // Check if we have reached a new raw contact
 			   if(rawID != cur.getLong(cur.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID))){
 				   // We're about to add a new contact
 				  rawContactsIndex++;
 	 			   
 				   if(rawContactsIndex < maxContacts || maxContacts < 0){
 	 				   // Create a new contact
 	 				   rawID = cur.getLong(cur.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
 	 				   contactID = cur.getLong(cur.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));
 	 				   contact = new RawContact(rawID, contactID);
 	 				   contact.setDisplayName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
 	 				   contact.setIsPrimary(cur.getInt(cur.getColumnIndex(ContactsContract.Data.IS_PRIMARY)));
 					   contacts.add(contact);
 				   } else{
 	 				   // We've reached the maximum number of contacts
 					   break;
 				   }
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
		Log.d(TAG, "Processed " + recordIndex + " records and " + contacts.size() + " contacts. Total Records=" + maxRecords);
 	   
 	   	return contacts;
 	}
 	
 	public boolean insertContacts(ArrayList <RawContact> people, MainActivity.ContactTask task){
	 	boolean success = false;
		printInfo();

		// remove duplicate contacts
		long now = System.currentTimeMillis();
 		people = removeContacts(people);
 		Log.i(TAG, "REMOVE PEOPLE " + (System.currentTimeMillis() - now) + " milliseconds.");

 		// Add the Raw contacts
		now = System.currentTimeMillis();
 		insertOpsContacts(people, task);
 		printInfo();
 		return success;
 	}
 	
 	private boolean insertOpsContacts(ArrayList <RawContact> people, MainActivity.ContactTask task) {
 		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
 		ContentProviderOperation.Builder operation = null;
 		
	 	boolean success = false;
 		RawContact contact;
 		int peopleSize = people.size();
 		long groupID = getDefaultGroupId();
 		
 		// insert the raw contact data
 		for(int i = 0; i < peopleSize; i++) {
 			int backReference = ops.size();
	 		ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI
	 				.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build())
	 	 	 		   .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, getDefaultAccount().name)
	 	 	 		   .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, getDefaultAccount().type)
	 	 	 		   .build());

 			contact = people.get(i);

 			
 			// insert the email address
 			for(int j = 0; j < contact.getEmail().size(); j++){
				// Build the initial URI
				Email email = contact.getEmail().get(j);
				operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backReference)
		  	 	 		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
				
				// Add the email values to the URI
				if(email.getAddress() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Email.ADDRESS,	email.getAddress());
				}
				if(email.getType() >= 0){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Email.TYPE,	email.getType());
				}
				if(email.getLabel()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Email.LABEL, 	email.getLabel());
				}
				
				// Add the operation to list of operations
	 	 		ops.add(operation.build());
	 	 		operation = null;
			}
 			
 			// insert the events
 			for(int j = 0; j < contact.getEvents().size(); j++){
				// Build the initial URI
				Event event = contact.getEvents().get(j);
				operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backReference)
		  	 	 		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
				
				// Add the data values to the URI
				if(event.getDate() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Event.START_DATE,	event.getDate());
				}
				if(event.getType() >= 0){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Event.TYPE,	event.getType());
				}
				if(event.getLabel()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Event.LABEL, 	event.getLabel());
				}
				
				// Add the operation to list of operations
	 	 		ops.add(operation.build());
	 	 		operation = null;
			}

 			// insert the events
 			for(int j = 0; j < contact.getImAddresses().size(); j++){
				// Build the initial URI
				IM im = contact.getImAddresses().get(j);
				operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backReference)
		  	 	 		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE);
				
				// Add the data values to the URI
				if(im.getData() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Im.DATA,	im.getData());
				}
				if(im.getType() >= 0){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Im.TYPE,	im.getType());
				}
				if(im.getLabel()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Im.LABEL, 	im.getLabel());
				}
				if(im.getProtocol()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Im.PROTOCOL, 	im.getProtocol());
				}
				if(im.getCustomProtocol()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL, 	im.getCustomProtocol());
				}
				
				// Add the operation to list of operations
	 	 		ops.add(operation.build());
	 	 		operation = null;
			}

 			// Add Nickname
 			if(contact.getNickName() != null){
				// Build the initial URI
				NickName nick = contact.getNickName();
				operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backReference)
		  	 	 		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
				
				// Add the data values to the URI
				if(nick.getName() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Nickname.DATA,		nick.getName());
				}
				if(nick.getType() >= 0){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Nickname.TYPE,		nick.getType());
				}
				if(nick.getLabel()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Nickname.LABEL, 	nick.getLabel());
				}
				
				// Add the operation to list of operations
	 	 		ops.add(operation.build());
	 	 		operation = null;
 			}

 			// insert the notes
 			for(int j = 0; j < contact.getNotes().size(); j++){
				// Build the initial URI
				Note note = contact.getNotes().get(j);
				operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backReference)
		  	 	 		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);
				
				// Add the data values to the URI
				if(note.getNote() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Note.NOTE,	note.getNote());
				}
				
				// Add the operation to list of operations
	 	 		ops.add(operation.build());
	 	 		operation = null;
			}
 			
 			// Add Organization
 			if(contact.getOrganization() != null){
				// Build the initial URI
				Organization org = contact.getOrganization();
				operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backReference)
		  	 	 		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE);
				
				// Add the data values to the URI
				if(org.getCompany() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Organization.COMPANY,	org.getCompany());
				}
				if(org.getType() >= 0){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Organization.TYPE,		org.getType());
				}
				if(org.getLabel()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Organization.LABEL, 	org.getLabel());
				}
 	 			if(org.getTitle() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Organization.TITLE, 	org.getTitle());
 	 			}
 	 			if(org.getDepartment() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Organization.DEPARTMENT, 		org.getDepartment());
 	 			}
 	 			if(org.getJobDescription() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Organization.JOB_DESCRIPTION, 	org.getJobDescription());
 	 			}
 	 			if(org.getSymbol() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Organization.SYMBOL, 			org.getSymbol());
 	 			}
 	 			if(org.getPhoneticName() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Organization.PHONETIC_NAME, 	org.getPhoneticName());
 	 			}
 	 			if(org.getOfficeLocation() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Organization.OFFICE_LOCATION, 	org.getOfficeLocation());
 	 			}
				
				// Add the operation to list of operations
	 	 		ops.add(operation.build());
	 	 		operation = null;
 			}
			
 			// insert the phones
 			for(int j = 0; j < contact.getPhone().size(); j++){
				// Build the initial URI
				Phone phone = contact.getPhone().get(j);
				operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backReference)
		  	 	 		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
				
				// Add the data values to the URI
				if(phone.getNumber() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,	phone.getNumber());
				}
				if(phone.getType() >= 0){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,	phone.getType());
				}
				if(phone.getLabel()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Phone.LABEL, 	phone.getLabel());
				}
				
				// Add the operation to list of operations
	 	 		ops.add(operation.build());
	 	 		operation = null;
			}
 			
 			// Add Organization
 			if(contact.getRelation() != null){
				// Build the initial URI
				Relation rel = contact.getRelation();
				operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backReference)
		  	 	 		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE);
				
				// Add the data values to the URI
				if(rel.getName() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Relation.NAME,	rel.getName());
				}
				if(rel.getType() >= 0){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Relation.TYPE,		rel.getType());
				}
				if(rel.getLabel()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Relation.LABEL, 	rel.getLabel());
				}
				
				// Add the operation to list of operations
	 	 		ops.add(operation.build());
	 	 		operation = null;
 			}
 			
 			// insert the sips
 			for(int j = 0; j < contact.getSips().size(); j++){
				// Build the initial URI
				SIPAddress sip = contact.getSips().get(j);
				operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backReference)
		  	 	 		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE);
				
				// Add the data values to the URI
				if(sip.getData() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS,	sip.getData());
				}
				if(sip.getType() >= 0){
					operation = operation.withValue(ContactsContract.CommonDataKinds.SipAddress.TYPE,	sip.getType());
				}
				if(sip.getLabel()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.SipAddress.LABEL, 	sip.getLabel());
				}
				
				// Add the operation to list of operations
	 	 		ops.add(operation.build());
	 	 		operation = null;
			}
 			
 			// Add Structured Name
 			if(contact.getStructuredName() != null){
				// Build the initial URI
				StructuredName name = contact.getStructuredName();
				operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backReference)
		  	 	 		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
				
				// Add the data values to the URI
				if(contact.getDisplayName() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getDisplayName());
				}
				if(name.getGivenName() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,	name.getGivenName());
				}
				if(name.getFamilyName() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, name.getFamilyName());
				}
				if(name.getPrefix() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredName.PREFIX, name.getPrefix());
				}
				if(name.getMiddleName() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, name.getMiddleName());
				}
				if(name.getSuffix() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredName.SUFFIX, name.getSuffix());
				}
				if(name.getPhoneticGivenName() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME, name.getPhoneticGivenName());
				}
				if(name.getPhoneticMiddleName() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME, name.getPhoneticMiddleName());
				}
				if(name.getPhoneticFamilyName() != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME, name.getPhoneticFamilyName());
				}
				
				// Add the operation to list of operations
	 	 		ops.add(operation.build());
	 	 		operation = null;
 			}
 			
 			// Add postal addresses
 			for(int j = 0; j < contact.getAddresses().size(); j++){
				// Build the initial URI
 				StructuredPostal post = contact.getAddresses().get(j);
				operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backReference)
		  	 	 		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
				
				// Add the data values to the URI
				operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,	post.toString());
				if(post.getType() >= 0){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE,	post.getType());
				}
				if(post.getLabel()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredPostal.LABEL, 	post.getLabel());
				}
				if(post.getStreet()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, 	post.getStreet());
				}
				if(post.getPobox()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredPostal.POBOX, 	post.getPobox());
				}
				if(post.getNeighborhood()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD, post.getNeighborhood());
				}
				if(post.getCity()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, 		post.getCity());
				}
				if(post.getRegion()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, 		post.getRegion());
				}
				if(post.getPostcode()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, 	post.getPostcode());
				}
				if(post.getCountry()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, 		post.getCountry());
				}
				
				// Add the operation to list of operations
	 	 		ops.add(operation.build());
	 	 		operation = null;
 			}

 			// Add postal addresses
 			for(int j = 0; j < contact.getWebsites().size(); j++){
				// Build the initial URI
 				Website site = contact.getWebsites().get(j);
				operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backReference)
		  	 	 		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
				
				// Add the data values to the URI
				if(site.getLabel()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Website.URL,	site.getURL());
				}
				if(site.getType() >= 0){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Website.TYPE,	site.getType());
				}
				if(site.getLabel()  != null){
					operation = operation.withValue(ContactsContract.CommonDataKinds.Website.LABEL, 	site.getLabel());
				}
				
				// Add the operation to list of operations
	 	 		ops.add(operation.build());
	 	 		operation = null;
 			}
 			
 			// Add it to the default group
			operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backReference)
	  	 	 		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
	  	 	 		.withValue(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, groupID);
			operation.withYieldAllowed(true);
 	 		ops.add(operation.build());
 	 		operation = null;
 	 		
 			if(task != null) { task.updateProgress((i*100) / peopleSize); }
 		}
 		
 		try {
			cr.applyBatch(ContactsContract.AUTHORITY, ops);
			success = true;
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			e.printStackTrace();
		}

 		return success;
 	}
 	 	
 	public Account getDefaultAccount(){
 		return new Account ("CSVContacts","edu.kris.contacts");
 	}
 	
 	private long getDefaultGroupId() {

 		long defaultGroupId = -1;
 		boolean alreadyExists = false;
        String[] projection = { ContactsContract.Groups.TITLE, ContactsContract.Groups._ID };
        String selection = ContactsContract.Groups.TITLE + "=?";
        String[] selectionArgs = new String [] { getDefaultAccount().name };  
        
 	    // Next get the raw contact account info (we need this to identify unique accounts)
 	    Cursor cur= cr.query(ContactsContract.Groups.CONTENT_URI, projection, selection, selectionArgs, null);
 	    alreadyExists = cur.getCount() > 0;
 	    if(alreadyExists && cur.moveToFirst()){
 	    	// Just get the id from the table
 	    	defaultGroupId = cur.getLong(cur.getColumnIndex(ContactsContract.Groups._ID));
 	    	if(cur != null){ cur.close(); }
 	    } else {
 	    	// Insert the group in the table and return the ID
 	    	if(cur != null){ cur.close(); }
 	    	ContentValues values = new ContentValues();
 	    	values.put(ContactsContract.Groups.TITLE, getDefaultAccount().name );
 	    	defaultGroupId = ContentUris.parseId(cr.insert(ContactsContract.Groups.CONTENT_URI, values));
 	    }
 		
 	    return defaultGroupId;
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
 		 		Log.v(TAG,"Not a duplicate: " + people.get(i).getDisplayName());
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
 		
 		if(cur != null){ cur.close(); }
 		
 		return names;
 	}
 	
 	private ArrayList<RawContactInfo> getRawContactInfo(Account account, int maxContacts) {

 		int rawContactsIndex = 0;
 		
 	    String[] projection = new String [] {
 	    		ContactsContract.RawContacts._ID,
 	    		ContactsContract.RawContacts.CONTACT_ID,
 	    		ContactsContract.RawContacts.ACCOUNT_NAME,
 	    		ContactsContract.RawContacts.ACCOUNT_TYPE,
 	    		ContactsContract.RawContacts.DELETED
 	    };
 	    
 	    String selection = null;
 	    String [] selectionArgs = null;
 		String sortOder = ContactsContract.RawContacts._ID + " DESC"; 

 		ArrayList<RawContactInfo> rawContacts = new ArrayList<RawContactInfo> ();
 		
 	    // Filter out specific accounts 
 	    if(account != null){
 	    	selection = ContactsContract.RawContacts.ACCOUNT_NAME + "=? AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + "=?";
 	    	selectionArgs = new String [] { account.name, account.type};  
 	    }
 	    
 	    // Next get the raw contact account info (we need this to identify unique accounts)
 	    Cursor cur= cr.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, sortOder);
  	   	if (cur.moveToFirst()) {
  	   		do {
  	   			if(rawContactsIndex < maxContacts || maxContacts < 0) {
  	  	   			rawContacts.add(new RawContactInfo(
  	    	   				cur.getLong(cur.getColumnIndex(ContactsContract.RawContacts._ID)),
  	    	   				cur.getLong(cur.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID)),
  	    	   				cur.getString(cur.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME)),
  	    	   				cur.getString(cur.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE)),
  	    	   				(cur.getInt(cur.getColumnIndex(ContactsContract.RawContacts.DELETED)) != 0)));
  	  	   			rawContactsIndex++;
  	   			} else {
  	   				break;
  	   			}
  	   		} while(cur.moveToNext());
  	   	}
  	   	
  	   	if(cur != null){ cur.close(); }
  	   	
 		return rawContacts;
 	}
 	
 	private void printInfo(){
 	    String[] projection = new String [] {
 	    		ContactsContract.RawContacts._ID,
 	    		ContactsContract.RawContacts.CONTACT_ID,
 	    		ContactsContract.RawContacts.ACCOUNT_NAME,
 	    		ContactsContract.RawContacts.ACCOUNT_TYPE,
 	    		ContactsContract.RawContacts.DELETED
 	    };
 	    
 	    String selection = null;
 	    String [] selectionArgs = null;
 		String sortOder = ContactsContract.RawContacts._ID + " DESC"; 

 	    // Next get the raw contact account info (we need this to identify unique accounts)
 	    Cursor cur= cr.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, sortOder);
  	   	if (cur.moveToFirst()) {
  	 	   Log.d(TAG, "RawContacts (" + cur.getCount() + " records):");
  	   		do {
  	  	   		Log.v(TAG, "RawContact: " + cur.getLong(cur.getColumnIndex(ContactsContract.RawContacts._ID))
 					+ " | " + cur.getLong(cur.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID)));
  	   		} while(cur.moveToNext());
  	   	}
  	   	
  	   	if(cur != null){ cur.close(); }
 		
 	    projection = new String [] {
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
 	    
		sortOder = ContactsContract.Data._ID + " DESC"; 
		selection = ContactsContract.Data._ID + ">=?";
		selectionArgs = new String [] { "0" };  
		cur= cr.query(ContactsContract.Data.CONTENT_URI, projection, selection, selectionArgs, sortOder);

	   // set the maximum number of records
 	   if (cur.moveToFirst()) {
 	 	   Log.d(TAG, "Data (" + cur.getCount() + " records):");
 		   do {
 		 	   Log.v(TAG, cur.getString(cur.getColumnIndex(ContactsContract.Data._ID)) + ") " 
 		 			   	+ cur.getString(cur.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID)) + " - " 
 		 			   	+ cur.getString(cur.getColumnIndex(ContactsContract.Data.MIMETYPE)) + " | " 
 		 			   	+ cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) + " | " 
 		 			   	+ cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1)) + " " 
 		 			   	+ cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA2)) + " " 
 		 			   	+ cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA3)) + " " 
 		 			   	+ cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA4)) + " " 
 		 			   	+ cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA5)) + " " 
 		 			   	+ cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA6)));
 		   } while (cur.moveToNext());
 	   }

 	   if(cur != null) { cur.close(); }
 	}
 	
 	private class RawContactInfo {
 		private long _id;
 		private long contactID;
 		private String name;
 		private String type;
 		private boolean deleted;
 		
 		public RawContactInfo(long _id, long contactId, String name, String type, boolean deleted){
 			this._id = _id;
 			this.contactID = contactId;
 			this.name = name;
 			this.type = type;
 			this.deleted = deleted;
 		}
 	}
}
