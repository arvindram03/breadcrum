package models;

public class LocationTag {
	private int _id;
	private double latitude;
	private double longitude;
	private String description;
	
	
	public LocationTag() {
	
	}
	public LocationTag(String description, double latitude, double longitude) {
		this.description = description;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public int getId() {
		return _id;
	}
	
	public void setId(int id) {
		this._id = id;
	}
	public String getDescription() {
		return description;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
