package edu.kris.contacts;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.kris.contacts.model.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity"; 
	
	ArrayList <RawContact> people;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Initial load
		new ContactTask(this, ContactTask.REFRESH).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void buttonHandler(View v) {
		int id = v.getId();
		switch(id){
		case R.id.btnInsert:
			new ContactTask(this, ContactTask.INSERT).execute();
			break;
		case R.id.btnBackup:
			new ContactTask(this, ContactTask.BACKUP).execute();
			break;
		case R.id.btnRefresh:
			new ContactTask(this, ContactTask.REFRESH).execute();
			break;
		}
	}
	
	public void handleTaskResult(boolean success, int taskId, int numberOfRecordsInView) {
        String output = "---start---\n";
        String name = (taskId == ContactTask.INSERT) ? "Insert" : (taskId == ContactTask.BACKUP) ? "Backup" : "Refresh"; 
        if(!success){
        	output += name + " failed\n";
        } else if (people == null) {
        	output += name + " succeeded, but no people\n";
        } else {
        	output += name + " succeeded.\n";
        	int i = 0;
            for(RawContact person : people){
            	i++;
            	
            	if(i > 56 && i < 56 + numberOfRecordsInView) {
            		output += person.getId() + ") " + person.getDisplayName() + "\n";

            		// Account type
            		output += "Type: "  + person.getAccount().getAccountName() + "|"
            							+ person.getAccount().getAccountType() + "|\n";
            		
            		// Name
            		output += "Name: |" + person.getStructuredName().getPrefix() + "|"
            							+ person.getStructuredName().getGivenName() + "|"
            							+ person.getStructuredName().getMiddleName() + "|"
            							+ person.getStructuredName().getFamilyName() + "|"
            							+ person.getStructuredName().getSuffix() + "|\n";

            		// Phone
            		output += "Phone: " + (person.getPhone() == null || person.getPhone().size() == 0 ? "null" : "") + "\n";
            		ArrayList <Phone> phones = person.getPhone();
                	for(Phone phone : phones){
                		output += phone.getNumber() + "|" + phone.getType() + "\n";
                	}
                	
                	// Email
                	output += "Email: " + (person.getEmail() == null || person.getEmail().size() == 0 ? "null" : "") + "\n";
                	ArrayList <Email> emails = person.getEmail();
                	for(Email email : emails){
                		output += email.getAddress() + "|" + email.getType() + "\n";
                	}
                	output += "~~~~~~~~\n";
            	}
            }
        }
        output += "---end---";
        
        TextView x = (TextView) findViewById(R.id.mainText);
        x.setText(output);
	}
	
	public File getBackupFolder(){
		File backupFolder = new File (new File (
							Environment.getExternalStorageDirectory().getAbsolutePath(), 
							"Download"), "backups");
		if(!backupFolder.exists()){ backupFolder.mkdirs(); }
		return backupFolder;
	}
	
	protected class ContactTask extends AsyncTask<Integer, Integer, Boolean> {

		private static final int INSERT  = 0;
		private static final int BACKUP  = 1;
		private static final int REFRESH = 2;

		protected ProgressDialog dialog;
		protected ContactAPI api;
		protected File backupFile;
		protected int taskId;

		private int MAX_RECORDS = -1;

		protected ContactTask(Context c, int taskId) {
			this(c,taskId,null);
		}
		
		protected ContactTask(Context c, int taskId, File backupFile) {
			this.taskId = taskId;
			this.dialog = new ProgressDialog(c);
			this.api = ContactAPI.getInstance(c);
			this.backupFile = backupFile;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Show the loading message
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setOnCancelListener(
					new OnCancelListener() { public void onCancel(DialogInterface dialog) { cancel(true); } });
			dialog.setMessage(getString(R.string.loading));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Integer... param) {
		
			// Create a backup file
			if(backupFile == null && taskId != REFRESH) { 
				backupFile = new File (getBackupFolder(), "contacts_" +".bak"); 
			}

			// Load the contacts if there are none.
			if(people == null && taskId != REFRESH) { refresh(); }
			
			// Do the requested task
			boolean success = false;
			switch(taskId){
			case INSERT:
				success = bulkInsert(backupFile);
				break;
			case BACKUP:
				success = bulkBackup(backupFile);
				break;
			case REFRESH:
				success = refresh();
				break;
			}
			
			return success;
		}
		
		/**
		 * <p>Update the dialog box with the progress.
		 * For the sake of simplicity I'm mixing three possible messages:</p>
		 * <ul>
		 * <li>0 &lt; message[1] &lt; 100 - Give a message saying that we're saving/loading (integer is the percentage)</li> 
		 * <li>All other cases - Give a message saying that the activity is unknown (ignore the integer)</li> 
		 * </ul>
		 */
		@Override
		protected void onProgressUpdate(Integer... message) {
			super.onProgressUpdate(message);
			// default message is loading 
			String msg = getString(R.string.unknown);
			switch(taskId){
			case INSERT:
				msg = getString(R.string.saving);
				break;
			case BACKUP:
				msg = getString(R.string.saving);
				break;
			case REFRESH:
				msg = getString(R.string.loading);
				break;
			}
			
			if(message != null && message.length > 0 && message[0] >= 0 && message[0] <= 100){
				msg += " " + message[0] + "%";
			}
			
			// publish the upgraded progress
			dialog.setMessage(msg);	
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// Hide the dialog
			if (dialog.isShowing()) { dialog.dismiss(); }
			handleTaskResult(result, taskId, 10);
		}

		@Override
	    protected void onCancelled() {
			if (dialog.isShowing()) { dialog.dismiss(); }
	    }
		
		protected void updateProgress(Integer... progress){
			this.publishProgress(progress);
		}
		
		public boolean bulkInsert(File backupFile) {
			CSVReader reader = null;
			boolean success = false;
			long now = System.currentTimeMillis();
			
			try {
				if(backupFile.exists()){
					
					int i = 0;
					int numberOfLines = countLines(backupFile);
					
					// Create a list and a reader for the file
					String [] line = null;
					ArrayList<RawContact> newPeople = new ArrayList <RawContact> ();
					reader = new CSVReader(new InputStreamReader(new FileInputStream(backupFile), "UTF-8"),',','"', true);
				    
					// skip the first line (it's the header row)
					reader.readNext();
					
					// Read the file
				    while ((line = reader.readNext()) != null) {
						// Add the contacts to the file
						newPeople.add(new RawContact(line));
						
						if(i < 4 || (i > 20 && i < 24)){
							Log.d(TAG, i + ") |" + line[1] + "|-|" + line[2] + "|-|" + line[3] + "|-|" + line[4] + "|-|" + line[5] + "|");
						}
						
						// Update progress
						publishProgress((i*100)/numberOfLines);
						i++;
				    }
					
					// Insert
					api.insertContacts(newPeople, this);
				    people = newPeople;

				    success = true;
				}
			} catch (IOException ioe) {
				Log.e(TAG,"IOException while reading from backup file.");
				ioe.printStackTrace(); 
			} catch (NullPointerException e){
				Log.e(TAG,"NullPointerException while reading from backup file.");
				e.printStackTrace();
			} finally {
				// Close the reader
				if(reader != null){ try { reader.close(); } catch (IOException e) { e.printStackTrace(); } }
			}
			
			Log.i(TAG, (System.currentTimeMillis() - now) + " milliseconds is duration of INSERT.");
			
			
			
			return success;
		}

		public boolean bulkBackup(File backupFile){
			CSVWriter writer = null;
			boolean success = false;
			long now = System.currentTimeMillis();
			
			try {
				
				// Overwrite or create the backup file
				if(backupFile.exists()){ backupFile.delete(); }
				backupFile.createNewFile();
				writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(backupFile), "UTF8"),',','"');
				
				// Write the people to the file 
				if(people != null ){
					
					// Write the header information 
					writer.writeNext(RawContact.getHeader());
					
					// Write the primary contact information
					int numberOfpeople = people.size();
					for(int i = 0; i < numberOfpeople; i++){
						// Write line
						writer.writeNext(people.get(i).getValues());
						
						// Update progress
						publishProgress((i*100)/numberOfpeople);
					}
				
				}
				
				// done
				success = true;
				
			} catch (IOException ioe) {
				Log.e(TAG,"IOException while writing to backup file.");
				ioe.printStackTrace(); 
			} catch (NullPointerException e){
				Log.e(TAG,"NullPointerException while writing to backup file.");
				e.printStackTrace();
			} finally {
				// Close the writer
				if(writer != null){ try { writer.close(); } catch (IOException e) { e.printStackTrace(); } }
			}
			
			Log.i(TAG, (System.currentTimeMillis() - now) + " milliseconds is duration of BACKUP.");
			
			return success;
		}
		
		public boolean refresh() {
			long now = System.currentTimeMillis();
	        people = api.getContacts(this, MAX_RECORDS);
//	        people = api.getContactsAlternative(this, MAX_RECORDS);
	        Log.i(TAG, (System.currentTimeMillis() - now) + " milliseconds is duration of REFRESH.");
	        return true;
		}
		
		public int countLines(File filename) throws IOException {
		    InputStream is = new BufferedInputStream(new FileInputStream(filename));
		    try {
		        byte[] c = new byte[1024];
		        int count = 0;
		        int readChars = 0;
		        boolean endsWithoutNewLine = false;
		        while ((readChars = is.read(c)) != -1) {
		            for (int i = 0; i < readChars; ++i) {
		                if (c[i] == '\n')
		                    ++count;
		            }
		            endsWithoutNewLine = (c[readChars - 1] != '\n');
		        }
		        if(endsWithoutNewLine) {
		            ++count;
		        } 
		        return count;
		    } finally {
		        is.close();
		    }
		}
	}
}
