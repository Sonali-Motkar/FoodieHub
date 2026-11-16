package com.foodiehub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.foodiehub.R;

public class OrderSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        String orderId = getIntent().getStringExtra("ORDER_ID");
        double total = getIntent().getDoubleExtra("ORDER_TOTAL", 0.0);

        TextView txtOrderId = findViewById(R.id.txtSuccessOrderId);
        TextView txtAmount = findViewById(R.id.txtSuccessAmount);
        Button btnHistory = findViewById(R.id.btnSuccessViewHistory);
        Button btnContinue = findViewById(R.id.btnSuccessContinue);

        if (orderId != null) {
            txtOrderId.setText("Order ID: " + orderId);
        }
        txtAmount.setText(String.format("Total Amount Paid: ₹ %.2f", total));

        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(OrderSuccessActivity.this, OrderHistoryActivity.class));
            finish();
        });

        btnContinue.setOnClickListener(v -> {
            startActivity(new Intent(OrderSuccessActivity.this, MainActivity.class));
            finishAffinity();
        });
    }
}
