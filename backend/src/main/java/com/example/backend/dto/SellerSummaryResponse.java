package com.example.backend.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SellerSummaryResponse {
	private String name;
	private Long totalSalesThisWeek;
	private BigDecimal totalRevenueThisWeek;
	private BigDecimal returnRate;
	private List<String> alerts;

	public SellerSummaryResponse() {
		this.alerts = new ArrayList<>();
	}

	public SellerSummaryResponse(String name, Long totalSalesThisWeek, 
			BigDecimal totalRevenueThisWeek, BigDecimal returnRate, List<String> alerts) {
		this.name = name;
		this.totalSalesThisWeek = totalSalesThisWeek;
		this.totalRevenueThisWeek = totalRevenueThisWeek != null ? totalRevenueThisWeek : BigDecimal.ZERO;
		this.returnRate = returnRate != null ? returnRate : BigDecimal.ZERO;
		this.alerts = alerts != null ? new ArrayList<>(alerts) : new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getTotalSalesThisWeek() {
		return totalSalesThisWeek != null ? totalSalesThisWeek : 0L;
	}

	public void setTotalSalesThisWeek(Long totalSalesThisWeek) {
		this.totalSalesThisWeek = totalSalesThisWeek;
	}

	public BigDecimal getTotalRevenueThisWeek() {
		return totalRevenueThisWeek != null ? totalRevenueThisWeek : BigDecimal.ZERO;
	}

	public void setTotalRevenueThisWeek(BigDecimal totalRevenueThisWeek) {
		this.totalRevenueThisWeek = totalRevenueThisWeek;
	}

	public BigDecimal getReturnRate() {
		return returnRate != null ? returnRate : BigDecimal.ZERO;
	}

	public void setReturnRate(BigDecimal returnRate) {
		this.returnRate = returnRate;
	}

	public List<String> getAlerts() {
		return alerts != null ? new ArrayList<>(alerts) : new ArrayList<>();
	}

	public void setAlerts(List<String> alerts) {
		this.alerts = alerts != null ? new ArrayList<>(alerts) : new ArrayList<>();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SellerSummaryResponse that = (SellerSummaryResponse) o;
		return Objects.equals(name, that.name) &&
				Objects.equals(totalSalesThisWeek, that.totalSalesThisWeek) &&
				Objects.equals(totalRevenueThisWeek, that.totalRevenueThisWeek) &&
				Objects.equals(returnRate, that.returnRate) &&
				Objects.equals(alerts, that.alerts);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, totalSalesThisWeek, totalRevenueThisWeek, returnRate, alerts);
	}

	@Override
	public String toString() {
		return "SellerSummaryResponse{" +
				"name='" + name + '\'' +
				", totalSalesThisWeek=" + totalSalesThisWeek +
				", totalRevenueThisWeek=" + totalRevenueThisWeek +
				", returnRate=" + returnRate +
				", alerts=" + alerts +
				'}';
	}
}

