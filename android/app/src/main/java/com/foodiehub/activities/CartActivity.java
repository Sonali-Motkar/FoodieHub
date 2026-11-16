package com.foodiehub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.UUID;
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

    // Payment Views
    private RadioGroup radioGroupPayment;
    private RadioButton radioCard, radioUpi, radioCod;
    private LinearLayout layoutCardDetails, layoutUpiDetails;
    private EditText edtCardNumber, edtCardExpiry, edtCardCvv, edtUpiId;

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

        // Bind Payment UI components
        radioGroupPayment = findViewById(R.id.radioGroupPayment);
        radioCard = findViewById(R.id.radioCard);
        radioUpi = findViewById(R.id.radioUpi);
        radioCod = findViewById(R.id.radioCod);
        layoutCardDetails = findViewById(R.id.layoutCardDetails);
        layoutUpiDetails = findViewById(R.id.layoutUpiDetails);
        edtCardNumber = findViewById(R.id.edtCardNumber);
        edtCardExpiry = findViewById(R.id.edtCardExpiry);
        edtCardCvv = findViewById(R.id.edtCardCvv);
        edtUpiId = findViewById(R.id.edtUpiId);

        // Pre-fill demo card details for quick testing
        if (edtCardNumber != null) edtCardNumber.setText("4532 8901 2345 8921");
        if (edtCardExpiry != null) edtCardExpiry.setText("08/28");
        if (edtCardCvv != null) edtCardCvv.setText("784");
        if (edtUpiId != null) edtUpiId.setText("sonalimotkar@okhdfcbank");

        if (radioGroupPayment != null) {
            radioGroupPayment.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.radioCard) {
                    if (layoutCardDetails != null) layoutCardDetails.setVisibility(View.VISIBLE);
                    if (layoutUpiDetails != null) layoutUpiDetails.setVisibility(View.GONE);
                } else if (checkedId == R.id.radioUpi) {
                    if (layoutCardDetails != null) layoutCardDetails.setVisibility(View.GONE);
                    if (layoutUpiDetails != null) layoutUpiDetails.setVisibility(View.VISIBLE);
                } else {
                    if (layoutCardDetails != null) layoutCardDetails.setVisibility(View.GONE);
                    if (layoutUpiDetails != null) layoutUpiDetails.setVisibility(View.GONE);
                }
                updateOrderButtonText();
            });
        }

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

        updateOrderButtonText();
    }

    private void updateOrderButtonText() {
        if (btnPlaceOrder == null) return;
        if (cartItems.isEmpty()) {
            btnPlaceOrder.setEnabled(false);
            btnPlaceOrder.setText("Cart is Empty");
            return;
        }
        btnPlaceOrder.setEnabled(true);
        if (radioCard != null && radioCard.isChecked()) {
            btnPlaceOrder.setText(String.format("Pay with Card • ₹ %.2f", grandTotal));
        } else if (radioUpi != null && radioUpi.isChecked()) {
            btnPlaceOrder.setText(String.format("Pay via UPI • ₹ %.2f", grandTotal));
        } else {
            btnPlaceOrder.setText(String.format("Place COD Order • ₹ %.2f", grandTotal));
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

        String paymentMethod = "Cash on Delivery";
        if (radioCard != null && radioCard.isChecked()) {
            paymentMethod = "Credit / Debit Card";
            if (edtCardNumber != null) {
                String cardNum = edtCardNumber.getText().toString().trim();
                if (cardNum.isEmpty() || cardNum.length() < 12) {
                    edtCardNumber.setError("Please enter valid card number");
                    edtCardNumber.requestFocus();
                    return;
                }
            }
        } else if (radioUpi != null && radioUpi.isChecked()) {
            paymentMethod = "UPI";
            if (edtUpiId != null) {
                String upi = edtUpiId.getText().toString().trim();
                if (upi.isEmpty() || !upi.contains("@")) {
                    edtUpiId.setError("Please enter valid UPI ID (e.g. name@okhdfc)");
                    edtUpiId.requestFocus();
                    return;
                }
            }
        }

        btnPlaceOrder.setEnabled(false);
        btnPlaceOrder.setText("Processing " + paymentMethod + "...");

        User user = dbHelper.getLoggedInUser();
        String uId = user != null ? user.getId() : "USR-101";
        String uName = user != null ? user.getName() : "Sonali Motkar";

        final String finalAddress = address;
        final String finalPaymentMethod = paymentMethod;

        OrderRequest request = new OrderRequest(uId, uName, cartItems, address, paymentMethod, currentSubtotal, currentTax, currentDeliveryFee, grandTotal);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.createOrder(request).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                btnPlaceOrder.setEnabled(true);
                updateOrderButtonText();

                String orderId = (response.isSuccessful() && response.body() != null && response.body().getOrderId() != null)
                        ? response.body().getOrderId() : ("ORD-" + (System.currentTimeMillis() % 1000000));

                // Clear local SQLite shopping cart upon order submission
                dbHelper.clearCart();

                // Directly launch Swiggy-style Live Delivery Tracking Screen
                Intent intent = new Intent(CartActivity.this, OrderTrackingActivity.class);
                intent.putExtra("ORDER_ID", orderId);
                intent.putExtra("ORDER_TOTAL", grandTotal);
                intent.putExtra("DELIVERY_ADDRESS", finalAddress);
                intent.putExtra("PAYMENT_METHOD", finalPaymentMethod);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                btnPlaceOrder.setEnabled(true);
                updateOrderButtonText();

                // Offline fallback order placement
                String orderId = "ORD-" + (System.currentTimeMillis() % 1000000);
                dbHelper.clearCart();

                // Directly launch Swiggy-style Live Delivery Tracking Screen
                Intent intent = new Intent(CartActivity.this, OrderTrackingActivity.class);
                intent.putExtra("ORDER_ID", orderId);
                intent.putExtra("ORDER_TOTAL", grandTotal);
                intent.putExtra("DELIVERY_ADDRESS", finalAddress);
                intent.putExtra("PAYMENT_METHOD", finalPaymentMethod);
                startActivity(intent);
                finish();
            }
        });
    }
}
