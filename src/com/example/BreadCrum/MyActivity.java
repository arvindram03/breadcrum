package com.example.BreadCrum;

import models.Contact;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MyActivity extends ListActivity {

    private static final int RQS_PICK_CONTACT = 1;
	private Intent intent;
    private SimpleCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        initiateListeningService();
        listAddedContacts();
        
        Button buttonPickContact = (Button)findViewById(R.id.pickcontact);
        buttonPickContact.setOnClickListener(new Button.OnClickListener(){

	        @Override
	         public void onClick(View arg0) {
	
	        	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	        	intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
	        	startActivityForResult(intent, 1);             
	         }
        });
	}
	
	private void listAddedContacts() {
		DataStoreHelper dataStoreHelper = new DataStoreHelper(this);
		Cursor cursor = dataStoreHelper.getAllContactsAdapter();
		String[] columns = new String[] { DataStoreHelper.KEY_NAME, DataStoreHelper.KEY_PH_NO };
        int[] to = new int[] { R.id.contact_name, R.id.phone_number };
        adapter = new SimpleCursorAdapter(this, R.layout.contact_list, cursor, columns, to);
		setListAdapter(adapter);
		
	}

	private void initiateListeningService() {

        intent = new Intent(this, CallListeningService.class);
        startService(intent);
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);

	   if(requestCode == RQS_PICK_CONTACT) {
		   if(resultCode == RESULT_OK) {
			   
			    Uri contactData = data.getData();
			    Cursor cursor =  managedQuery(contactData, null, null, null, null);
			    cursor.moveToFirst();
			
			      String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
			      String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			      phoneNumber = phoneNumber.replaceAll(" ", "");
			      Contact newContact = new Contact(name,phoneNumber);
			      DataStoreHelper dataStoreHelper = new DataStoreHelper(this);
			      if(!dataStoreHelper.addContact(newContact)){
			    	  Toast.makeText(MyActivity.this,"Contact Already Added", Toast.LENGTH_SHORT).show();
			      }
			      else{
			    	  cursor = dataStoreHelper.getAllContactsAdapter();
			    	  adapter.changeCursor(cursor);
			      } 
			     }
	   		}
	}
//    public void toggleChange(View view){
//        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.togglebutton);
//        if(toggleButton.getText().equals("Inside Geofence")){
//            stopService(intent);
//            countDownTimer.cancel();
//        }else{
//            startService(intent);
//            countDownTimer.start();
//        }
//        Toast.makeText(MyActivity.this,toggleButton.getText(), Toast.LENGTH_SHORT).show();
//    }




}
