# 🌐 FoodieHub RESTful API Documentation

This technical reference documents all RESTful HTTP endpoints exposed by the Node.js / Express.js backend for communication with the Android client and administrative interfaces.

---

## Base URL
```text
http://<host-ip>:5000/api
```

---

## 1. Authentication Endpoints

### 1.1 Register Customer Account
- **Endpoint**: `POST /auth/register`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
  ```json
  {
    "name": "Sonali Motkar",
    "email": "sonalimotkar001@gmail.com",
    "password": "Pass@123",
    "phone": "+91 832961****",
    "deliveryAddress": "Flat 402, Green Palms, Kothrud, Pune - 411038"
  }
  ```
- **Response (`201 Created`)**:
  ```json
  {
    "success": true,
    "message": "User registered successfully",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": "654a1b2c3d4e5f6a7b8c9d01",
      "name": "Sonali Motkar",
      "email": "sonalimotkar001@gmail.com",
      "role": "customer"
    }
  }
  ```

### 1.2 User & Admin Login
- **Endpoint**: `POST /auth/login`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
  ```json
  {
    "email": "sonalimotkar001@gmail.com",
    "password": "Pass@123"
  }
  ```
- **Response (`200 OK`)**:
  ```json
  {
    "success": true,
    "message": "Login successful",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": "654a1b2c3d4e5f6a7b8c9d01",
      "name": "Sonali Motkar",
      "email": "sonalimotkar001@gmail.com",
      "role": "customer"
    }
  }
  ```

---

## 2. Food Catalog Endpoints

### 2.1 List All Dishes / Filter by Category
- **Endpoint**: `GET /foods` or `GET /foods?category=Burgers`
- **Response (`200 OK`)**:
  ```json
  {
    "success": true,
    "count": 5,
    "data": [
      {
        "id": "FOD-01",
        "name": "Classic Paneer Tikka Burger",
        "category": "Burgers",
        "price": 189.00,
        "description": "Grilled spiced cottage cheese patty with mint mayo and lettuce.",
        "imageUrl": "https://images.foodiehub.com/paneer_burger.jpg",
        "rating": 4.7,
        "prepTime": "20 mins",
        "isAvailable": true
      }
    ]
  }
  ```

---

## 3. Order Management Endpoints

### 3.1 Place Food Order (Price-Preserving)
- **Endpoint**: `POST /orders`
- **Headers**: 
  - `Content-Type: application/json`
  - `Authorization: Bearer <JWT_TOKEN>`
- **Request Body**:
  ```json
  {
    "items": [
      {
        "foodId": "654a1b2c3d4e5f6a7b8c9d05",
        "foodName": "Classic Paneer Tikka Burger",
        "unitPrice": 189.00,
        "quantity": 2
      },
      {
        "foodId": "654a1b2c3d4e5f6a7b8c9d08",
        "foodName": "Cold Brew Frappe Coffee",
        "unitPrice": 99.00,
        "quantity": 1
      }
    ],
    "deliveryAddress": "Flat 402, Green Palms, Kothrud, Pune - 411038",
    "paymentMode": "Cash on Delivery"
  }
  ```
- **Response (`201 Created`)**:
  ```json
  {
    "success": true,
    "message": "Order placed successfully",
    "orderId": "ORD-2026-001",
    "totalAmount": 500.85,
    "orderStatus": "Placed"
  }
  ```

### 3.2 Update Order Status (Admin Dispatch)
- **Endpoint**: `PATCH /orders/:orderId/status`
- **Request Body**:
  ```json
  {
    "status": "Confirmed"
  }
  ```
- **Allowed Status Stages**:
  `Placed` ➔ `Confirmed` ➔ `Preparing` ➔ `Out for Delivery` ➔ `Delivered` (or `Cancelled`)
