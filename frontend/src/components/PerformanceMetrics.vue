<template>
  <div class="performance-metrics">
    <div class="metrics-header" v-if="performanceData">
      <div class="header-content">
        <h2 class="metrics-title">{{ performanceData.name }}</h2>
        <span class="seller-badge">ID: {{ performanceData.sellerId }}</span>
      </div>
    </div>

    <div class="metrics-container" v-if="performanceData">
      <div class="metric-item sales-metric">
        <div class="metric-visual">
          <div class="metric-circle sales-circle">
            <div class="circle-content">
              <div class="circle-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                  <path d="M3 3v18h18"/>
                  <path d="M18.7 8l-5.1 5.2-2.8-2.7L7 14.3"/>
                </svg>
              </div>
            </div>
          </div>
          <div class="metric-progress">
            <div class="progress-bar" :style="{ width: `${Math.min((performanceData.totalSales / 50) * 100, 100)}%` }"></div>
          </div>
        </div>
        <div class="metric-info">
          <div class="metric-label">Total Sales</div>
          <div class="metric-value-group">
            <span class="metric-value">{{ performanceData.totalSales }}</span>
            <span class="metric-unit">units</span>
          </div>
          <div class="metric-footer">
            <span class="metric-desc">This week</span>
            <span class="metric-change positive">↑ +12%</span>
          </div>
        </div>
      </div>

      <div class="metric-item revenue-metric">
        <div class="metric-visual">
          <div class="metric-circle revenue-circle">
            <div class="circle-content">
              <div class="circle-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                  <line x1="12" y1="1" x2="12" y2="23"/>
                  <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
                </svg>
              </div>
            </div>
          </div>
          <div class="metric-progress">
            <div class="progress-bar" :style="{ width: `${Math.min((performanceData.totalRevenue / 10000) * 100, 100)}%` }"></div>
          </div>
        </div>
        <div class="metric-info">
          <div class="metric-label">Total Revenue</div>
          <div class="metric-value-group">
            <span class="metric-value">${{ formatCurrency(performanceData.totalRevenue) }}</span>
          </div>
          <div class="metric-footer">
            <span class="metric-desc">This week</span>
            <span class="metric-change positive">↑ +8%</span>
          </div>
        </div>
      </div>

      <div class="metric-item return-metric">
        <div class="metric-visual">
          <div class="metric-circle return-circle">
            <div class="circle-content">
              <div class="circle-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                  <path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8"/>
                  <path d="M21 3v5h-5"/>
                  <path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16"/>
                  <path d="M3 21v-5h5"/>
                </svg>
              </div>
            </div>
          </div>
          <div class="metric-progress">
            <div class="progress-bar" :style="{ width: `${performanceData.returnRate * 1000}%` }"></div>
          </div>
        </div>
        <div class="metric-info">
          <div class="metric-label">Return Rate</div>
          <div class="metric-value-group">
            <span class="metric-value">{{ formatPercentage(performanceData.returnRate) }}</span>
          </div>
          <div class="metric-footer">
            <span class="metric-desc">Overall</span>
            <span class="metric-change" :class="performanceData.returnRate > 0.1 ? 'negative' : 'positive'">
              {{ performanceData.returnRate > 0.1 ? '↓ -2%' : '↑ +1%' }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import '../styles/components/PerformanceMetrics.css'

export default {
  name: 'PerformanceMetrics',
  props: {
    performanceData: {
      type: Object,
      default: null
    }
  },
  methods: {
    formatCurrency(value) {
      return new Intl.NumberFormat('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      }).format(value)
    },
    formatPercentage(value) {
      return new Intl.NumberFormat('en-US', {
        style: 'percent',
        minimumFractionDigits: 1,
        maximumFractionDigits: 1
      }).format(value)
    }
  }
}
</script>
