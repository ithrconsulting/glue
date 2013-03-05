package com.ithrconsulting.sp.model;

public class Address {
	private static final String GEO_STRING_SEPARATOR = ",";

	private final String street1;
	private final String street2;
	private final String city;
	private final String postcode;
	private final String region;
	private final String country;
	private String latitude;
	private String longitude;

	public Address(Address source) {
		this.street1 = source.getStreet1();
		this.street2 = source.getStreet2();
		this.city = source.getCity();
		this.postcode = source.getPostcode();
		this.region = source.getRegion();
		this.country = source.getCountry();
		this.latitude = source.getLatitude();
		this.longitude = source.getLongitude();
	}

	public Address forGeoCoordinates(String latitude, String longitude) {
		Address address = new Address(this);

		address.latitude = latitude;
		address.longitude = longitude;

		return address;
	}

	public String getStreet1() {
		return street1;
	}

	public String getStreet2() {
		return street2;
	}

	public String getCity() {
		return city;
	}

	public String getPostcode() {
		return postcode;
	}

	public String getRegion() {
		return region;
	}

	public String getCountry() {
		return country;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public String asGeoString() {
		StringBuilder sb = new StringBuilder();

		if (null != street1) {
			sb.append(street1);
			sb.append(GEO_STRING_SEPARATOR);
		}

		if (null != street2) {
			sb.append(street2);
			sb.append(GEO_STRING_SEPARATOR);
		}

		if (null != city) {
			sb.append(city);
			sb.append(GEO_STRING_SEPARATOR);
		}

		if (null != postcode) {
			sb.append(postcode);
			sb.append(GEO_STRING_SEPARATOR);
		}

		if (null != region) {
			sb.append(region);
			sb.append(GEO_STRING_SEPARATOR);
		}

		if (null != country) {
			sb.append(country);
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		return "Address [street1=" + street1 + ", street2=" + street2 + ", city=" + city + ", postcode=" + postcode
				+ ", region=" + region + ", country=" + country + ", latitude=" + latitude + ", longitude=" + longitude
				+ "]";
	}
}
