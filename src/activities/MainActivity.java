package activities;

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
import android.graphics.drawable.BitmapDrawable;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.geofence.R;

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

	AppSectionsPagerAdapter appSectionsPagerAdapter;
	ViewPager viewPager;
    
	private REQUEST_TYPE requestType;
    private REMOVE_TYPE mRemoveType;
    private DataStoreHelper dataStoreHelper;
    List<Geofence> currentGeofences;
    private GeofenceRequester geofenceRequester;
    private GeofenceRemover mGeofenceRemover;
   
    private GeofenceTransitionsReceiver geofenceReceiver;

    private IntentFilter mIntentFilter;

    private List<String> mGeofenceIdsToRemove;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geofenceReceiver = new GeofenceTransitionsReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);
        mIntentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

        dataStoreHelper = new DataStoreHelper(this);
        currentGeofences = new ArrayList<Geofence>();
        geofenceRequester = new GeofenceRequester(this);
        mGeofenceRemover = new GeofenceRemover(this);
        setContentView(R.layout.activity_main);
        appSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(appSectionsPagerAdapter);
       
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        
        for (int position = 0; position < appSectionsPagerAdapter.getCount(); position++) {
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
    		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_place); 
    		tabIcon = new BitmapDrawable(getResources(),bitmap);
    		break;
    	case LOG:
    		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_email); 
    		tabIcon = new BitmapDrawable(getResources(),bitmap);
    		break;
    	case CONTACT:
    		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_person); 
    		tabIcon = new BitmapDrawable(getResources(),bitmap);
    		break;
    	}
		return tabIcon;
    }

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	Fragment fragment=null;
        	Bundle args;
            switch (position) {
            case LOCATION:
	            	fragment = new LocationListFragment();
	                args = new Bundle();
	                args.putInt(LocationListFragment.ARG_SECTION_NUMBER, position + 1);
	                fragment.setArguments(args);
	                break;
            case LOG:
	            	fragment = new LogListFragment();
	                args = new Bundle();
	                args.putInt(LogListFragment.ARG_SECTION_NUMBER, position + 1);
	                fragment.setArguments(args);
	                break;
            case CONTACT:
		        	 fragment = new ContactListFragment();
	                 args = new Bundle();
	                 args.putInt(ContactListFragment.ARG_SECTION_NUMBER, position + 1);
	                 fragment.setArguments(args);
	                 break;
	        	 	
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

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
                            mGeofenceRemover.setInProgressFlag(false);
                            if (GeofenceUtils.REMOVE_TYPE.INTENT == mRemoveType) {
                                mGeofenceRemover.removeGeofencesByIntent(
                                    geofenceRequester.getRequestPendingIntent());
                            } else {
                                mGeofenceRemover.removeGeofencesById(mGeofenceIdsToRemove);
                            }
                        }
                    break;
                    default:
                        Log.d(GeofenceUtils.APPTAG, getString(R.string.no_resolution));
                }
            case RQS_PICK_CONTACT:
            	if(resultCode == RESULT_OK) {
					Uri contactData = intent.getData();
					Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
					cursor.moveToFirst();
			
					String normalizedPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
					String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			      
					if(normalizedPhoneNumber != null) {
						Contact newContact = new Contact(name,normalizedPhoneNumber);
						DataStoreHelper dataStoreHelper = new DataStoreHelper(this);
						if(!dataStoreHelper.addContact(newContact)){
							Toast.makeText(this,"Contact Already Added", Toast.LENGTH_SHORT).show();
						}
						else{
							
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
        LocalBroadcastManager.getInstance(this).registerReceiver(geofenceReceiver, mIntentFilter);
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
    	  .setTitle("WARNING")
    	  .setMessage("Do you really want to delete all contacts?")
    	  .setIcon(android.R.drawable.ic_dialog_alert)
    	  .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

    	      public void onClick(DialogInterface dialog, int whichButton) {
    	    	  dataStoreHelper.deleteAllContacts();
    	      }})
    	   .setNegativeButton(android.R.string.no, null).show();
    	break;
      case R.id.clear_all_logs:
    	  new AlertDialog.Builder(this)
    	  .setTitle("WARNING")
    	  .setMessage("Do you really want to delete all messages?")
    	  .setIcon(android.R.drawable.ic_dialog_alert)
    	  .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

    	      public void onClick(DialogInterface dialog, int whichButton) {
    	    	  dataStoreHelper.deleteAllMessageLogs();
    	      }})
    	   .setNegativeButton(android.R.string.no, null).show();
      	break;	
      case R.id.clear_all_locations:
    	  new AlertDialog.Builder(this)
    	  .setTitle("WARNING")
    	  .setMessage("Do you really want to delete all locations?")
    	  .setIcon(android.R.drawable.ic_dialog_alert)
    	  .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

    	      public void onClick(DialogInterface dialog, int whichButton) {
    	    	  dataStoreHelper.deleteAllGeofences();
    	    	  dataStoreHelper.deleteAllGeofenceStates();
    	      }})
    	   .setNegativeButton(android.R.string.no, null).show();
      	
      	break;	
        }

      return true;
    }    

//    @Override
//    protected void onPause() {
//        super.onPause();
//       // dataStoreHelper.storeGeofences(currentGeofences);
//    }

    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d(GeofenceUtils.APPTAG, getString(R.string.play_services_available));
            return true;
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), GeofenceUtils.APPTAG);
            }
            return false;
        }
    }
    public void onUnregisterByPendingIntentClicked(View view) {
        mRemoveType = GeofenceUtils.REMOVE_TYPE.INTENT;
        if (!servicesConnected()) {

            return;
        }
        try {
        mGeofenceRemover.removeGeofencesByIntent(geofenceRequester.getRequestPendingIntent());
        } catch (UnsupportedOperationException e) {
            Toast.makeText(this, R.string.remove_geofences_already_requested_error,
                        Toast.LENGTH_LONG).show();
        }
    }

    public void onRegisterClicked(View view) {
        requestType = GeofenceUtils.REQUEST_TYPE.ADD;
        if (!servicesConnected()) {

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
    	}
    	}
                try {
            geofenceRequester.addGeofences(currentGeofences);
        } catch (UnsupportedOperationException e) {
            Toast.makeText(this, R.string.add_geofences_already_requested_error,
                        Toast.LENGTH_LONG).show();
        }
    }
    public boolean registerGeofence(Geofence geofence, FragmentActivity context) {
        requestType = GeofenceUtils.REQUEST_TYPE.ADD;
        
        if (!servicesConnected()) {
            return false;
        }
		currentGeofences = dataStoreHelper.getAllGeofences();
        currentGeofences.add(geofence);
        try {
        	if(geofenceRequester==null){
            	geofenceRequester = new GeofenceRequester(context);}
            geofenceRequester.addGeofences(currentGeofences);
        } catch (UnsupportedOperationException e) {
            Toast.makeText(context, "Not added try again", Toast.LENGTH_SHORT).show();
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
	}
	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {	}
}
