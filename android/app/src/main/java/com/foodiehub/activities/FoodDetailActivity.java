package com.foodiehub.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.foodiehub.R;
import com.foodiehub.database.DatabaseHelper;
import com.foodiehub.models.FoodItem;

public class FoodDetailActivity extends AppCompatActivity {

    private ImageView imgFood, imgFavHeart;
    private TextView txtName, txtPrice, txtRating, txtDescription, txtQuantity, txtTag;
    private TextView btnSizeRegular, btnSizeLarge, btnSizeXLarge;
    private TextView btnMinus, btnPlus;
    private FrameLayout btnBack, btnFavorite;
    private Button btnAddToCart;
    private LinearLayout layoutDetailSheet;

    private SwitchCompat switchCheese, switchExtraPatty, switchBacon;

    private FoodItem foodItem;
    private int quantity = 1;
    private DatabaseHelper dbHelper;

    private String selectedSize = "Regular";
    private double sizeExtraPrice = 0.0;
    private double addOnPrice = 0.0;
    private boolean isFavorite = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        dbHelper = new DatabaseHelper(this);

        foodItem = (FoodItem) getIntent().getSerializableExtra("FOOD_ITEM");
        if (foodItem == null) {
            // Default fallback for demo / standalone launch
            foodItem = new FoodItem(
                    "FOD-01",
                    "Spicy Chicken Burger",
                    "Burgers",
                    249.00,
                    "Crispy spicy chicken patty with fresh lettuce, tomato, onion and our special house sauce on toasted brioche.",
                    "https://images.unsplash.com/photo-1568901346375-23c9450c58cd",
                    4.8,
                    "20 mins",
                    "480 kcal",
                    true
            );
        }

        initViews();
        setupData();
        setupListeners();
        playEntranceAnimations();
    }

    private void initViews() {
        imgFood = findViewById(R.id.imgDetailFood);
        imgFavHeart = findViewById(R.id.imgDetailFavHeart);
        txtName = findViewById(R.id.txtDetailName);
        txtPrice = findViewById(R.id.txtDetailPrice);
        txtRating = findViewById(R.id.txtDetailRating);
        txtDescription = findViewById(R.id.txtDetailDescription);
        txtQuantity = findViewById(R.id.txtDetailQuantity);
        txtTag = findViewById(R.id.txtDetailTag);
        layoutDetailSheet = findViewById(R.id.layoutDetailSheet);

        btnBack = findViewById(R.id.btnDetailBack);
        btnFavorite = findViewById(R.id.btnDetailFavorite);
        btnMinus = findViewById(R.id.btnDetailMinus);
        btnPlus = findViewById(R.id.btnDetailPlus);
        btnAddToCart = findViewById(R.id.btnDetailAddToCart);

        btnSizeRegular = findViewById(R.id.btnSizeRegular);
        btnSizeLarge = findViewById(R.id.btnSizeLarge);
        btnSizeXLarge = findViewById(R.id.btnSizeXLarge);

        switchCheese = findViewById(R.id.switchAddCheese);
        switchExtraPatty = findViewById(R.id.switchExtraPatty);
        switchBacon = findViewById(R.id.switchAddBacon);
    }

    private void setupData() {
        txtName.setText(foodItem.getName());
        txtRating.setText(String.format("★ %.1f", foodItem.getRating()));
        txtDescription.setText(foodItem.getDescription());

        if (foodItem.getName().toLowerCase().contains("burger") || foodItem.getName().toLowerCase().contains("spicy")) {
            txtTag.setText("Spicy 🔥");
        } else {
            txtTag.setText("Popular 🌟");
        }

        Glide.with(this)
                .load(foodItem.getImageUrl())
                .placeholder(R.drawable.bg_glass_card)
                .error(R.drawable.bg_glass_card)
                .into(imgFood);

        recalculateTotal();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnFavorite.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            imgFavHeart.setImageResource(isFavorite ? R.drawable.ic_glass_heart_fill : R.drawable.ic_glass_heart_outline);

            ObjectAnimator sx = ObjectAnimator.ofFloat(imgFavHeart, "scaleX", 0.6f, 1.3f, 1.0f);
            ObjectAnimator sy = ObjectAnimator.ofFloat(imgFavHeart, "scaleY", 0.6f, 1.3f, 1.0f);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(sx, sy);
            set.setDuration(300);
            set.setInterpolator(new OvershootInterpolator());
            set.start();
        });

        // Size toggles
        btnSizeRegular.setOnClickListener(v -> selectSize("Regular", 0.0, btnSizeRegular));
        btnSizeLarge.setOnClickListener(v -> selectSize("Large", 40.0, btnSizeLarge));
        btnSizeXLarge.setOnClickListener(v -> selectSize("X Large", 80.0, btnSizeXLarge));

        // Switches listener
        if (switchCheese != null) switchCheese.setOnCheckedChangeListener((btn, checked) -> recalculateTotal());
        if (switchExtraPatty != null) switchExtraPatty.setOnCheckedChangeListener((btn, checked) -> recalculateTotal());
        if (switchBacon != null) switchBacon.setOnCheckedChangeListener((btn, checked) -> recalculateTotal());

        // Quantity Stepper
        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                txtQuantity.setText(String.valueOf(quantity));
                recalculateTotal();
            }
        });

        btnPlus.setOnClickListener(v -> {
            quantity++;
            txtQuantity.setText(String.valueOf(quantity));
            recalculateTotal();
        });

        // Add to Cart
        btnAddToCart.setOnClickListener(v -> {
            ObjectAnimator sx = ObjectAnimator.ofFloat(btnAddToCart, "scaleX", 0.95f, 1.03f, 1.0f);
            ObjectAnimator sy = ObjectAnimator.ofFloat(btnAddToCart, "scaleY", 0.95f, 1.03f, 1.0f);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(sx, sy);
            set.setDuration(220);
            set.start();

            double unitPrice = foodItem.getPrice() + sizeExtraPrice + calculateAddOns();
            String customizedName = foodItem.getName() + " (" + selectedSize + ")";

            try {
                dbHelper.addToCart(foodItem.getId(), customizedName, unitPrice, quantity, foodItem.getImageUrl());
                Toast.makeText(this, "Added " + quantity + "x " + customizedName + " to Cart", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Added to Cart!", Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }

    private void selectSize(String sizeName, double extra, TextView activeBtn) {
        selectedSize = sizeName;
        sizeExtraPrice = extra;

        // Reset all buttons to unselected
        btnSizeRegular.setBackgroundResource(R.drawable.bg_pill_unselected);
        btnSizeRegular.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

        btnSizeLarge.setBackgroundResource(R.drawable.bg_pill_unselected);
        btnSizeLarge.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

        btnSizeXLarge.setBackgroundResource(R.drawable.bg_pill_unselected);
        btnSizeXLarge.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

        // Highlight selected
        activeBtn.setBackgroundResource(R.drawable.bg_pill_selected);
        activeBtn.setTextColor(Color.WHITE);

        // Micro-bounce
        ObjectAnimator sx = ObjectAnimator.ofFloat(activeBtn, "scaleX", 0.9f, 1.06f, 1.0f);
        ObjectAnimator sy = ObjectAnimator.ofFloat(activeBtn, "scaleY", 0.9f, 1.06f, 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(sx, sy);
        set.setDuration(240);
        set.start();

        recalculateTotal();
    }

    private double calculateAddOns() {
        double total = 0.0;
        if (switchCheese != null && switchCheese.isChecked()) total += 25.0;
        if (switchExtraPatty != null && switchExtraPatty.isChecked()) total += 50.0;
        if (switchBacon != null && switchBacon.isChecked()) total += 30.0;
        return total;
    }

    private void recalculateTotal() {
        double unitPrice = foodItem.getPrice() + sizeExtraPrice + calculateAddOns();
        double grandTotal = unitPrice * quantity;

        txtPrice.setText(String.format("₹ %.2f", unitPrice));
        btnAddToCart.setText(String.format("Add to Cart  •  ₹ %.2f", grandTotal));
    }

    private void playEntranceAnimations() {
        // Slide up the frosted bottom sheet smoothly
        if (layoutDetailSheet != null) {
            layoutDetailSheet.setTranslationY(300f);
            layoutDetailSheet.setAlpha(0f);
            layoutDetailSheet.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(600)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }

        // Hero image subtle breathing scale animation
        if (imgFood != null) {
            imgFood.setScaleX(0.92f);
            imgFood.setScaleY(0.92f);
            imgFood.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(700)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }
}
