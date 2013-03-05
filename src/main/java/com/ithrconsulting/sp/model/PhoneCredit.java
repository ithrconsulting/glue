package com.ithrconsulting.sp.model;

public class PhoneCredit implements Resource {
	private String id;
	private String status;
	private final String msisdn;
	private final String amount;

	public PhoneCredit(String id, String msisdn, String amount, String status) {
		this.id = id;
		this.msisdn = msisdn;
		this.amount = amount;
		this.status = status;
	}

	public PhoneCredit(PhoneCredit source) {
		this.id = source.getId();
		this.msisdn = source.getMsisdn();
		this.amount = source.getAmount();
		this.status = source.getStatus();
	}

	public PhoneCredit forStatus(String status) throws Exception {
		PhoneCredit phoneCredit = new PhoneCredit(this);

		phoneCredit.status = status;

		return phoneCredit;
	}

	public String getId() {
		return id;
	}

	public String getStatus() {
		return status;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public String getAmount() {
		return amount;
	}
}
