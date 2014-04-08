package models;

import com.google.android.gms.location.Geofence;

public class SimpleGeofence {
    private final String _id;
    private final double latitude;
    private final double longitude;
    private final float radius;
    private long expirationDuration;
    private int transitionType;
    public SimpleGeofence(String geofenceId, double latitude, double longitude, float radius, long expiration, int transition) {
        this._id = geofenceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.expirationDuration = expiration;
        this.transitionType = transition;
    }
    public String getId() {
        return _id;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public float getRadius() {
        return radius;
    }
    public long getExpirationDuration() {
        return expirationDuration;
    }
    public int getTransitionType() {
        return transitionType;
    }
    public Geofence toGeofence() {
        return new Geofence.Builder()
                       .setRequestId(getId())
                       .setTransitionTypes(transitionType)
                       .setCircularRegion(
                               getLatitude(),
                               getLongitude(),
                               getRadius())
                       .setExpirationDuration(expirationDuration)
                       .build();
    }
}
