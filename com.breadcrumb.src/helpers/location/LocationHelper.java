package helpers.location;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import models.UserPosition;
import helpers.database.DataStoreHelper;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationHelper implements  LocationListener{
	Context context;
	public static final long MIN_TIME_BETWEEN_UPDATE = 0l;
    public static final long MIN_DISTANCE_BETWEEN_UPDATE = 0l;
    public LocationHelper(Context context) {
		this.context = context;
	}
	public Location getLocation() {
        Location location=null;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        if(isNetworkEnabled){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BETWEEN_UPDATE,MIN_DISTANCE_BETWEEN_UPDATE, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        else if (isGPSEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BETWEEN_UPDATE,MIN_DISTANCE_BETWEEN_UPDATE, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

        }
        
        return location;

    }

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
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
		if(userPosition!=null){
			return userPosition.getTransitionType()+" "+userPosition.getGeofenceName()+" at "+formatDate(userPosition.getTimestamp());
		}
		return "";
	}
	
	private String formatDate(Timestamp timestamp) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a, dd MMM yy. ");
		String dateFormat = simpleDateFormat.format(timestamp);
		return dateFormat;
	}
}
