package fragments;

import helpers.database.DataStoreHelper;
import helpers.geofence.GeofenceRemover;

import java.util.ArrayList;
import java.util.List;

import models.SimpleGeofence;
import utils.GeofenceUtils;
import activities.MainActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.spot.R;

public class LocationListFragment extends Fragment {

	public static final String ARG_SECTION_NUMBER = "section_number";
	private ArrayAdapter<String> adapter;
	List<Geofence> currentGeofences;
	View rootView;

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.home, container, false);
		final FragmentActivity context = getActivity();
		listAddedSimpleGeofence(context);
		ListView locationListView = (ListView) rootView
				.findViewById(R.id.location_list);

		TextView addLocationText = (TextView) rootView
				.findViewById(R.id.empty_location_list);
		locationListView.setEmptyView(addLocationText);
		locationListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, final int position, long arg3) {
						removeLocationFromList(position, context);
						return true;
					}

				});
		locationListView.setAdapter(adapter);
		return rootView;
	}

	private void removeLocationFromList(final int position,
			final FragmentActivity context) {
		if (adapter.getCount() <= position) {
			listAddedSimpleGeofence(context);
			if (rootView != null) {

				ListView locationListView = (ListView) rootView
						.findViewById(R.id.location_list);
				locationListView.setAdapter(adapter);

			}
		}
		final String location = adapter.getItem(position);
		new AlertDialog.Builder(context)
				.setMessage("Remove location " + location + " ?")
				.setPositiveButton(R.string.remove,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								DataStoreHelper dataStoreHelper = new DataStoreHelper(
										context);
								dataStoreHelper.deleteSimpleGeofence(location);
								adapter.remove(adapter.getItem(position));
								adapter.notifyDataSetChanged();
								removeGeofence(location, context);
							}
						}).setNegativeButton(android.R.string.no, null).show();

	}

	private void removeGeofence(String geofenceId, FragmentActivity context) {
		MainActivity.removeType = GeofenceUtils.REMOVE_TYPE.INTENT;
		if (!GeofenceUtils.servicesConnected(context)) {

			return;
		}
		try {
			GeofenceRemover geofenceRemover = new GeofenceRemover(context);
			ArrayList<String> geofenceList = new ArrayList<String>();
			geofenceList.add(geofenceId);
			MainActivity.geofenceIdsToRemove = geofenceList;
			geofenceRemover.removeGeofencesById(geofenceList);
		} catch (UnsupportedOperationException e) { }
	}

	public void listAddedSimpleGeofence(final FragmentActivity context) {
		DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
		ArrayList<String> locationList = dataStoreHelper
				.getAllSimpleGeofences();
		adapter = new ArrayAdapter<String>(context, R.layout.location_list,
				R.id.location_description, locationList);
	}

	public boolean addSimpleGeofence(SimpleGeofence simpleGeofence,
			FragmentActivity context) {
		if (!simpleGeofence.getId().equals("")) {
			DataStoreHelper dataStoreHelper = new DataStoreHelper(context);

			if (!dataStoreHelper.addSimpleGeofence(simpleGeofence)) {
				Toast.makeText(context, "Location description Already Added",
						Toast.LENGTH_SHORT).show();
			} else {
				return true;
			}
		}
		return false;

	}

	public ArrayAdapter<String> getAdapter() {
		return adapter;
	}

}
