package activities;

import adapters.TabAdapter;
import android.R.color;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;

import fragments.ContactListFragment;
import fragments.LocationListFragment;
import fragments.LogListFragment;
import helpers.database.DataStoreHelper;
import helpers.geofence.GeofenceRemover;
import helpers.geofence.GeofenceRequester;
import helpers.location.LocationHelper;

import java.util.ArrayList;
import java.util.List;

import receivers.GeofenceTransitionsReceiver;
import com.breadcrumb.R;
import utils.GeofenceUtils;
import utils.GeofenceUtils.REMOVE_TYPE;
import utils.GeofenceUtils.REQUEST_TYPE;

import models.Contact;
import models.SimpleGeofence;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener{

	private static final int RQS_PICK_CONTACT = 1;
	private static final int LOCATION = 0;
    private static final int LOG = 1;
    private static final int CONTACT = 2;

	TabAdapter appSectionPagerAdapter;
	ViewPager viewPager;
	private REQUEST_TYPE requestType;
    public static REMOVE_TYPE removeType;
    private DataStoreHelper dataStoreHelper;
    List<Geofence> currentGeofences;
    private GeofenceRequester geofenceRequester;
    private GeofenceRemover geofenceRemover;
    private GeofenceTransitionsReceiver geofenceReceiver;
    private IntentFilter intentFilter;
    public static List<String> geofenceIdsToRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geofenceReceiver = new GeofenceTransitionsReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);
        intentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);
        intentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);
        intentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

        dataStoreHelper = new DataStoreHelper(this);
        currentGeofences = dataStoreHelper.getAllGeofences();
        geofenceRequester = new GeofenceRequester(this);
        geofenceRemover = new GeofenceRemover(this);
        setContentView(R.layout.activity_main);
        appSectionPagerAdapter = new TabAdapter(getSupportFragmentManager());

        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff"))); 
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(appSectionPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        
        for (int position = 0; position < appSectionPagerAdapter.getCount(); position++) {
        	Drawable tabIcon = getTabIcon(position);
            actionBar.addTab(actionBar.newTab().setIcon(tabIcon).setTabListener(this));
        }
        viewPager.setCurrentItem(LOG);

    }

    private Drawable getTabIcon(int position) {
    	Bitmap bitmap;
    	Drawable tabIcon=null;
    	switch(position){
    	case LOCATION:
    		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_place_light); 
    		tabIcon = new BitmapDrawable(getResources(),bitmap);
    		break;
    	case LOG:
    		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_email_light); 
    		tabIcon = new BitmapDrawable(getResources(),bitmap);
    		break;
    	case CONTACT:
    		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_person_light); 
    		tabIcon = new BitmapDrawable(getResources(),bitmap);
    		break;
    	}
    	tabIcon.setColorFilter(Color.parseColor("#bbbbbb"), Mode.MULTIPLY);
		return tabIcon;
    }

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (GeofenceUtils.REQUEST_TYPE.ADD == requestType) {
                            geofenceRequester.setInProgressFlag(false);
                            geofenceRequester.addGeofences(currentGeofences);
                        } else if (GeofenceUtils.REQUEST_TYPE.REMOVE == requestType ){
                            geofenceRemover.setInProgressFlag(false);
                            if (GeofenceUtils.REMOVE_TYPE.INTENT == removeType) {
                                geofenceRemover.removeGeofencesByIntent(
                                    geofenceRequester.getRequestPendingIntent());
                            } else {
                                geofenceRemover.removeGeofencesById(geofenceIdsToRemove);
                            }
                        }
                    break;
                    default:
                        Log.d(GeofenceUtils.APPTAG, getString(R.string.no_resolution));
                }
            case 196609:
            case RQS_PICK_CONTACT:
            	if(resultCode == RESULT_OK) {
					Uri contactData = intent.getData();
					Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
					cursor.moveToFirst();
			
					String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
					String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
					if(phoneNumber != null) {
						Contact newContact = new Contact(name,phoneNumber);
						DataStoreHelper dataStoreHelper = new DataStoreHelper(this);
						if(!dataStoreHelper.addContact(newContact)){
							Toast.makeText(this,"Contact already added", Toast.LENGTH_SHORT).show();
						}
						else{
							Toast.makeText(this,"Contact added successfully", Toast.LENGTH_SHORT).show();
							ContactListFragment contactListFragment = new ContactListFragment();
							contactListFragment.listAddedContacts(MainActivity.this);
							ListView contactListView = (ListView) findViewById(R.id.contacts_list);
			    	        contactListView.setAdapter(contactListFragment.getAdapter());
			    	        getActionBar().setSelectedNavigationItem(CONTACT);
						} 
			      }
			      else{
			    	  Toast.makeText(this,"Not a valid Phone Number", Toast.LENGTH_SHORT).show();
			      }
            	}
            	break;
            default:
               Log.d(GeofenceUtils.APPTAG,
                       getString(R.string.unknown_activity_request_code, requestCode));

               break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataStoreHelper = new DataStoreHelper(this);
        currentGeofences = dataStoreHelper.getAllGeofences();
        LocalBroadcastManager.getInstance(this).registerReceiver(geofenceReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.menu, menu);
      return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
      switch (item.getItemId()) {
      
      case R.id.add_contact:
    	intent = new Intent(Intent.ACTION_PICK);
      	intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
      	startActivityForResult(intent, 1);
        break;
      case R.id.clear_all_contacts:
    	  new AlertDialog.Builder(this)
    	  .setMessage("Remove all contacts ?")
    	  .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {

    	      public void onClick(DialogInterface dialog, int whichButton) {
    	    	  dataStoreHelper.deleteAllContacts();
    	    	  ContactListFragment contactListFragment = new ContactListFragment(); 
    	    	  contactListFragment.listAddedContacts(MainActivity.this);
    	    	  ListView contactListView = (ListView) findViewById(R.id.contacts_list);
    	          contactListView.setAdapter(contactListFragment.getAdapter());
    	          getActionBar().setSelectedNavigationItem(CONTACT);
    	      }})
    	   .setNegativeButton(android.R.string.no, null).show();
    	break;
      case R.id.clear_all_logs:
    	  new AlertDialog.Builder(this)
    	  .setMessage("Remove all messages ?")
    	  .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {

    	      public void onClick(DialogInterface dialog, int whichButton) {
    	    	  dataStoreHelper.deleteAllMessageLogs();
    	    	  LogListFragment logListFragment = new LogListFragment(); 
    	    	  logListFragment.listLogs(MainActivity.this);
    	    	  ListView logListView = (ListView) findViewById(R.id.message_list);
    	          logListView.setAdapter(logListFragment.getAdapter());
    	          getActionBar().setSelectedNavigationItem(LOG);
    	      }})
    	   .setNegativeButton(android.R.string.no, null).show();
      	break;	
      case R.id.clear_all_locations:
    	  new AlertDialog.Builder(this)
    	  .setMessage("Remove all locations ?")
    	  .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {

    	      public void onClick(DialogInterface dialog, int whichButton) {
    			  deleteGeofences();
    	    	  dataStoreHelper.deleteAllGeofences();
    	    	  dataStoreHelper.deleteAllGeofenceStates();
    	    	  LocationListFragment locationListFragment = new LocationListFragment(); 
    	    	  locationListFragment.listAddedSimpleGeofence(MainActivity.this);
    	    	  ListView contactListView = (ListView) findViewById(R.id.location_list);
    	          contactListView.setAdapter(locationListFragment.getAdapter());
    	          getActionBar().setSelectedNavigationItem(LOCATION);
    	      }})
    	   .setNegativeButton(android.R.string.no, null).show();
      	
      	break;	
        }

      return true;
    }    

    public void deleteGeofences() {
    	ArrayList<String> geofenceIds = new ArrayList<String>();
    	for(Geofence geofence:currentGeofences){
    		geofenceIds.add(geofence.getRequestId());
    	}
    	geofenceIdsToRemove = geofenceIds;
        removeType = GeofenceUtils.REMOVE_TYPE.INTENT;
        if (!GeofenceUtils.servicesConnected(this, getSupportFragmentManager())) {

            return;
        }
        try {
        geofenceRemover.removeGeofencesById(geofenceIdsToRemove);
        } catch (UnsupportedOperationException e) {
           // Toast.makeText(this, R.string.remove_geofences_already_requested_error,
             //           Toast.LENGTH_LONG).show();
        }
    }

    public void onLocationRegisterClicked(View view) {
        requestType = GeofenceUtils.REQUEST_TYPE.ADD;
        if (!GeofenceUtils.servicesConnected(this, getSupportFragmentManager())) {

            return;
        }
        
        LocationListFragment locationListFragment = new LocationListFragment();
    	EditText locationTagText = (EditText)findViewById(R.id.location_tag);
    	LocationHelper locationHelper = new LocationHelper(this);
    	Location location = locationHelper.getLocation(); 
    	
    	SimpleGeofence simpleGeofence = new SimpleGeofence(locationTagText.getText().toString(), location.getLatitude(), location.getLongitude(), 1000, Geofence.NEVER_EXPIRE, Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT);
    	if(registerGeofence(simpleGeofence.toGeofence(), this)) {
    	if(locationListFragment.addSimpleGeofence(simpleGeofence, this)){
    		locationTagText.setText("");
			locationListFragment.listAddedSimpleGeofence(this);
			ListView locationList = (ListView) findViewById(R.id.location_list);
			locationList.setAdapter(locationListFragment.getAdapter());
    		Toast.makeText(this, "Location added successfully", Toast.LENGTH_SHORT).show();
    		}
    	}
                try {
            geofenceRequester.addGeofences(currentGeofences);
        } catch (UnsupportedOperationException e) {
//            Toast.makeText(this, R.string.add_geofences_already_requested_error,
//                        Toast.LENGTH_LONG).show();
        }
    }
    public boolean registerGeofence(Geofence geofence, FragmentActivity context) {
        requestType = GeofenceUtils.REQUEST_TYPE.ADD;
        
        if (!GeofenceUtils.servicesConnected(this, getSupportFragmentManager())) {
            return false;
        }
		
        currentGeofences = dataStoreHelper.getAllGeofences();
        currentGeofences.add(geofence);
        
        try {
        	if(geofenceRequester==null){
            	geofenceRequester = new GeofenceRequester(context);}
            geofenceRequester.addGeofences(currentGeofences);
        } catch (UnsupportedOperationException e) {
            //Toast.makeText(context, "Not added try again", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
  
    
    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {	}
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		viewPager.setCurrentItem(tab.getPosition());
		tab.getIcon().setColorFilter(Color.parseColor("#02798b"), Mode.MULTIPLY);
	}
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction arg1) {	
		tab.getIcon().setColorFilter(Color.parseColor("#bbbbbb"), Mode.MULTIPLY);
	}
}
