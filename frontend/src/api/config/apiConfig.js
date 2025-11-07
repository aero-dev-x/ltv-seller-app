export const API_SETTINGS = {
  BASE_URL: '/api',
  TIMEOUT: 20000,
  RETRY_ATTEMPTS: 3,
  RETRY_DELAY: 1000,
  HEADERS: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
}

export const HTTP_STATUS_CODES = {
  NOT_FOUND: 404,
  SERVER_ERROR: 500,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403
}

export const ERROR_MESSAGES = {
  [HTTP_STATUS_CODES.BAD_REQUEST]: 'Invalid request parameters',
  [HTTP_STATUS_CODES.UNAUTHORIZED]: 'Authentication required',
  [HTTP_STATUS_CODES.FORBIDDEN]: 'Access denied',
  [HTTP_STATUS_CODES.NOT_FOUND]: 'Resource not found',
  [HTTP_STATUS_CODES.SERVER_ERROR]: 'Internal server error occurred',
  DEFAULT: 'An unexpected error occurred'
}

