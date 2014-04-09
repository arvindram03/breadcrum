package fragments;

import java.util.List;

import utils.GeofenceUtils;
import utils.GeofenceUtils.REQUEST_TYPE;

import com.example.android.geofence.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;

import helpers.database.DataStoreHelper;
import helpers.geofence.GeofenceRequester;
import helpers.location.LocationHelper;
import models.SimpleGeofence;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class LocationListFragment extends ListFragment {
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	private SimpleCursorAdapter adapter; 
	List<Geofence> currentGeofences;
	private GeofenceRequester geofenceRequester;
	private GeofenceUtils.REQUEST_TYPE requestType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listAddedSimpleGeofence(getActivity());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		DataStoreHelper dataStoreHelper = new DataStoreHelper(getActivity());
		currentGeofences = dataStoreHelper.getAllGeofences();
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home, container, false);
        listAddedSimpleGeofence(getActivity());
        geofenceRequester = new GeofenceRequester(getActivity());
        DataStoreHelper dataStoreHelper = new DataStoreHelper(getActivity());
		currentGeofences = dataStoreHelper.getAllGeofences();
        setListAdapter(adapter);
        return rootView;
    }
   
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(adapter);
	}
    private void listAddedSimpleGeofence(FragmentActivity context) {
    	DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
		Cursor cursor = dataStoreHelper.getAllSimpleGeofenceAdapter();
		String[] columns = new String[] { DataStoreHelper.KEY_ID };
        int[] to = new int[] { R.id.location_description };
        adapter = new SimpleCursorAdapter(context, R.layout.location_tag_list, cursor, columns, to);
	}
    
    public boolean addSimpleGeofence(SimpleGeofence simpleGeofence, FragmentActivity context){
    	if(!simpleGeofence.getId().equals("")) {
	    	LocationHelper locationHelper = new LocationHelper(context);
	    	Location location = locationHelper.getLocation(); 
	    	
	    	DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
	    	//SimpleGeofence simpleGeofence = new SimpleGeofence(description, location.getLatitude(), location.getLongitude(),2);
	    	
	    		
	    	if(!dataStoreHelper.addSimpleGeofence(simpleGeofence)){
		    	  Toast.makeText(context,"Location description Already Added", Toast.LENGTH_SHORT).show();
		      }
		      else{
		    	 listAddedSimpleGeofence(context);
		    	 return true;
		      }
	    	
    	}
    	return false;
    
    }

}
