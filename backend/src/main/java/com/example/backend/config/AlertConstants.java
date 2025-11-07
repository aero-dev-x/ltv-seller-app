package com.example.backend.config;

import java.math.BigDecimal;

public final class AlertConstants {
	private AlertConstants() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static final BigDecimal SALES_DROP_THRESHOLD = new BigDecimal("30.0");
	public static final BigDecimal RETURN_RATE_THRESHOLD = new BigDecimal("10.0");
	public static final int DAYS_IN_WEEK = 7;
	public static final int DAYS_IN_TWO_WEEKS = 14;

	public static final String ALERT_SALES_DROP = "Sales dropped by more than 30% vs last week";
	public static final String ALERT_RETURN_RATE = "Return rate above 10%";
}

