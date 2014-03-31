package com.example.BreadCrum;

import helpers.DataStoreHelper;
import helpers.LocationHelper;
import models.Contact;
import models.LocationTag;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MyActivity extends ListActivity {

    private static final int RQS_PICK_CONTACT = 1;
    private SimpleCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        EditText locationTag = (EditText) findViewById(R.id.location_tag);
        Button saveLocation = (Button) findViewById(R.id.save_location);
        listAddedLocationTags();
	}
    
    private void listAddedLocationTags() {
    	DataStoreHelper dataStoreHelper = new DataStoreHelper(this);
		Cursor cursor = dataStoreHelper.getAllLocationTagsAdapter();
		String[] columns = new String[] { DataStoreHelper.KEY_DESCRIPTION };
        int[] to = new int[] { R.id.location_description };
        adapter = new SimpleCursorAdapter(this, R.layout.location_tag_list, cursor, columns, to);
		setListAdapter(adapter);
		
	}

	public void addLocationTag(View view){
    	EditText locationTagText = (EditText) findViewById(R.id.location_tag);
    	String description = locationTagText.getText().toString();
    	if(!description.equals("")) {
	    	LocationHelper locationHelper = new LocationHelper(MyActivity.this);
	    	Location location = locationHelper.getLocation(); 
	    	
	    	DataStoreHelper dataStoreHelper = new DataStoreHelper(MyActivity.this);
	    	LocationTag locationTag = new LocationTag(description, location.getLatitude(), location.getLongitude());
	    	
	    	if(!dataStoreHelper.addLocationTag(locationTag)){
		    	  Toast.makeText(MyActivity.this,"Location description Already Added", Toast.LENGTH_SHORT).show();
		      }
		      else{
		    	  Cursor cursor = dataStoreHelper.getAllLocationTagsAdapter(); 
		    	  adapter.changeCursor(cursor);
		    	  locationTagText.setText("");
		      } 
    	}
    	
    	
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.action_list, menu);
      return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
      switch (item.getItemId()) {
      
      case R.id.contact_list:
    	intent = new Intent(this, ContactsActivity.class);
    	startActivity(intent);
        break;
      }

      return true;
    } 
	

	
}
