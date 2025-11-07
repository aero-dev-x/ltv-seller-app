import { HTTP_STATUS_CODES } from '../config/apiConfig.js'

export class ApiError extends Error {
  constructor(message, statusCode, originalError = null) {
    super(message)
    this.name = 'ApiError'
    this.statusCode = statusCode
    this.originalError = originalError
    this.timestamp = new Date().toISOString()
  }

  isNetworkError() {
    return this.statusCode === 0
  }

  isServerError() {
    return this.statusCode >= HTTP_STATUS_CODES.SERVER_ERROR
  }

  isClientError() {
    return this.statusCode >= HTTP_STATUS_CODES.BAD_REQUEST && 
           this.statusCode < HTTP_STATUS_CODES.SERVER_ERROR
  }
}

