package com.example.BreadCrum;

import helpers.DataStoreHelper;
import models.Contact;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ContactsActivity extends ListActivity{
	private static final int RQS_PICK_CONTACT = 1;
    private SimpleCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listAddedContacts();
	}
    
    private void listAddedContacts() {
		DataStoreHelper dataStoreHelper = new DataStoreHelper(this);
		Cursor cursor = dataStoreHelper.getAllContactsAdapter();
		String[] columns = new String[] { DataStoreHelper.KEY_NAME, DataStoreHelper.KEY_PH_NO };
        int[] to = new int[] { R.id.contact_name, R.id.phone_number };
        adapter = new SimpleCursorAdapter(this, R.layout.contact_list, cursor, columns, to);
		setListAdapter(adapter);
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);

	   if(requestCode == RQS_PICK_CONTACT) {
		   if(resultCode == RESULT_OK) {
			   
			    Uri contactData = data.getData();
			    Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
			    cursor.moveToFirst();
			
			      String normalizedPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
			      String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			      
			      if(normalizedPhoneNumber != null) {
			    	  Contact newContact = new Contact(name,normalizedPhoneNumber);
			    	  DataStoreHelper dataStoreHelper = new DataStoreHelper(this);
				      if(!dataStoreHelper.addContact(newContact)){
				    	  Toast.makeText(ContactsActivity.this,"Contact Already Added", Toast.LENGTH_SHORT).show();
				      }
				      else{
				    	  cursor = dataStoreHelper.getAllContactsAdapter();
				    	  adapter.changeCursor(cursor);
				      } 
			      }
			      else{
			    	  Toast.makeText(ContactsActivity.this,"Not a valid Phone Number", Toast.LENGTH_SHORT).show();
			      }
			     }
	   		}
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.contacts_action_list, menu);
      return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
      switch (item.getItemId()) {
      
      case R.id.add_contact:
    	intent = new Intent(Intent.ACTION_GET_CONTENT);
      	intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
      	startActivityForResult(intent, 1);
        break;
        }

      return true;
    }
}
