package com.foodiehub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.foodiehub.R;
import com.foodiehub.database.DatabaseHelper;
import com.foodiehub.models.User;

public class ProfileActivity extends AppCompatActivity {

    private View btnBack;
    private TextView btnHelp;
    private TextView txtName, txtPhone, txtEmail;
    private LinearLayout btnAddress, btnPayments, btnRefunds, btnMoney;
    private Button btnBrowsePastOrders;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);

        initViews();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnProfileBack);
        btnHelp = findViewById(R.id.btnProfileHelp);
        txtName = findViewById(R.id.txtProfileName);
        txtPhone = findViewById(R.id.txtProfilePhone);
        txtEmail = findViewById(R.id.txtProfileEmail);

        btnAddress = findViewById(R.id.btnQuickAddress);
        btnPayments = findViewById(R.id.btnQuickPayments);
        btnRefunds = findViewById(R.id.btnQuickRefunds);
        btnMoney = findViewById(R.id.btnQuickMoney);
        btnBrowsePastOrders = findViewById(R.id.btnBrowsePastOrders);
    }

    private void loadUserData() {
        User user = dbHelper.getLoggedInUser();
        if (user != null) {
            txtName.setText(user.getName());
            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                txtPhone.setText(user.getPhone());
            } else {
                txtPhone.setText("+91 - 8329611364");
            }
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                txtEmail.setText(user.getEmail());
            } else {
                txtEmail.setText("sonalimotkar4@gmail.com");
            }
        } else {
            txtName.setText("Sonali Motkar");
            txtPhone.setText("+91 - 8329611364");
            txtEmail.setText("sonalimotkar4@gmail.com");
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnHelp.setOnClickListener(v -> {
            Toast.makeText(this, "FoodieHub 24x7 Customer Support & Help", Toast.LENGTH_SHORT).show();
        });

        btnAddress.setOnClickListener(v -> {
            Toast.makeText(this, "📍 Saved Address: Flat 402, Green Palms, Pune", Toast.LENGTH_LONG).show();
        });

        btnPayments.setOnClickListener(v -> {
            Toast.makeText(this, "💳 Payment Modes: UPI, Cards & Netbanking active", Toast.LENGTH_SHORT).show();
        });

        btnRefunds.setOnClickListener(v -> {
            Toast.makeText(this, "🔄 All refunds settled. Balance: ₹0.00", Toast.LENGTH_SHORT).show();
        });

        btnMoney.setOnClickListener(v -> {
            Toast.makeText(this, "👛 FoodieHub Money Balance: ₹ 500.00", Toast.LENGTH_SHORT).show();
        });

        btnBrowsePastOrders.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, OrderHistoryActivity.class));
        });
    }
}
