package com.ithrconsulting.sp.sms;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

/*
 * This is throwaway code, as I am quite sure Vodafone has its own
 * SMS gateway :)
 */
public class MediaburstSmsGateway implements SmsGateway {

	private static final Logger LOG = Logger.getLogger(MediaburstSmsGateway.class);

	private static final String UTF_8 = "UTF-8";
	
	private final String key;

	public MediaburstSmsGateway(String key) {
		super();
		this.key = key;
	}

	@Override
	public List<SmsResult> send(List<String> msisdns, String text) throws IOException {

		List<SmsResult> result;

		GetMethod method = new GetMethod(composeURL(msisdns, text));
		HttpClient client = new HttpClient();
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode == HttpStatus.SC_OK) {
				String resultText = method.getResponseBodyAsString();
				result = parseResultFrom(msisdns, resultText);
				LOG.info("REAL SMS sent to "+msisdns+" - result: "+result);
			} else {
				throw new IOException("Service returned status code "+statusCode+": "+method.getStatusText());
			}
		} finally {
			method.releaseConnection();
		}
		
		return result;
	}

	List<SmsResult> parseResultFrom(List<String> msisdns, String text) throws IOException {
		
		List<SmsResult> result = null;

		Scanner scanner = new Scanner(text);
		if (scanner.check("Error")) {
			result = parseGlobalError(scanner, msisdns);
		}
		else {
			result = parseResults(scanner);
		}
		
		return result;
	}

	private List<SmsResult> parseGlobalError(Scanner scanner, List<String> msisdns) throws IOException {
		scanner.skip("Error");
		int statusCode = Integer.parseInt(scanner.next());
		String statusText = scanner.toEndOfLine();
		
		List<SmsResult> result = new ArrayList<SmsResult>(msisdns.size());
		for (String msisdn : msisdns) {
			result.add(new MediaburstSmsResult(msisdn, statusCode, statusText, null));
		}
		
		return result;
	}

	public List<SmsResult> parseResults(Scanner scanner) throws IOException {
		List<SmsResult> result = new ArrayList<SmsResult>();
		while(scanner.hasMoreTokens()) {
			int statusCode = 0;
			String statusText = "";
			String id = null;
			String msisdn;
			
			scanner.skip("To");
			msisdn = scanner.next();

			if (scanner.check("ID") == true) {
				scanner.skip("ID");
				id = scanner.next();
			} else {
				scanner.skip("Error");
				statusCode = Integer.parseInt(scanner.next());
				statusText = scanner.toEndOfLine();
			}
				
			result.add(new MediaburstSmsResult(msisdn, statusCode, statusText, id));
		}
		return result;
	}

	private String composeURL(List<String> msisdns, String text) throws UnsupportedEncodingException {
		StringBuffer url = new StringBuffer();
		url.append("https://api.clockworksms.com/http/send.aspx?");
		url.append("key=");
		url.append(key);
		url.append("&to=");

		for (int i = 0; i < msisdns.size(); i++) {
			if (i>0)
				url.append("%2C");
			url.append(URLEncoder.encode(msisdns.get(i), UTF_8));
		}

		url.append("&content=");
		url.append(URLEncoder.encode(text, UTF_8));
		
		return url.toString();
	}
	
	private static class Scanner {

		private int idx;
		private String text;

		public Scanner(String text) {
			this.idx = 0;
			this.text = text;
		}

		public boolean check(String expected) {
			return peekToken().equals(expected);
		}

		public void skip(String expected) throws IOException {
			if (!consumeToken().equals(expected))
				throw new IOException("Response parsing error - cannot find token "+expected);
		}

		public String next() {
			return consumeToken();
		}

		public String toEndOfLine() {
			int ini = moveToStartOfToken(idx);
			idx = moveToEndOfLine(ini);
			return text.substring(ini, idx).trim();
		}

		public boolean hasMoreTokens() {
			return !peekToken().isEmpty();
		}

		public String peekToken() {
			return nextToken(true);
		}

		public String consumeToken() {
			return nextToken(false);
		}

		public String nextToken(boolean peekOnly) {
			int ini = moveToStartOfToken(idx);
			int end = moveToEndOfToken(ini);
			final String token = text.substring(ini, end);
			if (!peekOnly)
				idx = end;
			return token;
		}

		private int moveToStartOfToken(int from) {
			int i = from;
			while(i < text.length()) {
				char c = text.charAt(i);
				if (c != ':' && !Character.isWhitespace(c))
					break;
				
				i++;
			}

			return i;
		}

		private int moveToEndOfToken(int from) {
			int i = from;
			while(i < text.length()) {
				char c = text.charAt(i);
				if (c == ':' || Character.isWhitespace(c))
					break;
				
				i++;
			}

			return i;
		}

		private int moveToEndOfLine(int from) {
			int i = from;
			while(i < text.length()) {
				char c = text.charAt(i);
				if (c == '\n')
					break;
				
				i++;
			}

			return i;
		}
	}
}
