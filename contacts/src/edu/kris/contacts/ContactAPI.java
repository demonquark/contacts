package edu.kris.contacts;

import java.util.ArrayList;

import edu.kris.contacts.model.*;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
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
 	
 	public ArrayList <Contact> getContacts(){
 		return getContacts(null, -1);
 	}
 	
 	public ArrayList <Contact> getContacts(MainActivity.ContactTask task, int max) {
 		
 		String sortOder = ContactsContract.Contacts._ID + " DESC"; 
 		ArrayList<Contact> contacts = new ArrayList<Contact>();
 		String id;
 		int i = 0;
 		
 		Cursor cur = this.cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, sortOder);
 		if (cur.getCount() > 0) {
 			
 			// set the maximum number of records
 			if(max < 0) { max = cur.getCount(); }
 			
 			while (cur.moveToNext() && i < max) {
 				// publish progress
 				task.updateProgress((i*100) / max);
 				
 				Contact c = new Contact();
 				id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
 				c.setId(id);
 				c.setDisplayName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
 				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
 					c.setPhone(this.getPhoneNumbers(id));
 				}
 				c.setEmail(this.getEmailAddresses(id));
 				c.setNotes(this.getContactNotes(id));
 				c.setAddresses(this.getContactAddresses(id));
 				c.setImAddresses(this.getIM(id));
 				c.setOrganization(this.getContactOrg(id));
 				c.setStructuredName(this.getStructuredName(id));
 				c.setAccount(this.getAccount(id));
 				contacts.add(c);
 				i++;
 			}
 		}
 		
 		return contacts;
 	}
 	
 	public ArrayList <Contact> getContactsAlternative(MainActivity.ContactTask task, int max) {
 		
 		ArrayList<Contact> contacts = new ArrayList<Contact>();
 		int i = 0;
 	    String[] projection = new String [] {
 	    		ContactsContract.Data._ID,
 	    		ContactsContract.Data.RAW_CONTACT_ID,
 	    		ContactsContract.RawContactsEntity.CONTACT_ID,
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
 	    
 	    Cursor cur= cr.query(ContactsContract.Data.CONTENT_URI, projection, null, null, null);
 	    
 	   if (cur.moveToFirst()) {
 		    do {
// 		        StringBuilder sb = new StringBuilder();
// 		        int columnsQty = cur.getColumnCount();
// 		        for (int idx=0; idx<columnsQty; ++idx) {
// 		            sb.append(cur.getString(idx));
// 		            if (idx < columnsQty - 1)
// 		                sb.append("; ");
// 		        }
// 		        if(i < 20)
// 		        	Log.v(TAG, String.format("Row: %d, Values: %s", cur.getPosition(), sb.toString()));
 		        i++;
 		    } while (cur.moveToNext());
 		   
 		    Log.v(TAG, "There are " + i + " instances.");
 		    cur.close();
 		}
 	    
 	    
// 	    if (cur.getCount() > 0) {
// 			
// 			// set the maximum number of records
// 			if(max < 0) { max = cur.getCount(); }
// 			
// 			while (cur.moveToNext() && i < max) {
// 				// publish progress
// 				task.updateProgress((i*100) / max);
// 				
// 				Contact c = new Contact();
// 				id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
// 				c.setId(id);
// 				c.setDisplayName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
// 				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
// 					c.setPhone(this.getPhoneNumbers(id));
// 				}
// 				c.setEmail(this.getEmailAddresses(id));
// 				c.setNotes(this.getContactNotes(id));
// 				c.setAddresses(this.getContactAddresses(id));
// 				c.setImAddresses(this.getIM(id));
// 				c.setOrganization(this.getContactOrg(id));
// 				c.setStructuredName(this.getStructuredName(id));
// 				c.setAccount(this.getAccount(id));
// 				contacts.add(c);
// 				i++;
// 			}
// 		}
 		
 		return contacts;
 	}

 	public ArrayList<Phone> getPhoneNumbers(String id) {

 		ArrayList<Phone> phones = new ArrayList<Phone>();
 		String where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?"; 
 		String[] whereParameters = new String[]{ id };
 		
 		Cursor pCur = this.cr.query(
 				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, where, whereParameters, null);
 		while (pCur.moveToNext()) {
 			phones.add(new Phone(
 					pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)), 
 					pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
 			));
 
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
 			emails.add(new Email(
 					emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)),
 					emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE))  
 			));
 		} 
 		emailCur.close();
 		
 		return(emails);
 	}
 	
 	public ArrayList<String> getContactNotes(String id) {
 		
 		ArrayList<String> notes = new ArrayList<String>();
 		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
 		String[] whereParameters = new String[]{id, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
 		
 		Cursor noteCur = this.cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null); 
 		if (noteCur.moveToFirst()) { 
 			String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
 			if (note.length() > 0) {
 				notes.add(note);
 			}
 		} 
 		noteCur.close();
 		
 		return(notes);
 	}
 	
 	public ArrayList<Address> getContactAddresses(String id) {
 		ArrayList<Address> addrList = new ArrayList<Address>();
 		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
 		String[] whereParameters = new String[]{id, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}; 
 		
 		Cursor addrCur = this.cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null); 
 		while(addrCur.moveToNext()) {
 			addrList.add(new Address(
 					addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX)), 
 					addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET)), 
 					addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY)), 
 					addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION)), 
 					addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE)), 
 					addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY)), 
 					addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE))
 			));
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
 			String imType = imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
 			if (imName.length() > 0) {
 				imList.add(new IM(imName, imType));
 			}
 		} 
 		imCur.close();
 		return(imList);
 	}
 	
 	public Organization getContactOrg(String id) {
 		Organization org = new Organization();
 		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
 		String[] whereParameters = new String[]{id, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}; 
 		
 		Cursor orgCur = this.cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
 		if (orgCur.moveToFirst()) { 
 			String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
 			String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
 			if (orgName.length() > 0) {
 				org.setOrganization(orgName);
 				org.setTitle(title);
 			}
 		}
 		orgCur.close();
 		return(org);
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
 	
 	public boolean insertContact(Contact person, MainActivity.ContactTask task){
 		ArrayList <Contact> people = new ArrayList <Contact> ();
 		if(person != null) { people.add(person); }
 		return insertContacts(people, task);
 	}
 	
 	public boolean insertContacts(ArrayList <Contact> people, MainActivity.ContactTask task) {
 		
 		ArrayList <String> names = getDisplayNames(); 
 		
 		for(int i = 0; i < names.size(); i++){
 			if(i%10 == 0)
 				Log.d(TAG, i + ") " + names.get(i) + "|");
 		}
 		
 		// Remove duplicate entries
 		for(Contact person : people){
 			
 		}
 		

// 		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

 		
 		
 		return false;
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
}
