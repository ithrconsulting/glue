package com.ithrconsulting.sp.model;

public class Courier implements Resource {
	private final String id;
	private final String name;
	private int rating;
	private Address address;
	private final String msisdn;

	public Courier(Courier source) {
		this.id = source.getId();
		this.name = source.getName();
		this.rating = source.getRating();
		this.address = source.getAddress();
		this.msisdn = source.getMsisdn();
	}

	public Courier forRating(int rating) {
		Courier courier = new Courier(this);

		courier.rating = rating;

		return courier;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getRating() {
		return rating;
	}

	public Address getAddress() {
		return address;
	}

	public String getMsisdn() {
		return msisdn;
	}

	@Override
	public String toString() {
		return "Courier [id=" + id + ", name=" + name + ", rating=" + rating + ", address=" + address + ", msisdn="
				+ msisdn + "]";
	}
}
