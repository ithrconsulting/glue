package com.ithrconsulting.sp.sms;


public class MediaburstSmsResult extends SmsResult {

	private String id;

	public MediaburstSmsResult(String msisdn, int statusCode, String statusText, String id) {
		super(msisdn, statusCode, statusText);
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public String toString() {
		if (getStatusCode() == 0) {
			return "{msisdn="+getMsisdn()+", id="+getId()+"}";
		}
		else {
			return "{msisdn="+getMsisdn()+", error="+getStatusCode()+", errorText="+getStatusText()+"}";
		}
	}
}
