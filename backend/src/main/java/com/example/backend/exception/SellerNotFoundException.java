package com.example.backend.exception;

public class SellerNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final Long sellerId;

	public SellerNotFoundException(Long sellerId) {
		super("Seller not found with id: " + sellerId);
		this.sellerId = sellerId;
	}

	public SellerNotFoundException(String message) {
		super(message);
		this.sellerId = null;
	}

	public Long getSellerId() {
		return sellerId;
	}
}

