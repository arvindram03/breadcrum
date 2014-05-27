package receivers;

import java.util.List;

import utils.GeofenceUtils;

import com.google.android.gms.location.Geofence;

import helpers.database.DataStoreHelper;
import helpers.geofence.GeofenceRequester;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RebootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
		List<Geofence> currentGeofences = dataStoreHelper.getAllGeofences();
		
		if(currentGeofences.size()>0){
			
			GeofenceRequester geofenceRequester = new GeofenceRequester(context);				
			if (GeofenceUtils.servicesConnected(context)) {
				try{
					geofenceRequester.addGeofences(currentGeofences);
					}
					catch(UnsupportedOperationException e){}
			
			}
		}
	}
}
