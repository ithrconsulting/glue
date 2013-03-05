package com.ithrconsulting.sp.model;

public enum PhoneCreditStatusEnum {
	Used("Used"),
	Unused("Unused");

	String name;

	PhoneCreditStatusEnum(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
