package com.example.backend.repository;

import com.example.backend.dto.SellerSummaryResponse;
import com.example.backend.exception.SellerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.postgresql.jdbc.PgArray;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class SellerRepository {

	private static final Logger logger = LoggerFactory.getLogger(SellerRepository.class);
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public SellerRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private static final String SUMMARY_QUERY = """
		WITH sales_data AS (
			SELECT 
				COUNT(*) FILTER (WHERE date >= CURRENT_DATE - INTERVAL '7 days' AND date < CURRENT_DATE + 1) as this_week_count,
				COUNT(*) FILTER (WHERE date >= CURRENT_DATE - INTERVAL '14 days' AND date < CURRENT_DATE - INTERVAL '7 days') as last_week_count,
				COALESCE(SUM(price * quantity) FILTER (WHERE date >= CURRENT_DATE - INTERVAL '7 days' AND date < CURRENT_DATE + 1), 0) as this_week_revenue,
				COALESCE(SUM(CASE WHEN returned THEN 1 ELSE 0 END) FILTER (WHERE date >= CURRENT_DATE - INTERVAL '7 days' AND date < CURRENT_DATE + 1), 0) as this_week_returns,
				COALESCE(SUM(CASE WHEN returned THEN 1 ELSE 0 END) FILTER (WHERE date >= CURRENT_DATE - INTERVAL '14 days' AND date < CURRENT_DATE - INTERVAL '7 days'), 0) as last_week_returns
			FROM sales
			WHERE seller_id = ? 
			AND date >= CURRENT_DATE - INTERVAL '14 days'
			AND date < CURRENT_DATE + 1
		),
		summary AS (
			SELECT 
				s.name,
				COALESCE(sd.this_week_count, 0) as total_sales_this_week,
				COALESCE(sd.this_week_revenue, 0) as total_revenue_this_week,
				CASE 
					WHEN COALESCE(sd.this_week_count, 0) > 0 THEN ROUND((sd.this_week_returns::decimal / sd.this_week_count * 100), 2) 
					ELSE 0 
				END as return_rate,
				CASE 
					WHEN COALESCE(sd.last_week_count, 0) > 0 THEN ROUND(((sd.this_week_count - sd.last_week_count)::decimal / sd.last_week_count * 100), 2)
					ELSE 0
				END as sales_change_percent
			FROM sellers s
			CROSS JOIN sales_data sd
			WHERE s.id = ?
		)
		SELECT 
			name,
			total_sales_this_week,
			total_revenue_this_week,
			return_rate,
			CASE 
				WHEN ABS(sales_change_percent) > 30 AND sales_change_percent < 0 THEN 'Sales dropped by more than 30% vs last week'
				ELSE NULL
			END as alert_sales_drop,
			CASE 
				WHEN return_rate > 10 THEN 'Return rate above 10%'
				ELSE NULL
			END as alert_return_rate,
			COALESCE(ARRAY_REMOVE(ARRAY[
				CASE 
					WHEN ABS(sales_change_percent) > 30 AND sales_change_percent < 0 THEN 'Sales dropped by more than 30% vs last week'
					ELSE NULL
				END,
				CASE 
					WHEN return_rate > 10 THEN 'Return rate above 10%'
					ELSE NULL
				END
			], NULL), ARRAY[]::text[]) as alerts
		FROM summary
		""";

	public SellerSummaryResponse findSummaryById(Long sellerId) {
		if (sellerId == null || sellerId <= 0) {
			logger.warn("Invalid seller ID provided to repository: {}", sellerId);
			throw new IllegalArgumentException("Seller ID must be a positive integer");
		}

		logger.debug("Executing query for seller ID: {}", sellerId);
		try {
			SellerSummaryResponse result = jdbcTemplate.queryForObject(
				SUMMARY_QUERY, 
				new SellerSummaryRowMapper(), 
				sellerId, 
				sellerId
			);
			
			if (result == null) {
				logger.error("Query returned null result for seller ID: {}", sellerId);
				throw new SellerNotFoundException(sellerId);
			}
			
			logger.debug("Successfully retrieved seller summary for ID: {}", sellerId);
			return result;
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Seller not found with ID: {}", sellerId);
			throw new SellerNotFoundException(sellerId);
		} catch (Exception e) {
			logger.error("Error retrieving seller summary for ID: {}", sellerId, e);
			throw new RuntimeException("Failed to fetch seller summary", e);
		}
	}

	private static class SellerSummaryRowMapper implements RowMapper<SellerSummaryResponse> {
		@Override
		public SellerSummaryResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
			SellerSummaryResponse response = new SellerSummaryResponse();
			
			try {
				response.setName(rs.getString("name"));
				response.setTotalSalesThisWeek(rs.getLong("total_sales_this_week"));
				
				BigDecimal revenue = rs.getBigDecimal("total_revenue_this_week");
				response.setTotalRevenueThisWeek(revenue != null ? revenue : BigDecimal.ZERO);
				
				BigDecimal returnRate = rs.getBigDecimal("return_rate");
				response.setReturnRate(returnRate != null ? returnRate : BigDecimal.ZERO);
				
				response.setAlerts(extractAlerts(rs.getObject("alerts")));
			} catch (SQLException e) {
				logger.error("Error mapping result set row to SellerSummaryResponse", e);
				throw e;
			}
			
			return response;
		}

		private List<String> extractAlerts(Object alertsObj) {
			if (alertsObj == null) {
				return Collections.emptyList();
			}
			
			List<String> alertsList = new ArrayList<>();
			
			if (alertsObj instanceof PgArray) {
				try {
					Object[] alertsArray = (Object[]) ((PgArray) alertsObj).getArray();
					if (alertsArray != null && alertsArray.length > 0) {
						for (Object alert : alertsArray) {
							if (alert != null) {
								String alertString = alert.toString().trim();
								if (!alertString.isEmpty()) {
									alertsList.add(alertString);
								}
							}
						}
					}
				} catch (SQLException e) {
					logger.warn("Error extracting alerts from PostgreSQL array", e);
				}
			}
			
			return alertsList.isEmpty() ? Collections.emptyList() : alertsList;
		}
	}
}

