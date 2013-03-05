package com.ithrconsulting.sp.sms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.ithrconsulting.sp.model.Transaction;

public class SmsTextCreator {
	private static final Logger LOGGER = Logger.getLogger(SmsTextCreator.class);

	private static final String SYSTEM_PROPERTY_QRCODE_ENDPOINT = "stuff.poc.qrcode.endpoint";
	private static final String SYSTEM_PROPERTY_SMS_TEXT_FILE = "stuff.poc.sms.text.file";

	private static final String DEFAULT_QRCODE_SERVLET_PATH = "/opt/qrcode";
	private static final String QUERY_PARAMETER_QRCODE_TEXT = "qrtext";

	private static final String VARIABLE_SELLER_NAME = createVariableName("SELLER_NAME");
	private static final String VARIABLE_PRODUCT_NAME = createVariableName("PRODUCT_NAME");
	private static final String VARIABLE_BUYER_PIN = createVariableName("BUYER_PIN");
	private static final String VARIABLE_BUYER_QR_CODE = createVariableName("BUYER_QR_CODE");
	private static final String VARIABLE_QRCODE_ENDPOINT = createVariableName("QR_CODE_ENDPOINT");

	public String createSmsText(HttpServletRequest request, Transaction transaction) throws IOException {
		String textTemplate = getSmsTextTemplate();

		String text = substituteVariables(textTemplate, transaction, request);

		return text;
	}

	private String getSmsTextTemplate() throws IOException {
		String template;

		String propertySmsTextFile = System.getProperty(SYSTEM_PROPERTY_SMS_TEXT_FILE);
		if (null != propertySmsTextFile) {
			if (LOGGER.isDebugEnabled()) LOGGER.debug("Using sms text template from file: " + propertySmsTextFile);
			template = getSmsTextTemplateFromFile(propertySmsTextFile);
		} else {
			if (LOGGER.isDebugEnabled()) LOGGER.debug("Using default sms text template");
			template = getDefaultSmsTextTemplate();
		}

		return template;
	}

	private String getSmsTextTemplateFromFile(String filename) throws IOException {
		File file = new File(filename);

		if (!file.exists()) {
			throw new IOException("SMS text file \"" + filename + "\" does not exist!");
		}

		BufferedReader input = new BufferedReader(new FileReader(file));
		String line;
		try {
			StringBuilder sb = new StringBuilder();
			while ((line = input.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			return sb.toString();
		} finally {
			input.close();
		}
	}
	
//	private String readWholeTextFile(String filename) throws IOException {
//		FileInputStream inputStream = new FileInputStream(filename);
//		try {
//			String everything = IOUtils.toString(inputStream);
//			return everything;
//		} finally {
//			inputStream.close();
//		}
//	}

	private String getDefaultSmsTextTemplate() {
		StringBuilder sb = new StringBuilder();

		// sb.append("Your TheCompany.com order of ");
		// sb.append("\"");
		// sb.append(transaction.getProduct().getName());
		// sb.append("\"");
		// sb.append(" has been dispatched.");
		// sb.append("\n\n");

		sb.append("From TheCompany.com:");
		sb.append("\n\n");
		sb.append("Your PIN is ");
		sb.append("${BUYER_PIN}");
		sb.append("\n\n");
		sb.append("${QR_CODE_ENDPOINT}");
		sb.append("\n");

		return sb.toString();
	}

	private String substituteVariables(String textTemplate, Transaction transaction, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String newText = textTemplate;

		newText = substituteSellerName(newText, transaction);
		newText = substituteProductName(newText, transaction);
		newText = substituteBuyerPin(newText, transaction);
		newText = substituteBuyerQRCode(newText, transaction);
		newText = substituteQRCodeEndpoint(newText, transaction, request);

		return newText;
	}

	private String substituteSellerName(String originalText, Transaction transaction) {
		String seller = (null != transaction.getSeller()) ? transaction.getSeller().getName() : "unknown";
		String newText = replaceAll(originalText, VARIABLE_SELLER_NAME, seller);
		return newText;
	}

	private String substituteProductName(String originalText, Transaction transaction) {
		String productName = (null != transaction.getProduct()) ? transaction.getProduct().getName() : "unknown";
		String newText = replaceAll(originalText, VARIABLE_PRODUCT_NAME, productName);
		return newText;
	}

	private String substituteBuyerPin(String originalText, Transaction transaction) {
		String pin = getBuyerPin(transaction);
		String newText = replaceAll(originalText, VARIABLE_BUYER_PIN, pin);
		return newText;
	}

	private String substituteBuyerQRCode(String originalText, Transaction transaction) {
		String qrCode = getBuyerQRCode(transaction);
		String newText = replaceAll(originalText, VARIABLE_BUYER_QR_CODE, qrCode);
		return newText;
	}

	private String substituteQRCodeEndpoint(String originalText, Transaction transaction, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String qrcodeEndpoint = getQRCodeEndpoint(request, transaction);
		String newText = replaceAll(originalText, VARIABLE_QRCODE_ENDPOINT, qrcodeEndpoint);
		return newText;
	}

	private String getQRCodeEndpoint(HttpServletRequest request, Transaction transaction)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();

		sb.append(System.getProperty(SYSTEM_PROPERTY_QRCODE_ENDPOINT, getDefaultQRCodeEndpoint(request)));
		sb.append("?");
		sb.append(QUERY_PARAMETER_QRCODE_TEXT);
		sb.append("=");
		sb.append(URLEncoder.encode(transaction.getBuyerQRCode(), "UTF-8"));

		String endpoint = sb.toString();

		return endpoint;
	}

	private String getBuyerPin(Transaction transaction) {
		return transaction.getBuyerPin();
	}

	private String getBuyerQRCode(Transaction transaction) {
		return transaction.getBuyerQRCode();
	}

	private String getDefaultQRCodeEndpoint(HttpServletRequest request) {
		String fullUrl = request.getRequestURL().toString();
		String baseUrl = fullUrl.replace(request.getRequestURI(), request.getContextPath());
		String endpoint = baseUrl + DEFAULT_QRCODE_SERVLET_PATH;

		return endpoint;
	}

	private String replaceAll(String originalText, String target, String replacement) {
		if ((null == originalText) || (null == target) || (null == replacement)) {
			return originalText;
		}

		String newText = originalText.replace(target, replacement);
		return newText;
	}

	private static String createVariableName(String variableName) {
		return "${" + variableName + "}";
	}
}
