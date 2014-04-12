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
import helpers.geofence.GeofenceRequester;
import helpers.location.LocationHelper;
import models.SimpleGeofence;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class LocationListFragment extends Fragment {
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	private ArrayAdapter<String> adapter; 
	List<Geofence> currentGeofences;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home, container, false);
        listAddedSimpleGeofence(getActivity());
		ListView locationList = (ListView) rootView.findViewById(R.id.locationList);
        locationList.setAdapter(adapter);
        return rootView;
    }

	public void listAddedSimpleGeofence(FragmentActivity context) {
    	DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
		ArrayList<String> locationList= dataStoreHelper.getAllSimpleGeofences();
        adapter = new ArrayAdapter<String>(context, R.layout.location_tag_list,R.id.location_description,locationList);
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
