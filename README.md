## Merchant Analytics Dashboard (Vue 3 + Spring Boot)

Performance analytics dashboard for Amazon-style merchants. Modern Vue 3 frontend with Vite consumes a Spring Boot REST API backed by PostgreSQL. The backend implements intelligent caching, and the UI provides real-time alerts based on sales trends and return rates.

### What's here
- **Frontend**: `frontend/` — Vue 3 application with Vite, Axios, and composables architecture. Proxies `/api` requests to the backend.
- **Backend**: `backend/` — Spring Boot REST API with PostgreSQL, repository pattern, DTOs, and Caffeine caching.

---

## Architecture Overview

### Frontend Structure
```
frontend/src/
├── api/
│   ├── config/          # API configuration constants
│   ├── core/            # HTTP client, error handling
│   └── services/        # Business logic services
├── components/          # Vue components
│   ├── MerchantSelector.vue
│   ├── PerformanceMetrics.vue
│   └── NotificationPanel.vue
├── composables/         # Reusable composition functions
│   ├── useMerchantData.js
│   └── useDateTime.js
└── views/
    └── AnalyticsView.vue
```

### Backend Structure
```
backend/src/main/java/com/example/backend/
├── controller/
│   ├── MerchantController.java
│   └── advice/
│       └── GlobalExceptionHandler.java
├── service/
│   └── MerchantService.java
├── repository/
│   └── MerchantRepository.java
├── dto/
│   ├── MerchantSummaryResponse.java
│   └── ErrorResponse.java
├── exception/
│   └── MerchantNotFoundException.java
└── config/
    └── AlertConstants.java
```

---

## Setup Steps

### Prerequisites
- Node.js 18+ and npm
- Java 21
- PostgreSQL 14+

### Backend (Spring Boot)

1. **Create database and user** (matches `backend/src/main/resources/application.properties`):
```bash
createdb demo_db
createuser -P demo_user
# set password: demo_pass
psql -d demo_db -U demo_user -c "GRANT ALL PRIVILEGES ON DATABASE demo_db TO demo_user;"
```

2. **Seed sample data**:
```bash
psql -d demo_db -U demo_user -f backend/sellers.sql
```

3. **Start API server**:
```bash
cd backend
./gradlew bootRun
# API available at: http://localhost:8080
```

### Frontend (Vue + Vite)

1. **Install dependencies and start development server**:
```bash
cd frontend
npm install
npm run dev
# Application available at: http://localhost:3000
# API requests are proxied from /api → http://localhost:8080
```

---

## Features & Implementation Details

### Caching Strategy (Backend)
- Enabled via `@EnableCaching` in `BackendApiApplication.java`
- `@Cacheable(cacheNames = "merchantSummary", key = "#id")` on `GET /api/seller/{id}/summary` endpoint
- Caffeine cache configured in `application.properties`: `spring.cache.caffeine.spec=expireAfterWrite=30s`
- **Effect**: Repeated requests for the same merchant ID within 30 seconds are served from cache, reducing database load

### Alert Generation Logic (Backend)
- SQL aggregates sales data comparing this week vs. last week
- Computes total revenue and return rate percentages
- Alerts are generated in the repository layer when:
  - **Sales dropped by more than 30%** compared to last week
  - **Return rate exceeds 10%**
- Response includes `alerts: string[]` (empty array if no alerts)

### Frontend Architecture
- **API Client**: Modular HTTP client with interceptors, retry logic, and error handling
- **Composables**: Reusable logic for data fetching (`useMerchantData`) and date formatting (`useDateTime`)
- **Components**: 
  - `MerchantSelector` - Dropdown for merchant selection
  - `PerformanceMetrics` - Displays sales, revenue, and return rate metrics
  - `NotificationPanel` - Shows system alerts with severity-based styling
- **Error Handling**: Comprehensive error handling with retry mechanisms and user-friendly messages
- **State Management**: Reactive state management using Vue 3 Composition API

---

## API Documentation

### Endpoint: Get Merchant Summary

#### Request
```http
GET /api/seller/{id}/summary HTTP/1.1
Host: localhost:8080
Accept: application/json
```

#### Success Response (200 OK)
```json
{
  "name": "TechZone",
  "total_sales_this_week": 15,
  "total_revenue_this_week": 750.50,
  "return_rate": 12.5,
  "alerts": [
    "Return rate above 10%"
  ]
}
```

#### Error Responses

**404 Not Found**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Merchant not found with id: 999",
  "timestamp": "2025-01-01T12:00:00",
  "path": "/api/seller/999/summary"
}
```

**500 Internal Server Error**
```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred while processing your request",
  "timestamp": "2025-01-01T12:00:00",
  "path": "/api/seller/1/summary"
}
```

---

## Quick Start

### Start Both Servers

**Terminal 1 - Backend:**
```bash
cd backend
./gradlew bootRun
```
Backend runs on port **8080**

**Terminal 2 - Frontend:**
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on port **3000** (or next available port)

The frontend automatically proxies `/api` requests to `http://localhost:8080`

---

## Troubleshooting

### Common Issues

1. **Network/CORS Errors**
   - Ensure the backend is running on port 8080
   - Check that the Vite proxy configuration is correct in `vite.config.js`

2. **Database Connection Issues**
   - Verify PostgreSQL is running
   - Confirm database credentials in `application.properties`
   - Ensure the database and user exist (see setup steps)

3. **No Data Displayed**
   - Verify seed data was loaded: `psql -d demo_db -U demo_user -f backend/sellers.sql`
   - The application expects merchant IDs 1-3 (see `frontend/src/api/services/merchantDataService.js`)

4. **Cache Not Working**
   - Check that `@EnableCaching` is present in `BackendApiApplication.java`
   - Verify Caffeine dependency in `build.gradle`
   - Cache configuration in `application.properties` should match cache name in controller

### Configuration Files

- **Backend**: `backend/src/main/resources/application.properties`
- **Frontend**: `frontend/vite.config.js`
- **Database Seed**: `backend/sellers.sql`

---

## Technology Stack

### Frontend
- **Vue 3** - Progressive JavaScript framework
- **Vite** - Next-generation frontend build tool
- **Axios** - HTTP client with interceptors
- **Composition API** - Modern Vue 3 reactivity system

### Backend
- **Spring Boot** - Java application framework
- **PostgreSQL** - Relational database
- **Spring JDBC** - Database access layer
- **Caffeine** - High-performance caching library
- **Gradle** - Build automation tool

---

## Code Quality Features

- **Type Safety**: DTOs for structured data transfer
- **Error Handling**: Comprehensive exception handling with standardized error responses
- **Separation of Concerns**: Repository, Service, and Controller layers
- **Caching**: Intelligent response caching for performance
- **Modular Architecture**: Composables and services for reusable logic
- **Validation**: Input validation on both frontend and backend
