import { ref, computed } from 'vue'
import { sellerDataService } from '../api/services/sellerDataService.js'

export function useSellerData() {
  const isLoading = ref(false)
  const errorMessage = ref('')
  const sellerData = ref(null)
  const lastSelectedSellerId = ref(null)

  const hasData = computed(() => sellerData.value !== null && typeof sellerData.value === 'object')
  const hasError = computed(() => errorMessage.value !== null && errorMessage.value.trim().length > 0)

  async function loadSellerData(sellerId) {
    if (!sellerId || sellerId === null || sellerId === undefined) {
      resetState()
      return
    }

    const id = Number(sellerId)
    if (isNaN(id) || id <= 0) {
      errorMessage.value = 'Invalid seller ID'
      return
    }

    lastSelectedSellerId.value = id
    isLoading.value = true
    errorMessage.value = ''
    sellerData.value = null

    try {
      const result = await sellerDataService.fetchSellerSummary(id)
      
      if (result && result.success === true && result.data) {
        sellerData.value = result.data
        errorMessage.value = ''
      } else {
        errorMessage.value = result?.error || 'Failed to load seller data'
        sellerData.value = null
      }
    } catch (err) {
      const errorMsg = err?.message || 'An unexpected error occurred. Please try again.'
      errorMessage.value = errorMsg
      sellerData.value = null
    } finally {
      isLoading.value = false
    }
  }

  function retryLastRequest() {
    if (lastSelectedSellerId.value) {
      loadSellerData(lastSelectedSellerId.value)
    } else {
      resetState()
    }
  }

  function resetState() {
    errorMessage.value = ''
    sellerData.value = null
    isLoading.value = false
    lastSelectedSellerId.value = null
  }

  return {
    isLoading,
    errorMessage,
    sellerData,
    hasData,
    hasError,
    loadSellerData,
    retryLastRequest,
    resetState
  }
}

