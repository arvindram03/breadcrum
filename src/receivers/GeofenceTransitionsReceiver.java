package receivers;

import utils.GeofenceUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.android.geofence.R;

public class GeofenceTransitionsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_ERROR)) {
            handleGeofenceError(context, intent);
        } else if (
                TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_ADDED)
                ||
                TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_REMOVED)) {

            handleGeofenceStatus(context, intent);

        } else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_TRANSITION)) {

            handleGeofenceTransition(context, intent);
        } else {
            Toast.makeText(context, R.string.invalid_action, Toast.LENGTH_LONG).show();
        }
    }
    private void handleGeofenceStatus(Context context, Intent intent) {

    }
    private void handleGeofenceTransition(Context context, Intent intent) {
    }
    private void handleGeofenceError(Context context, Intent intent) {
        String msg = intent.getStringExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS);
        Log.e(GeofenceUtils.APPTAG, msg);
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}