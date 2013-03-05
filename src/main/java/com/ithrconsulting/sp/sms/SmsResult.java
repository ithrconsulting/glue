package com.ithrconsulting.sp.sms;

public class SmsResult {
	private String msisdn;
	private int statusCode;
	private String statusText;
	
	public SmsResult(String msisdn, int statusCode, String statusText) {
		super();
		this.msisdn = msisdn;
		this.statusCode = statusCode;
		this.statusText = statusText;
	}

	public String getMsisdn() {
		return msisdn;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public String getStatusText() {
		return statusText;
	}
	
	public String toString() {
		return "{msisdn="+getMsisdn()+", status="+getStatusCode()+"}";
	}
}
