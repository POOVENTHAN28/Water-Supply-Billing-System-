# API Reference - Water Billing System

## Base URL
```
http://localhost:8080
```

---

## 📄 Authentication APIs

### 1. User Login
**Endpoint**: `POST /api/login`

**Request Body**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response (Success - 200)**:
```json
{
  "success": true,
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "role": "admin",
  "userId": "admin",
  "message": "Login successful"
}
```

**Response (Failure - 401)**:
```json
{
  "success": false,
  "message": "Invalid credentials"
}
```

---

### 2. User Registration
**Endpoint**: `POST /api/register`

**Request Body**:
```json
{
  "userId": "U1234567890",
  "username": "newuser",
  "password": "password123",
  "email": "user@example.com",
  "phone": "9876543210",
  "address": "123 Main Street",
  "role": "user"
}
```

**Response (Success - 200)**:
```json
{
  "success": true,
  "message": "Registration successful"
}
```

**Response (Failure - 400)**:
```json
{
  "success": false,
  "message": "Username already exists"
}
```

---

### 3. User Logout
**Endpoint**: `POST /api/logout`

**Headers**:
```
Session-Id: <sessionId>
```

**Response (200)**:
```json
{
  "message": "Logged out successfully"
}
```

---

## 👤 User Information APIs

### 4. Get User Info
**Endpoint**: `GET /api/user`

**Headers**:
```
Session-Id: 550e8400-e29b-41d4-a716-446655440000
```

**Response (Success - 200)**:
```json
{
  "userId": "user1",
  "username": "john",
  "email": "john@example.com",
  "phone": "9876543210",
  "address": "123 Main St",
  "role": "user"
}
```

**Response (Failure - 401)**:
```json
{
  "message": "Invalid session"
}
```

---

## 🔗 Connection APIs

### 5. Get User Connections
**Endpoint**: `GET /api/connections`

**Headers**:
```
Session-Id: 550e8400-e29b-41d4-a716-446655440000
```

**Response (200)**:
```json
[
  {
    "connectionId": "C001",
    "userId": "user1",
    "connectionType": "domestic",
    "meterNumber": "MTR001",
    "currentReading": 150.0,
    "previousReading": 100.0,
    "connectionDate": "2025-01-15T10:30:00",
    "status": "active",
    "lastUpdated": "2025-01-19T15:45:00",
    "isVerified": true
  }
]
```

---

### 6. Add New Connection
**Endpoint**: `POST /api/connections`

**Headers**:
```
Session-Id: 550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
```

**Request Body**:
```json
{
  "connectionType": "domestic",
  "meterNumber": "MTR002"
}
```

**Response (200)**:
```json
{
  "connectionId": "C1234567890",
  "userId": "user1",
  "connectionType": "domestic",
  "meterNumber": "MTR002",
  "status": "active"
}
```

---

## 📄 Bill APIs

### 7. Get User Bills
**Endpoint**: `GET /api/bills`

**Headers**:
```
Session-Id: 550e8400-e29b-41d4-a716-446655440000
```

**Response (200)**:
```json
[
  {
    "billId": "B1234567890",
    "connectionId": "C001",
    "userId": "user1",
    "unitsConsumed": 50.0,
    "amount": 250.0,
    "billDate": "2025-01-19T15:45:00",
    "dueDate": "2025-02-18T15:45:00",
    "status": "pending",
    "paymentDate": null,
    "generatedBy": "admin"
  }
]
```

---

### 8. Pay Bill
**Endpoint**: `POST /api/bills/pay`

**Headers**:
```
Session-Id: 550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
```

**Request Body**:
```json
{
  "billId": "B1234567890"
}
```

**Response (Success - 200)**:
```json
{
  "message": "Payment successful"
}
```

**Response (Failure - 400)**:
```json
{
  "message": "Invalid bill"
}
```

---

## ⚙️ Admin APIs

### 9. Get System Analytics
**Endpoint**: `GET /api/admin/analytics`

**Headers**:
```
Session-Id: 550e8400-e29b-41d4-a716-446655440000
```

**Response (200)**:
```json
{
  "totalUsers": 5,
  "onlineUsers": 2,
  "totalConnections": 8,
  "activeConnections": 7,
  "totalBills": 24,
  "pendingBills": 3,
  "totalRevenue": 5250.0
}
```

---

### 10. Get All Users
**Endpoint**: `GET /api/admin/users`

**Headers**:
```
Session-Id: 550e8400-e29b-41d4-a716-446655440000
```

**Response (200)**:
```json
[
  {
    "userId": "user1",
    "username": "john",
    "email": "john@example.com",
    "phone": "9876543210",
    "address": "123 Main St",
    "role": "user",
    "isOnline": true
  },
  {
    "userId": "admin",
    "username": "admin",
    "email": "admin@water.com",
    "phone": "1234567890",
    "address": "Admin Office",
    "role": "admin",
    "isOnline": true
  }
]
```

---

### 11. Generate Bill
**Endpoint**: `POST /api/admin/generate-bill`

**Headers**:
```
Session-Id: 550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
```

**Request Body**:
```json
{
  "connectionId": "C001",
  "currentReading": 200.5
}
```

**Response (Success - 200)**:
```json
{
  "success": true,
  "message": "Bill generated successfully",
  "billId": "B1234567890",
  "connectionId": "C001",
  "unitsConsumed": 100.5,
  "amount": 502.5,
  "ratePerUnit": 5.0,
  "dueDate": "2025-02-18T15:45:00"
}
```

**Response (Failure - 404)**:
```json
{
  "success": false,
  "message": "Connection not found"
}
```

**Response (Failure - 503)**:
```json
{
  "success": false,
  "message": "Connection is currently offline. Please try again later."
}
```

**Response (Failure - 400)**:
```json
{
  "success": false,
  "message": "Current reading cannot be less than previous reading"
}
```

---

### 12. Track Bill Generation Progress
**Endpoint**: `GET /api/bill-progress?billId=B1234567890`

**Headers**:
```
Session-Id: 550e8400-e29b-41d4-a716-446655440000
```

**Response (200)**:
```json
{
  "success": true,
  "billId": "B1234567890",
  "progress": 85,
  "status": "in-progress",
  "message": "Creating bill record...",
  "duration": 2500
}
```

**Possible Status Values**:
- `in-progress` - Bill generation is ongoing
- `completed` - Bill generation finished successfully
- `failed` - Bill generation encountered an error

---

## 🔌 Static Content APIs

### 13. Get Home Page
**Endpoint**: `GET /`

**Response**: HTML login page

### 14. Get User Dashboard
**Endpoint**: `GET /user.html`

**Response**: HTML user dashboard

### 15. Get Admin Dashboard
**Endpoint**: `GET /admin.html`

**Response**: HTML admin dashboard

### 16. Get Stylesheet
**Endpoint**: `GET /style.css`

**Response**: CSS stylesheet

---

## 🔑 Rate Per Unit by Connection Type

| Connection Type | Rate per Unit | Usage |
|-----------------|---------------|-------|
| Domestic        | ₹5            | Household |
| Commercial      | ₹10           | Business |
| Industrial      | ₹15           | Factory |

---

## ❌ Common Error Codes

| Code | Message | Cause |
|------|---------|-------|
| 200 | Success | Request successful |
| 400 | Bad Request | Invalid input data |
| 401 | Unauthorized | No valid session ID |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource doesn't exist |
| 405 | Method Not Allowed | Wrong HTTP method |
| 500 | Server Error | Internal server error |
| 503 | Service Unavailable | Connection offline |

---

## 📝 Request Headers Required

### For Protected Endpoints:
```
Session-Id: <your-session-id>
```

### For JSON Requests:
```
Content-Type: application/json
```

---

## 💡 Usage Examples

### Example 1: Complete User Journey

1. **Register**: `POST /api/register`
2. **Login**: `POST /api/login` → Get `sessionId`
3. **Get User Info**: `GET /api/user` with `sessionId`
4. **View Connections**: `GET /api/connections` with `sessionId`
5. **Check Bills**: `GET /api/bills` with `sessionId`
6. **Pay Bill**: `POST /api/bills/pay` with `sessionId`

### Example 2: Admin Bill Generation

1. **Admin Login**: `POST /api/login`
2. **Generate Bill**: `POST /api/admin/generate-bill` with connectionId & reading
3. **Track Progress**: `GET /api/bill-progress?billId=<billId>`
4. **View Analytics**: `GET /api/admin/analytics`

---

## 🔒 Security Notes

1. **Session IDs** expire when user logs out
2. **Admin endpoints** require "admin" role
3. **Bill payments** can only be made by bill owner
4. **Connection reading** cannot decrease
5. **CORS headers** allow cross-origin requests

---

## 📊 Data Models

### User
```java
userId: String
username: String
password: String
email: String
phone: String
address: String
role: "admin" or "user"
registrationDate: LocalDateTime
isOnline: boolean
```

### WaterConnection
```java
connectionId: String
userId: String
connectionType: "domestic", "commercial", "industrial"
meterNumber: String
currentReading: double
previousReading: double
connectionDate: LocalDateTime
status: "active", "suspended", "inactive"
lastUpdated: LocalDateTime
isVerified: boolean
```

### Bill
```java
billId: String
connectionId: String
userId: String
unitsConsumed: double
amount: double
billDate: LocalDateTime
dueDate: LocalDateTime
status: "pending", "paid", "overdue"
paymentDate: LocalDateTime (nullable)
generatedBy: String
```

---

## ✨ Response Format

All JSON responses follow this format:
```json
{
  "success": true/false,
  "message": "Descriptive message",
  "data": {},
  "status": "completed/failed/pending",
  "timestamp": 1234567890
}
```

---

Generated: 2025-01-19
Version: 1.0
Status: ✅ Production Ready
