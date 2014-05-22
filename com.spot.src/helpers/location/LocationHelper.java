package helpers.location;

import helpers.database.DataStoreHelper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;

import models.UserPosition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class LocationHelper implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener  {
	Context context;
	public static final long MIN_TIME_BETWEEN_UPDATE = 0l;
	public static final long MIN_DISTANCE_BETWEEN_UPDATE = 0l;
	Location newLocation;

	public LocationHelper(Context context) {
		this.context = context;
		this.newLocation= null;
	}

	public Location getLocation() {
		Location location = null;
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		boolean isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (isNetworkEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, MIN_TIME_BETWEEN_UPDATE,
					MIN_DISTANCE_BETWEEN_UPDATE, this);
			if (locationManager != null) {
				location = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
		} else if (isGPSEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATE,
					MIN_DISTANCE_BETWEEN_UPDATE, this);
			if (locationManager != null) {
				location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}

		}
		if((isGPSEnabled || isNetworkEnabled) && location==null){
			
			while(this.newLocation==null){
	
			}
			//Toast.makeText(context, "Thalaivere thalaivere thalaiva thalaiva thalaivare thalaiavareeeee thalaivareeee thalaiavaree!!! In my slang :P", Toast.LENGTH_LONG).show();
			return this.newLocation;
		} 

		return location;

	}

	@Override
	public void onLocationChanged(Location newLocation) {
		// TODO Auto-generated method stub
		this.newLocation = newLocation;
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	public String getUserPosition() {
		DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
		UserPosition userPosition = dataStoreHelper.getUserPosition();
		if (userPosition != null) {
			return userPosition.getTransitionType() + " "
					+ userPosition.getGeofenceName() + " at "
					+ formatDate(userPosition.getTimestamp());
		}
		return "";
	}

	@SuppressLint("SimpleDateFormat") 
	private String formatDate(Timestamp timestamp) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"hh:mm a, dd MMM yy. ");
		String dateFormat = simpleDateFormat.format(timestamp);
		return dateFormat;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	public boolean isLocationPresent(String locationDescription) {
		DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
		return dataStoreHelper.getSimpleGeofence(locationDescription)!=null; 
	}
}
