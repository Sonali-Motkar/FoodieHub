# 🍔 FoodieHub — Android-Based Food Ordering & Delivery Management System

[![Platform](https://img.shields.io/badge/Platform-Android%20API%2024%2B-brightgreen.svg)](https://developer.android.com)
[![Backend](https://img.shields.io/badge/Backend-Node.js%20%7C%20Express-blue.svg)](https://nodejs.org)
[![Local%20DB](https://img.shields.io/badge/Local%20DB-SQLite-orange.svg)](https://sqlite.org)
[![Cloud%20DB](https://img.shields.io/badge/Cloud%20DB-MongoDB%20Atlas-green.svg)](https://www.mongodb.com/cloud/atlas)
[![License](https://img.shields.io/badge/License-ISC-purple.svg)]()

---

## 📖 Executive Summary
**FoodieHub** is an enterprise-grade, full-stack mobile food ordering and delivery ecosystem built to provide zero-latency local cart responsiveness coupled with scalable cloud order fulfillment. It implements a **Dual-Database Architecture** combining an offline-first **SQLite** client database with a cloud **MongoDB Atlas** master database orchestrated by a **Node.js/Express.js REST API**.

---

## 🏛️ System Architecture

```
 ┌───────────────────────────────────────────────────────────┐
 │                   ANDROID CLIENT (JAVA)                   │
 │                                                           │
 │   ┌───────────────────────┐       ┌───────────────────┐   │
 │   │ Activities & Adapters │       │ DatabaseHelper    │   │
 │   │ (Catalog, Cart, Auth) │◄─────►│ (SQLite Local DB) │   │
 │   └───────────┬───────────┘       └───────────────────┘   │
 └───────────────┼───────────────────────────────────────────┘
                 │ Retrofit HTTP / JSON
                 ▼
 ┌───────────────────────────────────────────────────────────┐
 │                 NODE.JS / EXPRESS BACKEND                 │
 │                                                           │
 │   ┌───────────────┐   ┌───────────────┐   ┌───────────┐   │
 │   │  Auth Routes  │   │  Food Routes  │   │ Orders    │   │
 │   │  (JWT+Bcrypt) │   │  (Filter/Cat) │   │ Pipeline  │   │
 │   └───────┬───────┘   └───────┬───────┘   └─────┬─────┘   │
 └───────────┼───────────────────┼─────────────────┼─────────┘
             │                   │                 │
             ▼                   ▼                 ▼
 ┌───────────────────────────────────────────────────────────┐
 │                MONGODB ATLAS CLUSTER (CLOUD)              │
 │          Collections: users, foods, orders, metrics       │
 └───────────────────────────────────────────────────────────┘
```

### Dual-Database Design Rationale
| Storage Layer | Technology | Physical Location | Purpose & Responsibility |
| :--- | :--- | :--- | :--- |
| **Client Storage** | **SQLite** (`SQLiteOpenHelper`) | Android Internal Storage (`/data/data/com.foodiehub/databases/foodiehub_local.db`) | Instant item stepper (+/-) responsiveness, cart persistence during app restarts, offline safety, active session tokens. |
| **Cloud Storage** | **MongoDB Atlas** | Distributed Cloud Replica Set | Master catalog storage, user authentication records, multi-device order dispatch pipeline, historical analytics. |

---

## ✨ Key Features
1. **Interactive Discovery**: Filter by Category Chips (*Burgers*, *Pizzas*, *Asian*, *Beverages*, *Desserts*) and real-time live keyword search.
2. **Offline-Resilient Shopping Cart**: Powered by SQLite table `cart_items` with automated 5% GST computation and dynamic delivery fee waiver for orders over ₹399.
3. **Price-Preserved Checkout**: Order items freeze `priceAtPurchase` to guarantee invoice integrity regardless of future menu price changes.
4. **Order Status Lifecycle**: Track delivery progress across live state stages:  
   `Placed` ➔ `Confirmed` ➔ `Preparing` ➔ `Out for Delivery` ➔ `Delivered`.
5. **Administrative Portal**: Real-time sales metrics (Total Orders, Pending Orders, Gross Revenue in ₹) and order status progression.

---

## 🛠️ Tech Stack
- **Mobile Frontend**: Android SDK (minSdk 24, compileSdk 34), Java 17, Material 3 Design Components, ConstraintLayout, CardView, RecyclerView.
- **Networking & Media**: Square Retrofit 2.9, OkHttp 3 Logging Interceptor, Google Gson, Bumptech Glide 4.16.
- **Backend API**: Node.js, Express.js, Mongoose ODM, JSON Web Tokens (JWT), Bcrypt.js, CORS.
- **Database**: SQLite (Local Client) + MongoDB Atlas (Production Cloud).

---

## 📁 Repository Structure
```text
FoodieHub/
├── android/                   # Native Android Studio Project
│   ├── app/
│   │   ├── src/main/java/com/foodiehub/
│   │   │   ├── activities/    # Splash, Login, Register, Main, FoodDetail, Cart, etc.
│   │   │   ├── adapters/      # FoodAdapter, CartAdapter, CategoryAdapter, OrderAdapter
│   │   │   ├── database/      # DatabaseHelper (SQLite local database)
│   │   │   ├── models/        # FoodItem, CartItem, Order, User, Category
│   │   │   └── network/       # ApiClient, ApiService, Request/Response DTOs
│   │   └── src/main/res/      # Layouts, Drawables, Values, Colors, Themes
│   ├── build.gradle           # Root Gradle build script
│   └── settings.gradle        # Project settings & dependency resolution
├── backend/                   # Node.js & Express REST API Server
│   ├── config/                # MongoDB Database Connection
│   ├── models/                # Mongoose Schemas (User, Food, Order)
│   ├── routes/                # Express API Route Handlers (auth, foods, orders)
│   ├── package.json           # Node.js dependencies & scripts
│   └── server.js              # Application entry point
├── docs/                      # Academic Documentation & Technical References
│   ├── API_DOCUMENTATION.md   # Complete RESTful API Specifications
│   └── README.md              # Documentation index
└── README.md                  # Project overview & deployment instructions
```

---

## 🚀 Quick Start Guide

### 1. Run the Backend API Server
```bash
cd backend
npm install
npm start
```
*Server runs on `http://localhost:5000` with active health status at `http://localhost:5000/`.*

### 2. Run the Android Mobile App
1. Open **Android Studio**.
2. Select **File ➔ Open** and choose the `android/` directory inside `C:\FoodieHub\`.
3. Let Gradle sync complete.
4. Select your emulator or physical Android phone and click **Run ▶** (`Shift + F10`).

---

## ☁️ Cloud Deployment Guide

### Deploy Backend to Render (Free 24/7 Hosting)
1. Push this repository to your **GitHub** account.
2. Sign in to [Render.com](https://render.com) and click **New ➔ Web Service**.
3. Select your `FoodieHub` repository.
4. Set the **Root Directory** to `backend`.
5. Set the **Build Command** to `npm install` and **Start Command** to `node server.js`.
6. Add the environment variable:
   - `MONGODB_URI`: `mongodb+srv://<username>:<password>@cluster0.mongodb.net/foodiehub?retryWrites=true&w=majority`
7. Click **Create Web Service**. Render provides a live HTTPS URL (e.g. `https://foodiehub-api.onrender.com`).
8. Update `BASE_URL` in `android/.../network/ApiClient.java`:
   ```java
   public static String BASE_URL = "https://foodiehub-api.onrender.com/api/";
   ```

---

## 🔐 Demo Test Accounts
| Role | Email | Password | Access Scope |
| :--- | :--- | :--- | :--- |
| **Customer** | `sonalimotkar001@gmail.com` | `Pass@123` | Discovery, SQLite Cart, Checkout, Order Tracking |
| **Administrator** | `admin@foodiehub.com` | `Admin@2026` | Live Business Metrics, Dispatch Status Updates |

---

## 📜 License
Licensed under the ISC License. All rights reserved.
