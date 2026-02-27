package dpnm.mobiledevice.policy.contextobjects;

public class Location {
	private int latitude;
	private int longitude;
	private int altitude;
	private String name;
	
	public Location() {
		latitude = 0;
		longitude = 0;
		altitude = 0;
		name = null;
	}
	public int getLatitude() {
		return latitude;
	}
	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}
	public int getLongitude() {
		return longitude;
	}
	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}
	public int getAltitude() {
		return altitude;
	}
	public void setAltitude(int altitude) {
		this.altitude = altitude;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
}
