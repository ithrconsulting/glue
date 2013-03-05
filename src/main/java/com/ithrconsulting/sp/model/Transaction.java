package com.ithrconsulting.sp.model;

import com.ithrconsulting.sp.model.geo.Geolocation;
import com.ithrconsulting.sp.model.geo.GeolocationService;

public class Transaction implements Resource {
	private String id;
	private String buyerPin;
	private String packageQRCode;
	private String buyerQRCode;
	private String status;
	private String paymentType;
	private String paymentId;
	private Buyer buyer;
	private Courier courier;
	private final Seller seller;
	private final Product product;

	public Transaction(Transaction source) {
		this.id = source.getId();
		this.buyerPin = source.getBuyerPin();
		this.packageQRCode = source.getPackageQRCode();
		this.buyerQRCode = source.getBuyerQRCode();
		this.status = source.getStatus();
		this.paymentType = source.getPaymentType();
		this.paymentId = source.getPaymentId();
		this.buyer = source.getBuyer();
		this.courier = source.getCourier();
		this.seller = source.getSeller();
		this.product = source.getProduct();
	}

	public String getId() {
		return id;
	}

	public Buyer getBuyer() {
		return buyer;
	}

	public Seller getSeller() {
		return seller;
	}

	public Product getProduct() {
		return product;
	}

	public String getBuyerPin() {
		return buyerPin;
	}

	public String getPackageQRCode() {
		return packageQRCode;
	}

	public String getBuyerQRCode() {
		return buyerQRCode;
	}

	public Courier getCourier() {
		return courier;
	}

	public String getStatus() {
		return status;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public Transaction forNew(String id) throws Exception {
		Transaction transaction = new Transaction(this);

		transaction.id = id;
		transaction.status = StatusEnum.New.toString();
		transaction.buyerPin = generateBuyerPin();
		transaction.packageQRCode = generatePackageQRCode();
		transaction.buyerQRCode = generateBuyerQRCode();
		transaction.buyer = addGeolocationToBuyer();

		return transaction;
	}

	public Transaction forShipping(Courier courier, String packageQRC) throws Exception {
		Transaction transaction = new Transaction(this);

		transaction.status = StatusEnum.OutForDelivery.toString();

		transaction.courier = courier;
		
		if (null != packageQRC) {
			transaction.packageQRCode = packageQRC;
		}

		return transaction;
	}

	public Transaction forStatus(String status) throws Exception {
		Transaction transaction = new Transaction(this);

		transaction.status = status;

		return transaction;
	}

	public Transaction forBuyer(Buyer buyer) throws Exception {
		Transaction transaction = new Transaction(this);

		transaction.buyer = buyer;

		return transaction;
	}

	public Transaction forPaymentType(String paymentType) throws Exception {
		Transaction transaction = new Transaction(this);

		transaction.paymentType = paymentType;

		return transaction;
	}

	public Buyer addGeolocationToBuyer() throws Exception {
		Address currentAddress = buyer.getAddress();

		Geolocation geolocation = new GeolocationService().getGeolocation(currentAddress);
		String latitude = geolocation.getLatitude();
		String longitude = geolocation.getLongitude();

		Address geoAddress = currentAddress.forGeoCoordinates(latitude, longitude);

		Buyer geoBuyer = buyer.forGeoAddress(geoAddress);

		return geoBuyer;
	}

	private String generateBuyerQRCode() {
		String code = new RandomStringGenerator().generate(8);
		return code;
	}

	private String generatePackageQRCode() {
		String code = new RandomStringGenerator().generate(8);
		return code;
	}

	private String generateBuyerPin() {
		String pin = new RandomStringGenerator().generate(6);
		return pin;
	}
}
