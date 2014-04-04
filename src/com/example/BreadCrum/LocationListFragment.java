package com.example.BreadCrum;

import helpers.DataStoreHelper;
import helpers.LocationHelper;
import models.LocationTag;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listAddedLocationTags(getActivity());
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home, container, false);
        listAddedLocationTags(getActivity());
        setListAdapter(adapter);
        return rootView;
    }
   
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(adapter);
	}
    private void listAddedLocationTags(FragmentActivity context) {
    	DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
		Cursor cursor = dataStoreHelper.getAllLocationTagsAdapter();
		String[] columns = new String[] { DataStoreHelper.KEY_DESCRIPTION };
        int[] to = new int[] { R.id.location_description };
        adapter = new SimpleCursorAdapter(context, R.layout.location_tag_list, cursor, columns, to);
	}
    
    public boolean addLocationTag(String description,FragmentActivity context){
    	if(!description.equals("")) {
	    	LocationHelper locationHelper = new LocationHelper(context);
	    	Location location = locationHelper.getLocation(); 
	    	
	    	DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
	    	LocationTag locationTag = new LocationTag(description, location.getLatitude(), location.getLongitude());
	    	
	    	if(!dataStoreHelper.addLocationTag(locationTag)){
		    	  Toast.makeText(context,"Location description Already Added", Toast.LENGTH_SHORT).show();
		      }
		      else{
		    	 listAddedLocationTags(context);
		    	 return true;
		      } 
    	}
    	return false;
    
    }
}
