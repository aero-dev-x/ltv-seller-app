package com.example.backend.controller;

import com.example.backend.service.SellerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.cache.annotation.Cacheable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Map;

@RestController
@RequestMapping("/api/seller")
@Validated
public class SellerController {

	private static final Logger logger = LoggerFactory.getLogger(SellerController.class);
	private final SellerService sellerService;

	@Autowired
	public SellerController(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	@GetMapping("/{id}/summary")
	@Cacheable(cacheNames = "sellerSummary", key = "#id")
	public ResponseEntity<Map<String, Object>> getSellerSummary(
			@PathVariable @NotNull @Positive Long id) {
		logger.debug("Fetching seller summary for id: {}", id);
		
		Map<String, Object> summary = sellerService.getSellerSummary(id);
		
		logger.debug("Successfully retrieved seller summary for id: {}", id);
		return ResponseEntity.ok(summary);
	}
}

