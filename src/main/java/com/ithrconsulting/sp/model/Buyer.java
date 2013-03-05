package com.ithrconsulting.sp.model;

public class Buyer implements Resource {
	private final String id;
	private final String name;
	private final String msisdn;
	private int rating;
	private Address address;

	public Buyer(Buyer source) {
		this.id = source.getId();
		this.name = source.getName();
		this.msisdn = source.getMsisdn();
		this.address = source.getAddress();
		this.rating = source.getRating();
	}
	
	public Buyer forGeoAddress(Address address) {
		Buyer buyer = new Buyer(this);
		
		buyer.address = address;
		
		return buyer;
	}
	
	public Buyer forRating(int rating) {
		Buyer buyer = new Buyer(this);
		
		buyer.rating = rating;
		
		return buyer;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public Address getAddress() {
		return address;
	}

	public int getRating() {
		return rating;
	}
}
