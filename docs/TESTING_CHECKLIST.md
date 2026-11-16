# 🧪 FoodieHub Testing & Verification Checklist

This document tracks execution of the **16 core test cases** required by your MCA University examination panel. All test cases are systematically organized with inputs, expected outcomes, and verification criteria.

---

## Test Execution Matrix

| Test ID | Test Scenario | Input Test Data | Expected Outcome | Execution Status |
|---|---|---|---|:---:|
| **TC_AUTH_01** | User Registration | Valid Name, unique Email, password (`Pass@123`), phone | HTTP `201 Created`; User persisted in MongoDB Atlas `users` | ⏳ Pending |
| **TC_AUTH_02** | User Login | `sonali.m@gmail.com`, `Pass@123` | HTTP `200 OK`; JWT token saved in SQLite `users_session` | ⏳ Pending |
| **TC_AUTH_03** | Invalid Login | Valid email with incorrect password | HTTP `401 Unauthorized`; Error toast: "Invalid credentials" | ⏳ Pending |
| **TC_MENU_01** | Food Catalog Listing | Navigation to Home Dashboard | GET `/api/foods`; RecyclerView populates dishes with `₹` | ⏳ Pending |
| **TC_MENU_02** | Live Food Search | Search Query: `"Burger"` | Filters list to only dishes matching `"Burger"` | ⏳ Pending |
| **TC_CART_01** | Add to Local Cart | Tap `+ Add` on Paneer Tikka Burger | Row inserted in local SQLite `cart_items` table | ⏳ Pending |
| **TC_CART_02** | Update Cart Quantity | Tap `+` stepper button on cart item | Quantity increments to `2`; subtotal updates in SQLite | ⏳ Pending |
| **TC_CART_03** | Remove Cart Item | Tap trash icon on cart row | Record deleted from local SQLite `cart_items` | ⏳ Pending |
| **TC_CART_04** | Total Calculation | Items subtotal `₹378.00` | Adds 5% GST (`₹18.90`) + Delivery (`₹40.00`) = `₹436.90` | ⏳ Pending |
| **TC_CART_05** | Free Delivery Threshold | Items subtotal `₹477.00` | Delivery fee displays as `FREE` (orders over `₹399.00`) | ⏳ Pending |
| **TC_ORD_01** | Place Food Order | Tap `Place Order` with address | POST `/api/orders`; Saved in MongoDB; SQLite cart cleared | ⏳ Pending |
| **TC_ORD_02** | Customer Order History | Tap Receipt / History icon | GET `/api/orders/user/:id`; Lists historical orders & status | ⏳ Pending |
| **TC_ADM_01** | Admin Authentication | `admin@foodiehub.com`, `Admin@2026` | Admin dashboard loads with operational metric counters | ⏳ Pending |
| **TC_ADM_02** | Add New Food Item | Name: `Farmhouse Supreme Pizza`, `₹299` | POST `/api/foods`; Document inserted in MongoDB `foods` | ⏳ Pending |
| **TC_ADM_03** | Update Food Price | Modify price from `₹299` to `₹319` | PATCH `/api/foods/:id`; Updated in MongoDB Atlas | ⏳ Pending |
| **TC_ADM_04** | Delete Food Item | Admin clicks Delete on item `FOD-05` | DELETE `/api/foods/:id`; Item removed from active menu | ⏳ Pending |
| **TC_ADM_05** | Update Order Status | Advance status: *Placed* ➔ *Confirmed* | PATCH `/api/orders/:id/status`; Propagates to customer view | ⏳ Pending |

---

## 🔍 Pre-Viva Hardware Verification Checklist
- [ ] Run backend server on development workstation: `node server.js` (Verify MongoDB Atlas connection log).
- [ ] Install FoodieHub APK on Android test phone or emulator.
- [ ] Ensure workstation Wi-Fi IP is configured in `ApiClient.java` (e.g., `http://192.168.1.X:5000/api/`).
- [ ] Execute test cases 1 through 16 sequentially.
- [ ] Record test results into Chapter 4 of the Word document.
