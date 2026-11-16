package com.foodiehub.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.foodiehub.R;
import com.foodiehub.views.DeliveryMapView;

/**
 * OrderTrackingActivity delivers a Swiggy/Zomato-grade live delivery experience:
 * - Animated vector route map with live moving scooter and pulsing radar.
 * - Dynamic ETA countdown.
 * - Real-time stage progression notifications (Placed -> Preparing -> Picked Up -> Delivered).
 * - Interactive delivery partner call/message controls.
 */
public class OrderTrackingActivity extends AppCompatActivity {

    private TextView txtOrderId, txtEta, txtSubtext, txtStageIcon, txtStageTitle, txtStageDesc;
    private TextView txtDistanceRemaining, txtAddress, txtLiveBadge;
    private ProgressBar progressStage;
    private DeliveryMapView mapDeliveryView;
    private Button btnCall, btnChat, btnSimulate, btnHome;
    private ImageButton btnBack;

    private int currentStageIndex = 0;
    private Handler autoAdvanceHandler = new Handler(Looper.getMainLooper());
    private Runnable autoAdvanceRunnable;

    private static class TrackingStage {
        String eta;
        String icon;
        String title;
        String desc;
        String distance;
        float mapProgress;
        int progressPercent;
        String popupNotification;

        TrackingStage(String eta, String icon, String title, String desc, String distance, float mapProgress, int progressPercent, String popupNotification) {
            this.eta = eta;
            this.icon = icon;
            this.title = title;
            this.desc = desc;
            this.distance = distance;
            this.mapProgress = mapProgress;
            this.progressPercent = progressPercent;
            this.popupNotification = popupNotification;
        }
    }

    private final TrackingStage[] stages = new TrackingStage[]{
            new TrackingStage(
                    "20 - 25 mins",
                    "📝",
                    "Order Confirmed & Received",
                    "FoodieHub Kitchen received your order.",
                    "📍 3.2 km away • FoodieHub Kitchen",
                    0.05f,
                    15,
                    "🔔 Order confirmed by FoodieHub Kitchen!"
            ),
            new TrackingStage(
                    "15 - 18 mins",
                    "👨‍🍳",
                    "Preparing Your Food",
                    "Chef is grilling and assembling your dishes with care.",
                    "📍 2.8 km away • Kitchen preparing",
                    0.20f,
                    35,
                    "👨‍🍳 Update: Your patties are sizzling on the grill!"
            ),
            new TrackingStage(
                    "10 - 12 mins",
                    "🛵",
                    "Delivery Partner Assigned",
                    "Rahul Sharma (4.9 ★) arrived at the restaurant.",
                    "📍 2.1 km away • Rahul packaging order",
                    0.40f,
                    60,
                    "🛵 Rahul has arrived at the kitchen to collect your order."
            ),
            new TrackingStage(
                    "4 - 6 mins",
                    "🚀",
                    "Out for Delivery",
                    "Rahul is on his way on Kothrud High Street.",
                    "📍 0.8 km away • Arriving near Green Palms",
                    0.75f,
                    85,
                    "⚡ Rahul is speeding towards your delivery address!"
            ),
            new TrackingStage(
                    "Arrived 🎉",
                    "🏁",
                    "Order Delivered!",
                    "Handed over at Flat 402, Green Palms. Enjoy your hot meal!",
                    "📍 Delivered • Enjoy your FoodieHub feast!",
                    1.00f,
                    100,
                    "🎉 Order Delivered! Enjoy your meal! Please rate Rahul."
            )
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        initViews();
        setupOrderData();
        applyStage(0, false);

        // Auto-advance stages every 10 seconds to simulate real-time tracking
        autoAdvanceRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentStageIndex < stages.length - 1) {
                    currentStageIndex++;
                    applyStage(currentStageIndex, true);
                    autoAdvanceHandler.postDelayed(this, 9000);
                }
            }
        };
        autoAdvanceHandler.postDelayed(autoAdvanceRunnable, 7000);
    }

    private void initViews() {
        txtOrderId = findViewById(R.id.txtTrackingOrderId);
        txtEta = findViewById(R.id.txtTrackingEta);
        txtSubtext = findViewById(R.id.txtTrackingSubtext);
        txtStageIcon = findViewById(R.id.txtStageIcon);
        txtStageTitle = findViewById(R.id.txtStageTitle);
        txtStageDesc = findViewById(R.id.txtStageDesc);
        txtDistanceRemaining = findViewById(R.id.txtMapDistanceRemaining);
        txtAddress = findViewById(R.id.txtTrackingAddress);
        txtLiveBadge = findViewById(R.id.txtTrackingLiveBadge);
        progressStage = findViewById(R.id.progressStage);
        mapDeliveryView = findViewById(R.id.mapDeliveryView);

        btnCall = findViewById(R.id.btnCallPartner);
        btnChat = findViewById(R.id.btnChatPartner);
        btnSimulate = findViewById(R.id.btnSimulateNextStage);
        btnHome = findViewById(R.id.btnBackHomeFromTracking);
        btnBack = findViewById(R.id.btnTrackingBack);

        btnBack.setOnClickListener(v -> finish());
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(OrderTrackingActivity.this, MainActivity.class));
            finishAffinity();
        });

        btnSimulate.setOnClickListener(v -> {
            currentStageIndex = (currentStageIndex + 1) % stages.length;
            applyStage(currentStageIndex, true);
        });

        btnCall.setOnClickListener(v -> {
            Toast.makeText(this, "📞 Calling delivery partner Rahul Sharma (+91 832961****)...", Toast.LENGTH_LONG).show();
        });

        btnChat.setOnClickListener(v -> showQuickMessageDialog());
    }

    private void setupOrderData() {
        String oId = getIntent().getStringExtra("ORDER_ID");
        if (oId != null && !oId.isEmpty()) {
            txtOrderId.setText("Order #" + oId);
        } else {
            txtOrderId.setText("Order #ORD-2026-001");
        }

        String userAddress = getIntent().getStringExtra("DELIVERY_ADDRESS");
        if (userAddress != null && !userAddress.isEmpty()) {
            txtAddress.setText(userAddress);
        }
    }

    private void applyStage(int index, boolean animate) {
        TrackingStage stg = stages[index];

        txtEta.setText(stg.eta);
        txtStageIcon.setText(stg.icon);
        txtStageTitle.setText(stg.title);
        txtStageDesc.setText(stg.desc);
        txtSubtext.setText(stg.desc);
        txtDistanceRemaining.setText(stg.distance);
        progressStage.setProgress(stg.progressPercent);

        if (index == stages.length - 1) {
            txtLiveBadge.setText("COMPLETED");
            txtLiveBadge.setBackgroundColor(Color.parseColor("#16A34A"));
        } else {
            txtLiveBadge.setText("● LIVE");
        }

        // Animate scooter along route on custom MapView
        float startP = mapDeliveryView.getProgress();
        float targetP = stg.mapProgress;

        if (animate) {
            ValueAnimator anim = ValueAnimator.ofFloat(startP, targetP);
            anim.setDuration(1200);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.addUpdateListener(a -> mapDeliveryView.setProgress((float) a.getAnimatedValue()));
            anim.start();

            // Swiggy-style popup toast banner
            Toast.makeText(this, stg.popupNotification, Toast.LENGTH_SHORT).show();
        } else {
            mapDeliveryView.setProgress(targetP);
        }
    }

    private void showQuickMessageDialog() {
        String[] quickReplies = {
                "Please ring the doorbell when you arrive.",
                "Please leave the food with building security at Gate 2.",
                "Call me once you enter Green Palms society.",
                "Please ensure the food box remains upright."
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("💬 Message Rahul Sharma");
        builder.setItems(quickReplies, (dialog, which) -> {
            Toast.makeText(OrderTrackingActivity.this, "Sent: \"" + quickReplies[which] + "\"", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (autoAdvanceHandler != null && autoAdvanceRunnable != null) {
            autoAdvanceHandler.removeCallbacks(autoAdvanceRunnable);
        }
    }
}
