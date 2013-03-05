package com.ithrconsulting.sp.model.geo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import com.ithrconsulting.sp.model.Address;

public class GeolocationService {
	private static final Logger LOGGER = Logger.getLogger(GeolocationService.class);

	private static final String URL = "http://maps.googleapis.com/maps/api/geocode/json";

	private static final String QUERY_STRING_PARAMETER_SENSOR = "sensor";
	private static final String QUERY_STRING_PARAMETER_ADDRESS = "address";

	protected static final String DEFAULT_CHARSET = "UTF-8";

	public Geolocation getGeolocation(Address address) throws HttpException, IOException, ParseException {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("Using Geolocation endpoint: " + URL);

		GetMethod method = new GetMethod(URL);

		method.setQueryString(createParams(address));
		
		if (LOGGER.isDebugEnabled()) LOGGER.debug("Query String: " + method.getQueryString());

		HttpClient httpClient = new HttpClient();

		try {
			int responseCode = httpClient.executeMethod(method);

			if (HttpStatus.SC_OK != responseCode) {
				throw new RuntimeException("Geolocation call returned status code [" + responseCode + "]");
			}

			Geolocation geolocation = parseResponse(method);

			if (LOGGER.isDebugEnabled()) LOGGER.debug("Retrieved Geolocation (" + geolocation + ") for address: " + address);
			
			return geolocation;
		} finally {
			method.releaseConnection();
		}
	}

	private Geolocation parseResponse(GetMethod method) throws IOException, ParseException {
		InputStream responseBodyStream = validateResponseBody(method);
		String charset = handleResponseCharacterSet(method);
		BufferedReader rawContentReader = new BufferedReader(new InputStreamReader(responseBodyStream, charset));
		
		JSONObject json = (JSONObject) JSONValue.parseWithException(rawContentReader);
		
		if (LOGGER.isDebugEnabled()) LOGGER.debug("Geolocation response: " + json);
		
		String status = (String) json.get("status");
		if (!"OK".equalsIgnoreCase(status)) {
			throw new RuntimeException("Wrong Geolocation json status: " + status);
		}
		
		JSONArray geoResults = (JSONArray) json.get("results");
		if (geoResults.size() < 1) {
			throw new RuntimeException("Geolocation json does not contain any results");
		}

		JSONObject geoResult = (JSONObject) geoResults.get(0);
		JSONObject geometry = (JSONObject) geoResult.get("geometry");
		JSONObject location = (JSONObject) geometry.get("location");
		
		String latitude = String.valueOf(((Double) location.get("lat")).doubleValue());
		String longitude = String.valueOf(((Double) location.get("lng")).doubleValue());
		
		Geolocation geoLocation = new Geolocation(latitude, longitude);
		
		return geoLocation;
	}

	private InputStream validateResponseBody(final HttpMethod method) {
		InputStream responseBodyStream = null;

		try {
			responseBodyStream = method.getResponseBodyAsStream();
		} catch (IOException ioe) {
			throw new RuntimeException("Error retrieving Geolocation response body as stream (" + ioe.getMessage() + ")", ioe);
		}

		if (null == responseBodyStream) {
			throw new RuntimeException("Geolocation response body is empty");
		}

		return responseBodyStream;
	}

	protected String handleResponseCharacterSet(final HttpMethodBase method) {
		String charset = method.getResponseCharSet();

		if (null == charset) {
			charset = DEFAULT_CHARSET;
			if (LOGGER.isDebugEnabled()) LOGGER.debug("No charset in Geolocation response, using default: " + charset);
		} else {
			if (LOGGER.isDebugEnabled()) LOGGER.debug("Using charset from Geolocation response: " + charset);
		}

		return charset;
	}

	private NameValuePair[] createParams(Address address) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new NameValuePair(QUERY_STRING_PARAMETER_SENSOR, "true"));
		params.add(new NameValuePair(QUERY_STRING_PARAMETER_ADDRESS, address.asGeoString()));

		return params.toArray(new NameValuePair[0]);
	}
}
