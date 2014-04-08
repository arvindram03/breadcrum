package com.example.android.geofence;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class GeofenceRemover implements
            ConnectionCallbacks,
            OnConnectionFailedListener,
            OnRemoveGeofencesResultListener {
    private Context context;
    private List<String> currentGeofenceIds;
    private LocationClient locationClient;
    private PendingIntent currentIntent;
    private GeofenceUtils.REMOVE_TYPE requestType;
    private boolean inProgress;

    public GeofenceRemover(Context context) {
        this.context = context;
    }

    public void setInProgressFlag(boolean flag) {
        inProgress = flag;
    }

    public boolean getInProgressFlag() {
        return inProgress;
    }

    public void removeGeofencesById(List<String> geofenceIds) throws
        IllegalArgumentException, UnsupportedOperationException {
        if ((null == geofenceIds) || (geofenceIds.size() == 0)) {
            throw new IllegalArgumentException();
        } else {
            if (!inProgress) {
                requestType = GeofenceUtils.REMOVE_TYPE.LIST;
                currentGeofenceIds = geofenceIds;
                requestConnection();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    public void removeGeofencesByIntent(PendingIntent requestIntent) {
        if (!inProgress) {
            requestType = GeofenceUtils.REMOVE_TYPE.INTENT;
            currentIntent = requestIntent;
            requestConnection();
        } else {
            throw new UnsupportedOperationException();
        }
    }
    private void continueRemoveGeofences() {
        switch (requestType) {
            case INTENT :
                locationClient.removeGeofences(currentIntent, this);
                break;
            case LIST :
                locationClient.removeGeofences(currentGeofenceIds, this);
                break;
        }
    }

    private void requestConnection() {
        getLocationClient().connect();
    }

    private GooglePlayServicesClient getLocationClient() {
        if (locationClient == null) {
            locationClient = new LocationClient(context, this, this);
        }
        return locationClient;
    }

    @Override
    public void onRemoveGeofencesByPendingIntentResult(int statusCode,
            PendingIntent requestIntent) {
        Intent broadcastIntent = new Intent();
        if (statusCode == LocationStatusCodes.SUCCESS) {
            Log.d(GeofenceUtils.APPTAG,
                    context.getString(R.string.remove_geofences_intent_success));
            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);
            broadcastIntent.putExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS,
                    context.getString(R.string.remove_geofences_intent_success));
        } else {
            Log.e(GeofenceUtils.APPTAG,
                    context.getString(R.string.remove_geofences_intent_failure, statusCode));
            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);
            broadcastIntent.putExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS,
                    context.getString(R.string.remove_geofences_intent_failure, statusCode));
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
        requestDisconnection();
    }

    @Override
    public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] geofenceRequestIds) {

        Intent broadcastIntent = new Intent();

        String msg;

        if (LocationStatusCodes.SUCCESS == statusCode) {

            msg = context.getString(R.string.remove_geofences_id_success,
                    Arrays.toString(geofenceRequestIds));
            Log.d(GeofenceUtils.APPTAG, msg);

            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED)
                           .addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES)
                           .putExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS, msg);

        } else {
            msg = context.getString(
                    R.string.remove_geofences_id_failure,
                    statusCode,
                    Arrays.toString(geofenceRequestIds)
            );
            Log.e(GeofenceUtils.APPTAG, msg);
            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCE_ERROR)
                           .addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES)
                           .putExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS, msg);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
        requestDisconnection();
    }
    private void requestDisconnection() {
        inProgress = false;
        getLocationClient().disconnect();
        if (requestType == GeofenceUtils.REMOVE_TYPE.INTENT) {
            currentIntent.cancel();
        }

    }

    @Override
    public void onConnected(Bundle arg0) {
        Log.d(GeofenceUtils.APPTAG, context.getString(R.string.connected));
        continueRemoveGeofences();
    }
    @Override
    public void onDisconnected() {
        inProgress = false;
        Log.d(GeofenceUtils.APPTAG, context.getString(R.string.disconnected));
        locationClient = null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        inProgress = false;
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult((Activity) context,
                    GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (SendIntentException e) {
                e.printStackTrace();
            }
        } else {

            Intent errorBroadcastIntent = new Intent(GeofenceUtils.ACTION_CONNECTION_ERROR);
            errorBroadcastIntent.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES)
                                .putExtra(GeofenceUtils.EXTRA_CONNECTION_ERROR_CODE,
                                        connectionResult.getErrorCode());
            LocalBroadcastManager.getInstance(context).sendBroadcast(errorBroadcastIntent);
        }
    }
}
