package com.foodiehub.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.foodiehub.models.CartItem;
import com.foodiehub.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper encapsulates all SQLite client-side storage for FoodieHub:
 * 1. cart_items: Persists active food items in cart across app lifecycle.
 * 2. user_session: Stores the active JWT token and user profile locally.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "foodiehub_local.db";
    private static final int DATABASE_VERSION = 2;

    // Table: cart_items
    public static final String TABLE_CART = "cart_items";
    public static final String COL_CART_ID = "cart_item_id";
    public static final String COL_FOOD_ID = "food_id";
    public static final String COL_FOOD_NAME = "food_name";
    public static final String COL_PRICE = "unit_price";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_SUBTOTAL = "subtotal";
    public static final String COL_IMAGE = "image_url";

    // Table: user_session
    public static final String TABLE_SESSION = "user_session";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_USER_NAME = "user_name";
    public static final String COL_USER_EMAIL = "user_email";
    public static final String COL_USER_PHONE = "user_phone";
    public static final String COL_USER_ADDRESS = "user_address";
    public static final String COL_USER_ROLE = "user_role";
    public static final String COL_JWT_TOKEN = "jwt_token";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ensureTablesExist(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        ensureTablesExist(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
        } catch (Exception e) {
            Log.e(TAG, "Error dropping tables on upgrade", e);
        }
        ensureTablesExist(db);
    }

    private void ensureTablesExist(SQLiteDatabase db) {
        try {
            String createCartSql = "CREATE TABLE IF NOT EXISTS " + TABLE_CART + " ("
                    + COL_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_FOOD_ID + " TEXT NOT NULL, "
                    + COL_FOOD_NAME + " TEXT NOT NULL, "
                    + COL_PRICE + " REAL NOT NULL, "
                    + COL_QUANTITY + " INTEGER NOT NULL, "
                    + COL_SUBTOTAL + " REAL NOT NULL, "
                    + COL_IMAGE + " TEXT);";

            String createSessionSql = "CREATE TABLE IF NOT EXISTS " + TABLE_SESSION + " ("
                    + COL_USER_ID + " TEXT PRIMARY KEY, "
                    + COL_USER_NAME + " TEXT, "
                    + COL_USER_EMAIL + " TEXT, "
                    + COL_USER_PHONE + " TEXT, "
                    + COL_USER_ADDRESS + " TEXT, "
                    + COL_USER_ROLE + " TEXT, "
                    + COL_JWT_TOKEN + " TEXT);";

            db.execSQL(createCartSql);
            db.execSQL(createSessionSql);
        } catch (Exception e) {
            Log.e(TAG, "Error creating tables", e);
        }
    }

    // ==========================================
    // CART OPERATIONS
    // ==========================================

    public synchronized long addToCart(String foodId, String name, double price, int qty, String imgUrl) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ensureTablesExist(db);

            // Check if item already exists
            Cursor cursor = null;
            try {
                cursor = db.query(TABLE_CART, new String[]{COL_CART_ID, COL_QUANTITY},
                        COL_FOOD_ID + " = ?", new String[]{foodId}, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int colCartIdx = cursor.getColumnIndex(COL_CART_ID);
                    int colQtyIdx = cursor.getColumnIndex(COL_QUANTITY);
                    if (colCartIdx != -1 && colQtyIdx != -1) {
                        int cartId = cursor.getInt(colCartIdx);
                        int oldQty = cursor.getInt(colQtyIdx);
                        cursor.close();

                        int newQty = oldQty + qty;
                        ContentValues cv = new ContentValues();
                        cv.put(COL_QUANTITY, newQty);
                        cv.put(COL_SUBTOTAL, newQty * price);
                        return db.update(TABLE_CART, cv, COL_CART_ID + " = ?", new String[]{String.valueOf(cartId)});
                    }
                }
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

            ContentValues cv = new ContentValues();
            cv.put(COL_FOOD_ID, foodId != null ? foodId : "FOD-01");
            cv.put(COL_FOOD_NAME, name != null ? name : "Dish");
            cv.put(COL_PRICE, price);
            cv.put(COL_QUANTITY, qty > 0 ? qty : 1);
            cv.put(COL_SUBTOTAL, price * (qty > 0 ? qty : 1));
            cv.put(COL_IMAGE, imgUrl != null ? imgUrl : "");

            return db.insert(TABLE_CART, null, cv);
        } catch (Exception e) {
            Log.e(TAG, "Error in addToCart", e);
            try {
                SQLiteDatabase db = this.getWritableDatabase();
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
                ensureTablesExist(db);
                ContentValues cv = new ContentValues();
                cv.put(COL_FOOD_ID, foodId != null ? foodId : "FOD-01");
                cv.put(COL_FOOD_NAME, name != null ? name : "Dish");
                cv.put(COL_PRICE, price);
                cv.put(COL_QUANTITY, qty > 0 ? qty : 1);
                cv.put(COL_SUBTOTAL, price * (qty > 0 ? qty : 1));
                cv.put(COL_IMAGE, imgUrl != null ? imgUrl : "");
                return db.insert(TABLE_CART, null, cv);
            } catch (Exception ex) {
                Log.e(TAG, "Failed retry addToCart", ex);
                return -1;
            }
        }
    }

    public synchronized List<CartItem> getAllCartItems() {
        List<CartItem> items = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ensureTablesExist(db);
            cursor = db.rawQuery("SELECT * FROM " + TABLE_CART + " ORDER BY " + COL_CART_ID + " DESC", null);

            if (cursor != null && cursor.moveToFirst()) {
                int colId = cursor.getColumnIndex(COL_CART_ID);
                int colFoodId = cursor.getColumnIndex(COL_FOOD_ID);
                int colName = cursor.getColumnIndex(COL_FOOD_NAME);
                int colPrice = cursor.getColumnIndex(COL_PRICE);
                int colQty = cursor.getColumnIndex(COL_QUANTITY);
                int colImg = cursor.getColumnIndex(COL_IMAGE);

                do {
                    int id = colId != -1 ? cursor.getInt(colId) : 0;
                    String fId = colFoodId != -1 ? cursor.getString(colFoodId) : "";
                    String name = colName != -1 ? cursor.getString(colName) : "";
                    double price = colPrice != -1 ? cursor.getDouble(colPrice) : 0.0;
                    int qty = colQty != -1 ? cursor.getInt(colQty) : 1;
                    String img = colImg != -1 ? cursor.getString(colImg) : "";

                    items.add(new CartItem(id, fId, name, price, qty, img));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getAllCartItems", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return items;
    }

    public synchronized int getCartCount() {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ensureTablesExist(db);
            cursor = db.rawQuery("SELECT SUM(" + COL_QUANTITY + ") FROM " + TABLE_CART, null);
            int count = 0;
            if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) {
                count = cursor.getInt(0);
            }
            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error in getCartCount", e);
            return 0;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    public synchronized int updateQuantity(int cartId, int newQty, double unitPrice) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL_QUANTITY, newQty);
            cv.put(COL_SUBTOTAL, newQty * unitPrice);
            return db.update(TABLE_CART, cv, COL_CART_ID + " = ?", new String[]{String.valueOf(cartId)});
        } catch (Exception e) {
            Log.e(TAG, "Error in updateQuantity", e);
            return -1;
        }
    }

    public synchronized int deleteCartItem(int cartId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_CART, COL_CART_ID + " = ?", new String[]{String.valueOf(cartId)});
        } catch (Exception e) {
            Log.e(TAG, "Error in deleteCartItem", e);
            return -1;
        }
    }

    public synchronized int removeFromCart(String foodId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_CART, COL_FOOD_ID + " = ?", new String[]{foodId});
        } catch (Exception e) {
            Log.e(TAG, "Error in removeFromCart", e);
            return -1;
        }
    }

    public synchronized int updateCartQuantity(String foodId, int newQty) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            if (newQty <= 0) {
                return removeFromCart(foodId);
            }
            Cursor cursor = db.query(TABLE_CART, new String[]{COL_PRICE}, COL_FOOD_ID + " = ?", new String[]{foodId}, null, null, null);
            double price = 0.0;
            if (cursor != null && cursor.moveToFirst()) {
                price = cursor.getDouble(0);
                cursor.close();
            }
            ContentValues cv = new ContentValues();
            cv.put(COL_QUANTITY, newQty);
            cv.put(COL_SUBTOTAL, newQty * price);
            return db.update(TABLE_CART, cv, COL_FOOD_ID + " = ?", new String[]{foodId});
        } catch (Exception e) {
            Log.e(TAG, "Error in updateCartQuantity", e);
            return -1;
        }
    }

    public synchronized void clearCart() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_CART, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error in clearCart", e);
        }
    }

    // ==========================================
    // USER SESSION OPERATIONS
    // ==========================================

    public synchronized void saveUserSession(User user, String token) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ensureTablesExist(db);
            db.delete(TABLE_SESSION, null, null);

            if (user != null) {
                ContentValues cv = new ContentValues();
                cv.put(COL_USER_ID, user.getId() != null ? user.getId() : "USR-101");
                cv.put(COL_USER_NAME, user.getName() != null ? user.getName() : "Sonali Motkar");
                cv.put(COL_USER_EMAIL, user.getEmail() != null ? user.getEmail() : "sonalimotkar001@gmail.com");
                cv.put(COL_USER_PHONE, user.getPhone() != null ? user.getPhone() : "+91 832961****");
                cv.put(COL_USER_ADDRESS, user.getAddress() != null ? user.getAddress() : "Pune");
                cv.put(COL_USER_ROLE, user.getRole() != null ? user.getRole() : "customer");
                cv.put(COL_JWT_TOKEN, token != null ? token : "jwt_default_token");

                db.insert(TABLE_SESSION, null, cv);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in saveUserSession", e);
        }
    }

    public synchronized User getLoggedInUser() {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ensureTablesExist(db);
            cursor = db.rawQuery("SELECT * FROM " + TABLE_SESSION + " LIMIT 1", null);
            User user = null;
            if (cursor != null && cursor.moveToFirst()) {
                int colId = cursor.getColumnIndex(COL_USER_ID);
                int colName = cursor.getColumnIndex(COL_USER_NAME);
                int colEmail = cursor.getColumnIndex(COL_USER_EMAIL);
                int colPhone = cursor.getColumnIndex(COL_USER_PHONE);
                int colAddress = cursor.getColumnIndex(COL_USER_ADDRESS);
                int colRole = cursor.getColumnIndex(COL_USER_ROLE);

                user = new User(
                    colId != -1 ? cursor.getString(colId) : "USR-101",
                    colName != -1 ? cursor.getString(colName) : "Sonali Motkar",
                    colEmail != -1 ? cursor.getString(colEmail) : "sonalimotkar001@gmail.com",
                    colPhone != -1 ? cursor.getString(colPhone) : "+91 832961****",
                    colAddress != -1 ? cursor.getString(colAddress) : "Pune",
                    colRole != -1 ? cursor.getString(colRole) : "customer"
                );
            }
            return user;
        } catch (Exception e) {
            Log.e(TAG, "Error in getLoggedInUser", e);
            return null;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    public synchronized String getJwtToken() {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ensureTablesExist(db);
            cursor = db.rawQuery("SELECT " + COL_JWT_TOKEN + " FROM " + TABLE_SESSION + " LIMIT 1", null);
            String token = null;
            if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) {
                token = cursor.getString(0);
            }
            return token;
        } catch (Exception e) {
            Log.e(TAG, "Error in getJwtToken", e);
            return null;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    public synchronized void logout() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_SESSION, null, null);
            db.delete(TABLE_CART, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error in logout", e);
        }
    }
}
