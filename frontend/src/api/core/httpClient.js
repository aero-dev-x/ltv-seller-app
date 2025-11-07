import axios from 'axios'
import { API_SETTINGS, HTTP_STATUS_CODES, ERROR_MESSAGES } from '../config/apiConfig.js'
import { ApiError } from './apiError.js'

function createHttpClient() {
  const client = axios.create({
    baseURL: API_SETTINGS.BASE_URL,
    timeout: API_SETTINGS.TIMEOUT,
    headers: API_SETTINGS.HEADERS
  })

  client.interceptors.request.use(
    (config) => {
      config.metadata = { startTime: Date.now() }
      return config
    },
    (error) => Promise.reject(error)
  )

  client.interceptors.response.use(
    (response) => {
      if (response.config.metadata) {
        const duration = Date.now() - response.config.metadata.startTime
        console.debug(`API request completed in ${duration}ms`)
      }
      return response
    },
    (error) => processApiError(error)
  )

  return client
}

function processApiError(error) {
  if (error.response) {
    const { status, data } = error.response
    const message = data?.message || data?.error || getErrorMessage(status)
    return Promise.reject(new ApiError(message, status, error))
  } else if (error.request) {
    return Promise.reject(
      new ApiError(
        'Unable to reach server. Please verify your network connection and CORS configuration.',
        0,
        error
      )
    )
  } else {
    return Promise.reject(
      new ApiError('Request configuration error occurred', 0, error)
    )
  }
}

function getErrorMessage(statusCode) {
  return ERROR_MESSAGES[statusCode] || ERROR_MESSAGES.DEFAULT
}

async function executeWithRetry(requestFn, maxRetries = API_SETTINGS.RETRY_ATTEMPTS, delay = API_SETTINGS.RETRY_DELAY) {
  let lastError
  
  for (let attempt = 0; attempt <= maxRetries; attempt++) {
    try {
      return await requestFn()
    } catch (error) {
      lastError = error
      
      if (error instanceof ApiError && error.isClientError() && !error.isNetworkError()) {
        throw error
      }
      
      if (attempt < maxRetries) {
        await new Promise(resolve => setTimeout(resolve, delay * (attempt + 1)))
      }
    }
  }
  
  throw lastError
}

export const httpClient = createHttpClient()
export { executeWithRetry }

