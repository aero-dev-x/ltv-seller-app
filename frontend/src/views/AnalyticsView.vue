<template>
  <div class="analytics-view">
    <div class="view-header">
      <div class="header-content">
        <div class="header-main">
          <div class="header-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2.5">
              <path d="M3 3v18h18"/>
              <path d="M18.7 8l-5.1 5.2-2.8-2.7L7 14.3"/>
            </svg>
          </div>
          <div class="header-text">
            <h1 class="view-title">Seller Performance Hub</h1>
            <p class="view-subtitle">Real-time analytics and intelligent alerts</p>
          </div>
        </div>
        <div class="header-meta">
          <div class="meta-label">LAST UPDATED</div>
          <div class="meta-value">{{ currentTime }}</div>
        </div>
      </div>
    </div>

    <div class="view-content">
      <SellerSelector 
        :sellers="sellerList"
        :is-loading="isLoading"
        @seller-selected="handleSellerSelection"
      />

      <div v-if="isLoading" class="loading-container">
        <div class="loading-spinner"></div>
        <div class="loading-text">Loading seller data...</div>
      </div>

      <div v-else-if="hasError" class="error-container">
        <div class="error-icon">❌</div>
        <div class="error-title">Error Loading Data</div>
        <div class="error-message">{{ errorMessage }}</div>
        <button @click="retryLastRequest" class="retry-button">
          Try Again
        </button>
      </div>

      <div v-else-if="hasData" class="success-content">
        <PerformanceMetrics 
          :key="`metrics-${sellerData.sellerId}`"
          :performance-data="sellerData" 
        />
        <NotificationPanel 
          :key="`alerts-${sellerData.sellerId}`"
          :notifications="sellerData.alerts" 
        />
      </div>

      <div v-else class="initial-state">
        <div class="initial-icon">
          <svg width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 3v18h18"/>
            <path d="M18.7 8l-5.1 5.2-2.8-2.7L7 14.3"/>
          </svg>
        </div>
        <div class="initial-title">Get Started</div>
        <div class="initial-message">
          Choose a seller from the selector above to view detailed performance metrics, revenue insights, and system alerts.
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { sellerList } from '../api/services/sellerDataService.js'
import { useSellerData } from '../composables/useSellerData.js'
import { useDateTime } from '../composables/useDateTime.js'
import SellerSelector from '../components/SellerSelector.vue'
import PerformanceMetrics from '../components/PerformanceMetrics.vue'
import NotificationPanel from '../components/NotificationPanel.vue'
import '../styles/components/AnalyticsView.css'

export default {
  name: 'AnalyticsView',
  components: {
    SellerSelector,
    PerformanceMetrics,
    NotificationPanel
  },
  setup() {
    const {
      isLoading,
      errorMessage,
      sellerData,
      hasData,
      hasError,
      loadSellerData,
      retryLastRequest
    } = useSellerData()

    const { currentTime } = useDateTime()

    function handleSellerSelection(sellerId) {
      loadSellerData(sellerId)
    }

    return {
      sellerList,
      isLoading,
      errorMessage,
      sellerData,
      hasData,
      hasError,
      currentTime,
      handleSellerSelection,
      retryLastRequest
    }
  }
}
</script>
