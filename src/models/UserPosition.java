package models;

public class UserPosition {
	private int _id;
	private String geofenceName;
	private String transitionType;
	public UserPosition() {
	}
	public UserPosition(int id, String geofenceName, String transitionType){
		this._id = id;
		this.geofenceName = geofenceName;
		this.transitionType = transitionType;
	}
	public int get_id() {
		return _id;
	}
	public String getGeofenceName() {
		return geofenceName;
	}
	public String getTransitionType() {
		return transitionType;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public void setGeofenceName(String geofenceName) {
		this.geofenceName = geofenceName;
	}
	public void setTransitionType(String transitionType) {
		this.transitionType = transitionType;
	}
}
