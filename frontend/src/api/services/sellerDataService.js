import { httpClient, executeWithRetry } from '../core/httpClient.js'
import { ApiError } from '../core/apiError.js'
import { HTTP_STATUS_CODES } from '../config/apiConfig.js'

function validateSellerId(sellerId) {
  if (sellerId === null || sellerId === undefined) {
    throw new Error('Seller ID is required')
  }
  
  const id = Number(sellerId)
  if (isNaN(id) || id <= 0 || !Number.isInteger(id)) {
    throw new Error('Seller ID must be a positive integer')
  }
  
  return id
}

function mapApiResponseToViewModel(apiResponse, sellerId) {
  if (!apiResponse || typeof apiResponse !== 'object') {
    throw new Error('Invalid API response format')
  }

  const validatedId = validateSellerId(sellerId)
  
  return {
    sellerId: validatedId,
    name: String(apiResponse.name || 'Unknown Seller').trim(),
    totalSales: Math.max(0, Number(apiResponse.total_sales_this_week) || 0),
    totalRevenue: Math.max(0, Number(apiResponse.total_revenue_this_week) || 0),
    returnRate: Math.max(0, Math.min(1, Number(apiResponse.return_rate) / 100 || 0)),
    alerts: Array.isArray(apiResponse.alerts) 
      ? apiResponse.alerts.filter(alert => alert != null && String(alert).trim().length > 0)
      : []
  }
}

function formatErrorResponse(error) {
  if (error instanceof ApiError) {
    return {
      success: false,
      error: error.message || 'An error occurred',
      statusCode: error.statusCode || 0,
      isNetworkError: error.isNetworkError(),
      isServerError: error.isServerError(),
      timestamp: error.timestamp || new Date().toISOString()
    }
  }
  
  if (error && error.message && error.message.includes('Seller ID')) {
    return {
      success: false,
      error: error.message,
      statusCode: HTTP_STATUS_CODES.BAD_REQUEST,
      isNetworkError: false,
      isServerError: false,
      timestamp: new Date().toISOString()
    }
  }
  
  return {
    success: false,
    error: 'An unexpected error occurred while processing your request',
    statusCode: 0,
    isNetworkError: true,
    isServerError: false,
    timestamp: new Date().toISOString()
  }
}

export const sellerDataService = {
  async fetchSellerSummary(sellerId, options = {}) {
    try {
      validateSellerId(sellerId)
      
      const requestFn = async () => {
        const response = await httpClient.get(`/seller/${sellerId}/summary`)
        
        if (!response || !response.data) {
          throw new Error('Invalid response from server')
        }
        
        return response.data
      }
      
      const apiData = options.retry !== false 
        ? await executeWithRetry(requestFn)
        : await requestFn()
      
      const transformedData = mapApiResponseToViewModel(apiData, sellerId)
      
      return {
        success: true,
        data: transformedData,
        timestamp: new Date().toISOString()
      }
    } catch (error) {
      return formatErrorResponse(error)
    }
  },

  async verifyApiConnectivity(testSellerId = 1) {
    try {
      const validatedId = validateSellerId(testSellerId)
      
      await httpClient.get(`/seller/${validatedId}/summary`, {
        timeout: 5000,
        validateStatus: (status) => status < 500
      })
      return true
    } catch (error) {
      return false
    }
  }
}

export const sellerList = Object.freeze([
  Object.freeze({ id: 1, name: 'TechZone' }),
  Object.freeze({ id: 2, name: 'GadgetHub' }),
  Object.freeze({ id: 3, name: 'EcoMart' })
])

export default sellerDataService

