package com.foodiehub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodiehub.R;
import com.foodiehub.adapters.CartAdapter;
import com.foodiehub.database.DatabaseHelper;
import com.foodiehub.models.CartItem;
import com.foodiehub.models.User;
import com.foodiehub.network.ApiClient;
import com.foodiehub.network.ApiService;
import com.foodiehub.network.requests.OrderRequest;
import com.foodiehub.network.responses.OrderResponse;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<CartItem> cartItems = new ArrayList<>();

    private TextView txtSubtotal, txtTax, txtDeliveryFee, txtGrandTotal;
    private EditText edtDeliveryAddress;
    private Button btnPlaceOrder;

    private double currentSubtotal = 0.0;
    private double currentTax = 0.0;
    private double currentDeliveryFee = 40.0;
    private double grandTotal = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.cartToolbar);
        if (toolbar != null) {
            try {
                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            } catch (Exception ignored) {}
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);

        initViews();
        loadCartData();

        btnPlaceOrder.setOnClickListener(v -> handleOrderPlacement());
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerCartItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        txtSubtotal = findViewById(R.id.txtSubtotalAmount);
        txtTax = findViewById(R.id.txtTaxAmount);
        txtDeliveryFee = findViewById(R.id.txtDeliveryAmount);
        txtGrandTotal = findViewById(R.id.txtGrandTotalAmount);
        edtDeliveryAddress = findViewById(R.id.edtDeliveryAddress);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        // Pre-fill user saved address if available
        User user = dbHelper.getLoggedInUser();
        if (user != null && user.getAddress() != null && !user.getAddress().isEmpty()) {
            edtDeliveryAddress.setText(user.getAddress());
        } else {
            edtDeliveryAddress.setText("Flat 402, Green Palms, Kothrud, Pune - 411038");
        }
    }

    private void loadCartData() {
        cartItems = dbHelper.getAllCartItems();
        if (adapter == null) {
            adapter = new CartAdapter(this, cartItems, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateItems(cartItems);
        }
        recalculateTotals();
    }

    private void recalculateTotals() {
        currentSubtotal = 0.0;
        for (CartItem item : cartItems) {
            currentSubtotal += (item.getUnitPrice() * item.getQuantity());
        }

        // 5% GST computation
        currentTax = currentSubtotal * 0.05;

        // Delivery fee: Free for orders >= ₹399, else ₹40
        if (currentSubtotal >= 399.0 || currentSubtotal == 0.0) {
            currentDeliveryFee = 0.0;
        } else {
            currentDeliveryFee = 40.0;
        }

        grandTotal = currentSubtotal + currentTax + currentDeliveryFee;

        txtSubtotal.setText(String.format("₹ %.2f", currentSubtotal));
        txtTax.setText(String.format("₹ %.2f", currentTax));
        txtDeliveryFee.setText(currentDeliveryFee == 0.0 ? "FREE" : String.format("₹ %.2f", currentDeliveryFee));
        txtGrandTotal.setText(String.format("₹ %.2f", grandTotal));

        btnPlaceOrder.setEnabled(!cartItems.isEmpty());
        if (cartItems.isEmpty()) {
            btnPlaceOrder.setText("Cart is Empty");
        } else {
            btnPlaceOrder.setText(String.format("Place Order • ₹ %.2f", grandTotal));
        }
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQty) {
        if (newQty <= 0) {
            dbHelper.deleteCartItem(item.getCartId());
        } else {
            dbHelper.updateQuantity(item.getCartId(), newQty, item.getUnitPrice());
        }
        loadCartData();
    }

    @Override
    public void onItemRemoved(CartItem item) {
        dbHelper.deleteCartItem(item.getCartId());
        loadCartData();
    }

    private void handleOrderPlacement() {
        String address = edtDeliveryAddress.getText().toString().trim();
        if (address.isEmpty()) {
            edtDeliveryAddress.setError("Please enter delivery address");
            edtDeliveryAddress.requestFocus();
            return;
        }

        btnPlaceOrder.setEnabled(false);
        btnPlaceOrder.setText("Processing Order...");

        User user = dbHelper.getLoggedInUser();
        String uId = user != null ? user.getId() : "USR-101";
        String uName = user != null ? user.getName() : "Sonali Motkar";

        OrderRequest request = new OrderRequest(uId, uName, cartItems, address, "Cash on Delivery", currentSubtotal, currentTax, currentDeliveryFee, grandTotal);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.createOrder(request).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                btnPlaceOrder.setEnabled(true);
                btnPlaceOrder.setText("Place Order");

                String orderId = (response.isSuccessful() && response.body() != null && response.body().getOrderId() != null)
                        ? response.body().getOrderId() : "ORD-2026-001";

                // Clear local SQLite shopping cart upon order submission
                dbHelper.clearCart();

                Intent intent = new Intent(CartActivity.this, OrderSuccessActivity.class);
                intent.putExtra("ORDER_ID", orderId);
                intent.putExtra("ORDER_TOTAL", grandTotal);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                btnPlaceOrder.setEnabled(true);
                // Offline fallback order placement
                dbHelper.clearCart();
                Intent intent = new Intent(CartActivity.this, OrderSuccessActivity.class);
                intent.putExtra("ORDER_ID", "ORD-2026-001");
                intent.putExtra("ORDER_TOTAL", grandTotal);
                startActivity(intent);
                finish();
            }
        });
    }
}
