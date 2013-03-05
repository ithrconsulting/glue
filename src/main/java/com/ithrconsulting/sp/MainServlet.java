package com.ithrconsulting.sp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.xydra.restless.Restless;
import org.xydra.restless.RestlessParameter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.ithrconsulting.sp.model.Buyer;
import com.ithrconsulting.sp.model.Courier;
import com.ithrconsulting.sp.model.PhoneCredit;
import com.ithrconsulting.sp.model.PhoneCreditStatusEnum;
import com.ithrconsulting.sp.model.ResourceRepository;
import com.ithrconsulting.sp.model.StatusEnum;
import com.ithrconsulting.sp.model.Transaction;
import com.ithrconsulting.sp.sms.MediaburstSmsGateway;
import com.ithrconsulting.sp.sms.SmsTextCreator;

public class MainServlet extends Restless {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(MainServlet.class);

	private static final Object NULL = new Object();

	private static final String DEFAULT_SMS_KEY = "bfe6db9f5c5a09c856f1a22e6a9dea0f7e4f7154";

	private static final String HEADER_SEND_SMS = "STUFF-SEND-SMS";
	
	private static final String SYSTEM_PROPERTY_SMS_KEY = "stuff.poc.sms.key";

	@SuppressWarnings("serial")
	static final Map<String, List<StatusEnum>> STATUS_FILTER_MAP = new HashMap<String, List<StatusEnum>>() {{
	    put(null,          Arrays.asList(StatusEnum.values()));
	    put("all",         Arrays.asList(StatusEnum.values()));
	    put("open",        Arrays.asList(StatusEnum.New, StatusEnum.OutForDelivery, StatusEnum.Undelivered));
	    put("new",         Arrays.asList(StatusEnum.New));
	    put("inprogress",  Arrays.asList(StatusEnum.OutForDelivery));
	    put("success",     Arrays.asList(StatusEnum.Delivered));
	    put("failure",     Arrays.asList(StatusEnum.Undelivered));
	}};
	
	@SuppressWarnings("serial")
	static final Map<String, List<PhoneCreditStatusEnum>> PHONE_CREDIT_STATUS_FILTER_MAP = new HashMap<String, List<PhoneCreditStatusEnum>>() {{
	    put(null,          Arrays.asList(PhoneCreditStatusEnum.values()));
	    put("all",         Arrays.asList(PhoneCreditStatusEnum.values()));
	    put("used",        Arrays.asList(PhoneCreditStatusEnum.Used));
	    put("unused",      Arrays.asList(PhoneCreditStatusEnum.Unused));
	}};
	
	final private Gson gson;
	final private ResourceRepository transactions;
	final private ResourceRepository couriers;
	final private ResourceRepository buyers;
	final private ResourceRepository phoneCredits;
	
	public MainServlet() {
		super();
		
		gson = defaultGsonBuilder().create();
		
		File systemRoot = new File(System.getProperty("user.home"), ".stuff-poc");
		transactions = new ResourceRepository(new File(systemRoot, "transactions"), gson);
		couriers = new ResourceRepository(new File(systemRoot, "couriers"), gson);
		buyers = new ResourceRepository(new File(systemRoot, "buyers"), gson);
		phoneCredits = new ResourceRepository(new File(systemRoot, "phonecredits"), gson);
	}

	@Override
	public void init(ServletConfig servletConfig) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("loggerFactory", "com.ithrconsulting.sp.Log4jLoggerFactorySPI");
		super.init(new ManagedServletConfig(servletConfig, params));
		
		addGet("/transactions/", "listTransactions", new RestlessParameter("status", null));
		addGet("/transactions/{id}", "getTransaction", new RestlessParameter("id"));
		addPost("/transactions/", "createTransaction", new RestlessParameter("data"));
		addPost("/transactions/{id}", "updateTransaction", new RestlessParameter("id"), new RestlessParameter("data"));
		addDelete("/transactions/{id}", "deleteTransaction", new RestlessParameter("id"));

		addPost("/transactions/{id}/ship", "startDelivery", new RestlessParameter("id"), new RestlessParameter("courierId"), new RestlessParameter("packageQRC", null));
		addPost("/transactions/{id}/notify", "sendNotification", new RestlessParameter("id"));
		addPost("/transactions/{id}/success", "markSuccessfulDelivery", new RestlessParameter("id"), new RestlessParameter("paymentType", null));
		addPost("/transactions/{id}/failure", "markUnsuccessfulDelivery", new RestlessParameter("id"));

		addGet("/couriers/", "listCouriers");
		addGet("/couriers/{id}", "getCourier", new RestlessParameter("id"));
		addPost("/couriers/", "createCourier", new RestlessParameter("data"));
		addPost("/couriers/{id}", "updateCourier", new RestlessParameter("id"), new RestlessParameter("data"));
		addDelete("/couriers/{id}", "deleteCourier", new RestlessParameter("id"));
		addPost("/couriers/{id}/rating/increase", "increaseCourierRating", new RestlessParameter("id"));
		addPost("/couriers/{id}/rating/decrease", "decreaseCourierRating", new RestlessParameter("id"));

		addGet("/buyers/", "listBuyers");
		addGet("/buyers/{id}", "getBuyer", new RestlessParameter("id"));
		addPost("/buyers/", "createBuyer", new RestlessParameter("data"));
		addPost("/buyers/{id}", "updateBuyer", new RestlessParameter("id"), new RestlessParameter("data"));
		addDelete("/buyers/{id}", "deleteBuyer", new RestlessParameter("id"));
		addPost("/buyers/{id}/rating/increase", "increaseBuyerRating", new RestlessParameter("id"));
		addPost("/buyers/{id}/rating/decrease", "decreaseBuyerRating", new RestlessParameter("id"));

		addGet("/phonecredits/", "listPhoneCredits", new RestlessParameter("status", null));
		addGet("/phonecredits/{id}", "getPhoneCredit", new RestlessParameter("id"));
		addPost("/phonecredits/", "reservePhoneCredit", new RestlessParameter("msisdn"), new RestlessParameter("amount"));
		addPost("/phonecredits/{id}/commit", "usePhoneCredit", new RestlessParameter("id"));
		addDelete("/phonecredits/{id}", "deletePhoneCredit", new RestlessParameter("id"));
	}
	
	public static GsonBuilder defaultGsonBuilder() {
		final GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(java.util.Set.class, new InstanceCreator<java.util.Set<Object>>() {
			public Set<Object> createInstance(final Type arg0) {
				return new HashSet<Object>();
			}
		});
		return gb;
	}

	public void listTransactions(HttpServletResponse response, String status) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("listTransactions: status="+status);
	
		Set<Transaction> allTransactions = transactions.list(Transaction.class);
		Set<Transaction> filteredTransactions = filterTransactions(allTransactions, status);
		
		stream(response, filteredTransactions);
	}

	public void getTransaction(HttpServletResponse response, String id) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("getTransaction: id="+id);
		
		stream(response, transactions.get(id, Transaction.class));
	}
	
	public void createTransaction(HttpServletResponse response, String data) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("createTransaction: data="+data);

		Transaction transaction = gson.fromJson(data, Transaction.class);
		validateMsisdn(transaction.getBuyer().getMsisdn());
		validateCourier(transaction.getCourier(), false);
		transaction = transaction.forNew(generateTransactionId());
		
		createBuyerIfNecessary(transaction.getBuyer());
		
		stream(response, transactions.create(transaction));
	}

	public void updateTransaction(HttpServletResponse response, String id, String data) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("updateTransaction: id=" + id + ", data=" + data);

		Transaction transaction = gson.fromJson(data, Transaction.class);
		validateMsisdn(transaction.getBuyer().getMsisdn());
		validateCourier(transaction.getCourier(), false);
		
		if ((null != transaction.getBuyer()) && (null != transaction.getBuyer().getAddress())) {
			transaction = transaction.forBuyer(transaction.addGeolocationToBuyer());
		} else {
			LOGGER.warn("Buyer address missing, could not update geolocation");
		}

		stream(response, transactions.update(transaction));
	}
	
	public void deleteTransaction(HttpServletResponse response, String id) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("deleteTransaction: id=" + id);
		
		stream(response, transactions.delete(id));
	}
	
	public void listCouriers(HttpServletResponse response) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("listCouriers");
	
		stream(response, couriers.list(Courier.class));
	}
	
	public void getCourier(HttpServletResponse response, String id) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("getCourier: id="+id);
		
		stream(response, couriers.get(id, Courier.class));
	}
	
	public void createCourier(HttpServletResponse response, String data) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("createCourier: data="+data);

		Courier courier = gson.fromJson(data, Courier.class);
		validateMsisdn(courier.getMsisdn());
		
		stream(response, couriers.create(courier));
	}
	
	public void updateCourier(HttpServletResponse response, String id, String data) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("updateCourier: id=" + id + ", data=" + data);

		Courier courier = gson.fromJson(data, Courier.class);
		validateMsisdn(courier.getMsisdn());
		
		stream(response, couriers.update(courier));
	}
	
	public void deleteCourier(HttpServletResponse response, String id) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("deleteCourier: id=" + id);
		
		stream(response, couriers.delete(id));
	}
	
	public void increaseCourierRating(HttpServletResponse response, String id) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("increaseCourierRating: id="+id);
		
		Courier courier = increaseRatingForCourier(id);
		
		stream(response, courier);
	}
	
	public void decreaseCourierRating(HttpServletResponse response, String id) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("decreaseCourierRating: id="+id);
		
		Courier courier = decreaseRatingForCourier(id);
		
		stream(response, courier);
	}

	public void listBuyers(HttpServletResponse response) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("listBuyers");
	
		stream(response, buyers.list(Buyer.class));
	}
	
	public void getBuyer(HttpServletResponse response, String id) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("getBuyer: id="+id);
		
		stream(response, buyers.get(id, Buyer.class));
	}
	
	public void createBuyer(HttpServletResponse response, String data) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("createBuyer: data="+data);

		Buyer buyer = gson.fromJson(data, Buyer.class);
		stream(response, buyers.create(buyer));
	}
	
	public void updateBuyer(HttpServletResponse response, String id, String data) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("updateBuyer: id=" + id + ", data=" + data);

		Buyer buyer = gson.fromJson(data, Buyer.class);
		
		stream(response, buyers.update(buyer));
	}
	
	public void deleteBuyer(HttpServletResponse response, String id) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("deleteBuyer: id=" + id);
		
		stream(response, buyers.delete(id));
	}
	
	public void increaseBuyerRating(HttpServletResponse response, String id) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("increaseBuyerRating: id="+id);
		
		Buyer buyer = increaseRatingForBuyer(id);
		
		stream(response, buyer);
	}
	
	public void decreaseBuyerRating(HttpServletResponse response, String id) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("decreaseBuyerRating: id="+id);
		
		Buyer buyer = decreaseRatingForBuyer(id);
		
		stream(response, buyer);
	}
	
	public void startDelivery(HttpServletResponse response, String id, String courierId, String packageQRC) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("startDelivery: id=" + id + ", courierId=" + courierId + ", packageQRC=" + packageQRC);

		Courier courier = couriers.get(courierId, Courier.class);

		validateCourier(courier, true);
		
		Transaction transaction = transactions.get(id, Transaction.class);
		transaction = transaction.forShipping(courier, packageQRC);
		
		stream(response, transactions.update(transaction));
	}

	public void sendNotification(HttpServletRequest request, HttpServletResponse response, String id) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("sendNotification: id="+id);

		Transaction transaction = transactions.get(id, Transaction.class);
		
		sendSms(request, transaction);
	}
	
	public void markSuccessfulDelivery(HttpServletRequest request, HttpServletResponse response, String id, String paymentType) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("markSuccessfulDelivery: id=" + id + ", paymentType=" + paymentType);

		if (null == paymentType) {
			paymentType = "cash";
		}
		
		Transaction transaction = transactions.get(id, Transaction.class);
		transaction = transaction.forStatus(StatusEnum.Delivered.toString());
		transaction = transaction.forPaymentType(paymentType);
		
		increaseRatingForCourier(transaction.getCourier().getId());
		increaseRatingForBuyer(transaction.getBuyer().getId());
		
		stream(response, transactions.update(transaction));
	}

	public void markUnsuccessfulDelivery(HttpServletRequest request, HttpServletResponse response, String id) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("markUnsuccessfulDelivery: id="+id);

		Transaction transaction = transactions.get(id, Transaction.class);
		transaction = transaction.forStatus(StatusEnum.Undelivered.toString());

		decreaseRatingForBuyer(transaction.getBuyer().getId());
		
		stream(response, transactions.update(transaction));
	}

	public void listPhoneCredits(HttpServletResponse response, String status) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("listPhoneCredits: status="+status);
	
		Set<PhoneCredit> allPhoneCredits = phoneCredits.list(PhoneCredit.class);
		Set<PhoneCredit> filteredPhoneCredits = filterPhoneCredits(allPhoneCredits, status);
		
		stream(response, filteredPhoneCredits);
	}

	public void getPhoneCredit(HttpServletResponse response, String id) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("getPhoneCredit: id="+id);
		
		stream(response, phoneCredits.get(id, PhoneCredit.class));
	}
	
	public void reservePhoneCredit(HttpServletResponse response, String msisdn, String amount) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("reservePhoneCredit: msisdn=" + msisdn + ", amount=" + amount);

		validateMsisdn(msisdn);
		
		PhoneCredit phoneCredit = new PhoneCredit(generatePhoneCreditId(), msisdn, amount, PhoneCreditStatusEnum.Unused.toString());
		
		stream(response, phoneCredits.create(phoneCredit));
	}

	public void usePhoneCredit(HttpServletResponse response, String id) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("usePhoneCredit: id=" + id);

		PhoneCredit phoneCredit = phoneCredits.get(id, PhoneCredit.class);
		
		if (PhoneCreditStatusEnum.Used.toString().equals(phoneCredit.getStatus())) {
			throw new RuntimeException("Phone Credit Has Already Being Used!");
		}
		
		phoneCredit = phoneCredit.forStatus(PhoneCreditStatusEnum.Used.toString());

		stream(response, phoneCredits.update(phoneCredit));
	}
	
	public void deletePhoneCredit(HttpServletResponse response, String id) throws Exception {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("deletePhoneCredit: id=" + id);
		
		stream(response, phoneCredits.delete(id));
	}

	private void addEntry(String pathTemplate, String httpMethod, String javaMethodName, RestlessParameter... parameters) {
		addMethod(pathTemplate, httpMethod, this, javaMethodName, false, parameters);
	}
	
	private void addGet(String pathTemplate, String javaMethodName, RestlessParameter ... parameters) {
		addEntry(pathTemplate, "GET", javaMethodName, parameters);
	}

	private void addPost(String pathTemplate, String javaMethodName, RestlessParameter ... parameters) {
		addEntry(pathTemplate, "POST", javaMethodName, parameters);
	}

	private void addDelete(String pathTemplate, String javaMethodName, RestlessParameter ... parameters) {
		addEntry(pathTemplate, "DELETE", javaMethodName, parameters);
	}
	
	private void stream(HttpServletResponse res, Object result) throws IOException {
		if (!res.isCommitted()) {
			res.setContentType("application/json");
			gson.toJson(parseNull(result), res.getWriter());
		}
	}

	private Object parseNull(Object result) {
		return result == null ? NULL : result;
	}

	private void sendSms(HttpServletRequest request, Transaction transaction) throws IOException {
		String msisdn = transaction.getBuyer().getMsisdn();
		validateMsisdn(msisdn);
		List<String> msisdns = Arrays.asList(msisdn);

		String smsText = new SmsTextCreator().createSmsText(request, transaction);

		LOGGER.debug("SMS msisdn: " + msisdn);
		LOGGER.debug("SMS text:\n" + smsText);

		if (null != request.getHeader(HEADER_SEND_SMS)) {
			LOGGER.debug("Sending SMS ...");
			MediaburstSmsGateway smsGateway = new MediaburstSmsGateway(System.getProperty(SYSTEM_PROPERTY_SMS_KEY, DEFAULT_SMS_KEY));
			smsGateway.send(msisdns, smsText);
		} else {
			LOGGER.debug("NOT sending SMS, missing header: " + HEADER_SEND_SMS);
		}
	}

	private void validateCourier(Courier courier, boolean strict) {
		if (null == courier) {
			if (strict) {
				throw new RuntimeException("The courier must be supplied");				
			} else {
				return;
			}
		}
		
		String courierId = courier.getId();
		
		if (strict) {
			if (null == courierId) {
				throw new RuntimeException("The courier id must be supplied");				
			}

			if (!couriers.exists(courierId)) {
				throw new RuntimeException("Unknown courier: " + courier);			
			}
		} else {
			if ((null != courierId) && !couriers.exists(courierId)) {
				throw new RuntimeException("Unknown courier: " + courier);			
			}
		}
	}

	private void validateMsisdn(String msisdn) {
		if ((null == msisdn)|| !msisdn.matches("\\d+")) {
			throw new RuntimeException("Invalid msisdn: " + msisdn);
		}
	}

	private String generateTransactionId() throws Exception {
		String id = transactions.getUniqueName();
		return id;
	}

	private String generatePhoneCreditId() throws Exception {
		String id = phoneCredits.getUniqueName();
		return id;
	}
	
	private void createBuyerIfNecessary(Buyer buyer) throws Exception {
		if (!buyers.exists(buyer.getId())) {
			buyers.create(buyer);
		}
	}
	
	private Set<Transaction> filterTransactions(Set<Transaction> allTransactions, String status) {
		Set<Transaction> transactions = new HashSet<Transaction>();

		List<StatusEnum> includedStatusList = STATUS_FILTER_MAP.get(status);
		if (null == includedStatusList) {
			if (LOGGER.isDebugEnabled()) LOGGER.debug("Unknown status '" + status + "', not filtering transactions.");
			includedStatusList = STATUS_FILTER_MAP.get("all");
		}
		
		for (Transaction transaction : allTransactions) {
			if (isStatusIncluded(includedStatusList, transaction.getStatus())) {
				transactions.add(transaction);
			}
		}
		
		return transactions;
	}

	private boolean isStatusIncluded(List<StatusEnum> list, String status) {
		for (StatusEnum includedStatus : list) {
			if (includedStatus.toString().equals(status)) {
				return true;
			}
		}
		
		return false;
	}
	
	private Buyer increaseRatingForBuyer(String id) throws IOException {
		Buyer buyer = buyers.get(id, Buyer.class);	
		
		buyer = buyer.forRating(buyer.getRating() + 1);
		
		buyers.update(buyer);
		
		return buyer;
	}
	
	private Buyer decreaseRatingForBuyer(String id) throws IOException {
		Buyer buyer = buyers.get(id, Buyer.class);	
		
		buyer = buyer.forRating(buyer.getRating() - 1);
		
		buyers.update(buyer);
		
		return buyer;
	}

	private Courier increaseRatingForCourier(String id) throws IOException {
		Courier courier = couriers.get(id, Courier.class);	
		
		courier = courier.forRating(courier.getRating() + 1);
		
		couriers.update(courier);
		
		return courier;
	}

	private Courier decreaseRatingForCourier(String id) throws IOException {
		Courier courier = couriers.get(id, Courier.class);	
		
		courier = courier.forRating(courier.getRating() - 1);
		
		couriers.update(courier);
		
		return courier;
	}
	
	private Set<PhoneCredit> filterPhoneCredits(Set<PhoneCredit> allPhoneCredits, String status) {
		Set<PhoneCredit> phoneCredits = new HashSet<PhoneCredit>();

		List<PhoneCreditStatusEnum> includedStatusList = PHONE_CREDIT_STATUS_FILTER_MAP.get(status);
		if (null == includedStatusList) {
			if (LOGGER.isDebugEnabled()) LOGGER.debug("Unknown status '" + status + "', not filtering phone credits.");
			includedStatusList = PHONE_CREDIT_STATUS_FILTER_MAP.get("all");
		}
		
		for (PhoneCredit phoneCredit : allPhoneCredits) {
			if (isPhoneCreditStatusIncluded(includedStatusList, phoneCredit.getStatus())) {
				phoneCredits.add(phoneCredit);
			}
		}
		
		return phoneCredits;
	}

	private boolean isPhoneCreditStatusIncluded(List<PhoneCreditStatusEnum> list, String status) {
		for (PhoneCreditStatusEnum includedStatus : list) {
			if (includedStatus.toString().equals(status)) {
				return true;
			}
		}
		
		return false;
	}
}
