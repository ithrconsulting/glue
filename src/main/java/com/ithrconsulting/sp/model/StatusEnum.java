package com.ithrconsulting.sp.model;

public enum StatusEnum {
	New("New"),
	OutForDelivery("Out For Delivery"),
	Delivered("Delivered"),
	Undelivered("Undelivered");
	
	String name;
	
	StatusEnum(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
