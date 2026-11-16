package com.foodiehub.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodiehub.R;
import com.foodiehub.adapters.OrderAdapter;
import com.foodiehub.database.DatabaseHelper;
import com.foodiehub.models.Order;
import com.foodiehub.network.ApiClient;
import com.foodiehub.network.ApiService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private Button btnLogout;
    private DatabaseHelper dbHelper;
    private List<Order> adminOrders = new ArrayList<>();

    private final String[] statusStages = {
            "Placed", "Confirmed", "Preparing", "Out for Delivery", "Delivered", "Cancelled"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerAdminOrders);
        btnLogout = findViewById(R.id.btnAdminLogout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this, adminOrders);
        recyclerView.setAdapter(adapter);

        fetchAllOrders();

        btnLogout.setOnClickListener(v -> {
            dbHelper.logout();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AdminActivity.this, LoginActivity.class));
            finishAffinity();
        });
    }

    private void fetchAllOrders() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getAllOrders().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().get("data") != null) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Order>>(){}.getType();
                    List<Order> parsed = gson.fromJson(gson.toJson(response.body().get("data")), listType);
                    if (parsed != null && !parsed.isEmpty()) {
                        adminOrders = parsed;
                        adapter.updateOrders(adminOrders);
                        return;
                    }
                }
                loadMockAdminOrders();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                loadMockAdminOrders();
            }
        });
    }

    private void loadMockAdminOrders() {
        adminOrders = new ArrayList<>();
        // Add sample orders for inspection
        adminOrders.add(createAdminOrder("ORD-2026-001", "Placed", 500.85, "Sonali Motkar"));
        adminOrders.add(createAdminOrder("ORD-2026-002", "Confirmed", 491.40, "Rahul Sharma"));
        adminOrders.add(createAdminOrder("ORD-2026-003", "Preparing", 865.00, "Priya Deshmukh"));
        adminOrders.add(createAdminOrder("ORD-2026-004", "Out for Delivery", 338.00, "Amit Verma"));
        adminOrders.add(createAdminOrder("ORD-2026-005", "Delivered", 299.00, "Neha Kulkarni"));

        adapter.updateOrders(adminOrders);
    }

    private Order createAdminOrder(String id, String status, double total, String customer) {
        Order o = new Order();
        o.setOrderStatus(status);
        return o;
    }
}
