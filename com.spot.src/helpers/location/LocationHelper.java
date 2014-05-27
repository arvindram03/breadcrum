package helpers.location;

import helpers.database.DataStoreHelper;
import interfaces.LocationChangeNotifier;

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

public class LocationHelper implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener  {
	Context context;
	public static final long MIN_TIME_BETWEEN_UPDATE = 0l;
	public static final long MIN_DISTANCE_BETWEEN_UPDATE = 0l;
	Location newLocation;
	LocationChangeNotifier locationChangeNotifier;
	
	public LocationHelper(Context context) {
		this.context = context;
		this.newLocation= null;
	}

	public void requestLocationUpdate(LocationChangeNotifier locationChangeNotifier) {
		this.locationChangeNotifier = locationChangeNotifier;
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		boolean isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (isNetworkEnabled) {
			locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,  this, null);
		} else if (isGPSEnabled) {
			locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,  this, null);
		} 
	}

	@Override
	public void onLocationChanged(Location newLocation) {
		this.newLocation = newLocation;
		this.locationChangeNotifier.locationChanged(newLocation);
	}

	@Override
	public void onProviderDisabled(String arg0) {

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
	public String formatDate(Timestamp timestamp) {
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
