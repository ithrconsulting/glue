package com.ithrconsulting.sp.sms;

import java.io.IOException;
import java.util.List;

public interface SmsGateway {
	
	List<SmsResult> send(List<String> msisdns, String text) throws IOException
	;
	
}
