package fragments;

import java.util.ArrayList;
import java.util.List;

import com.breadcrumb.R;
import utils.GeofenceUtils;
import utils.GeofenceUtils.REQUEST_TYPE;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;

import helpers.database.DataStoreHelper;
import helpers.geofence.GeofenceRemover;
import helpers.geofence.GeofenceRequester;
import helpers.location.LocationHelper;
import models.Contact;
import models.SimpleGeofence;
import activities.MainActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class LocationListFragment extends Fragment {
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	private ArrayAdapter<String> adapter; 
	List<Geofence> currentGeofences;
	View rootView;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home, container, false);
        final FragmentActivity context = getActivity();
        listAddedSimpleGeofence(context);
        ListView locationListView = (ListView) rootView.findViewById(R.id.location_list);
        locationListView.setAdapter(adapter);
        TextView addLocationText = (TextView) rootView.findViewById(R.id.empty_location_list);
        locationListView.setEmptyView(addLocationText);
        locationListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,final int position, long arg3) {
            	removeLocationFromList(position, context);
            	return true;
            }

			
        });
        return rootView;
    }
	
	private void removeLocationFromList(final int position, final FragmentActivity context) {
		listAddedSimpleGeofence(context);
		final String location = adapter.getItem(position);
		new AlertDialog.Builder(context)
    	  .setMessage("Remove location "+location+" ?")
    	  .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
    	      public void onClick(DialogInterface dialog, int whichButton) {
    	    	DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
    	    	dataStoreHelper.deleteSimpleGeofence(location);
    	    	adapter.remove(adapter.getItem(position));
    	  		adapter.notifyDataSetChanged();
    	  		
    	  		removeGeofence(location, context);
    	  		
    	  		
    	  		
    	      }})
    	   .setNegativeButton(android.R.string.no, null).show();
		
	}
	private void removeGeofence(String geofenceId, FragmentActivity context){
		MainActivity.removeType = GeofenceUtils.REMOVE_TYPE.INTENT;
		if (!GeofenceUtils.servicesConnected(context, context.getSupportFragmentManager())) {

            return;
        }
        try {
        	GeofenceRemover geofenceRemover = new GeofenceRemover(context);
      		ArrayList<String> geofenceList = new ArrayList<String>();
      		geofenceList.add(geofenceId);
      		MainActivity.geofenceIdsToRemove = geofenceList;
      		geofenceRemover.removeGeofencesById(geofenceList);
        } catch (UnsupportedOperationException e) {
           // Toast.makeText(this, R.string.remove_geofences_already_requested_error,
             //           Toast.LENGTH_LONG).show();
        }
	}
	public void listAddedSimpleGeofence(final FragmentActivity context) {
		context.runOnUiThread(new Runnable(){ 
			public void run(){
		    	DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
				ArrayList<String> locationList= dataStoreHelper.getAllSimpleGeofences();
		        adapter = new ArrayAdapter<String>(context, R.layout.location_list,R.id.location_description,locationList);
			}
		});
	}
    
    public boolean addSimpleGeofence(SimpleGeofence simpleGeofence, FragmentActivity context){
    	if(!simpleGeofence.getId().equals("")) {
	    	DataStoreHelper dataStoreHelper = new DataStoreHelper(context);

	    	if(!dataStoreHelper.addSimpleGeofence(simpleGeofence)){
		    	  Toast.makeText(context,"Location description Already Added", Toast.LENGTH_SHORT).show();
		      }
	    	else{
	    		return true;
	    	}
    	}
    	return false;
    
    }
    
    public ArrayAdapter<String> getAdapter() {
		return adapter;
	}

}
