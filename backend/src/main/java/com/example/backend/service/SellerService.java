package com.example.backend.service;

import com.example.backend.dto.SellerSummaryResponse;
import com.example.backend.repository.SellerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class SellerService {

	private static final Logger logger = LoggerFactory.getLogger(SellerService.class);
	private final SellerRepository sellerRepository;

	@Autowired
	public SellerService(SellerRepository sellerRepository) {
		this.sellerRepository = sellerRepository;
	}

	public Map<String, Object> getSellerSummary(Long sellerId) {
		if (sellerId == null || sellerId <= 0) {
			logger.warn("Invalid seller ID provided: {}", sellerId);
			throw new IllegalArgumentException("Seller ID must be a positive integer");
		}

		logger.debug("Retrieving seller summary for ID: {}", sellerId);
		SellerSummaryResponse summary = sellerRepository.findSummaryById(sellerId);
		
		if (summary == null) {
			logger.error("Seller summary is null for ID: {}", sellerId);
			throw new IllegalStateException("Failed to retrieve seller summary");
		}

		Map<String, Object> response = convertToApiResponse(summary);
		logger.debug("Successfully converted seller summary to API response for ID: {}", sellerId);
		
		return response;
	}

	private Map<String, Object> convertToApiResponse(SellerSummaryResponse summary) {
		if (summary == null) {
			logger.warn("Attempted to convert null summary to API response");
			throw new IllegalArgumentException("Summary cannot be null");
		}

		Map<String, Object> response = new HashMap<>();
		response.put("name", summary.getName() != null ? summary.getName() : "Unknown Seller");
		response.put("total_sales_this_week", summary.getTotalSalesThisWeek() != null ? summary.getTotalSalesThisWeek() : 0L);
		response.put("total_revenue_this_week", summary.getTotalRevenueThisWeek() != null ? summary.getTotalRevenueThisWeek() : 0.0);
		response.put("return_rate", summary.getReturnRate() != null ? summary.getReturnRate() : 0.0);
		response.put("alerts", summary.getAlerts() != null ? summary.getAlerts() : new java.util.ArrayList<>());
		
		return response;
	}
}

