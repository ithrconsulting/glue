package com.ithrconsulting.sp.model.geo;

public class Geolocation {
	private final String latitude;
	private final String longitude;

	public Geolocation(String latitude, String longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	@Override
	public String toString() {
		return "Geolocation [latitude=" + latitude + ", longitude=" + longitude + "]";
	}
}
