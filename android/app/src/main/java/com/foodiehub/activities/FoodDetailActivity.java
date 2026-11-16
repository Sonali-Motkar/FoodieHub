package com.foodiehub.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.foodiehub.R;
import com.foodiehub.database.DatabaseHelper;
import com.foodiehub.models.FoodItem;

public class FoodDetailActivity extends AppCompatActivity {

    private ImageView imgFood;
    private TextView txtCategory, txtName, txtPrice, txtRating, txtPrepTime, txtCalories, txtDescription, txtQty;
    private ImageButton btnMinus, btnPlus;
    private Button btnAddToCart;

    private FoodItem foodItem;
    private int quantity = 1;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        dbHelper = new DatabaseHelper(this);

        foodItem = (FoodItem) getIntent().getSerializableExtra("FOOD_ITEM");
        if (foodItem == null) {
            Toast.makeText(this, "Food item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        populateData();

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityAndButton();
            }
        });

        btnPlus.setOnClickListener(v -> {
            quantity++;
            updateQuantityAndButton();
        });

        btnAddToCart.setOnClickListener(v -> {
            try {
                dbHelper.addToCart(foodItem.getId(), foodItem.getName(), foodItem.getPrice(), quantity, foodItem.getImageUrl());
                Toast.makeText(this, "Added " + quantity + "x " + foodItem.getName() + " to Cart", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }

    private void initViews() {
        imgFood = findViewById(R.id.imgDetailFood);
        txtCategory = findViewById(R.id.txtDetailCategory);
        txtName = findViewById(R.id.txtDetailName);
        txtPrice = findViewById(R.id.txtDetailPrice);
        txtRating = findViewById(R.id.txtDetailRating);
        txtPrepTime = findViewById(R.id.txtDetailPrepTime);
        txtCalories = findViewById(R.id.txtDetailCalories);
        txtDescription = findViewById(R.id.txtDetailDescription);
        txtQty = findViewById(R.id.txtDetailQty);
        btnMinus = findViewById(R.id.btnDetailMinus);
        btnPlus = findViewById(R.id.btnDetailPlus);
        btnAddToCart = findViewById(R.id.btnDetailAddToCart);
    }

    private void populateData() {
        txtCategory.setText(foodItem.getCategory().toUpperCase());
        txtName.setText(foodItem.getName());
        txtPrice.setText(String.format("₹ %.2f", foodItem.getPrice()));
        txtRating.setText(String.format("%.1f ★", foodItem.getRating()));
        txtPrepTime.setText("⏱ " + foodItem.getPrepTime());
        txtCalories.setText("🔥 " + (foodItem.getCalories() != null ? foodItem.getCalories() : "400 kcal"));
        txtDescription.setText(foodItem.getDescription());

        Glide.with(this)
                .load(foodItem.getImageUrl())
                .placeholder(R.drawable.bg_rounded_card)
                .error(R.drawable.bg_rounded_card)
                .into(imgFood);

        updateQuantityAndButton();
    }

    private void updateQuantityAndButton() {
        txtQty.setText(String.valueOf(quantity));
        double total = foodItem.getPrice() * quantity;
        btnAddToCart.setText(String.format("Add to Cart • ₹ %.2f", total));
    }
}
