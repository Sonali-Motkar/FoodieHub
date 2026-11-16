package com.foodiehub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodiehub.R;
import com.foodiehub.adapters.RestaurantComboAdapter;
import com.foodiehub.adapters.RestaurantDishAdapter;
import com.foodiehub.database.DatabaseHelper;
import com.foodiehub.models.CartItem;
import com.foodiehub.models.FoodItem;
import com.foodiehub.models.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailActivity extends AppCompatActivity implements
        RestaurantDishAdapter.OnDishActionListener,
        RestaurantComboAdapter.OnComboAddListener {

    private View btnBack, btnSearch;
    private TextView txtHeaderName, txtHeaderTime, txtRecommendedTitle, txtRecommendedChevron, txtAddress;
    private RecyclerView recyclerCombos, recyclerDishes;
    private CardView cardStickyCart, cardFloatingMenu;
    private TextView txtStickyCartCount;

    private Restaurant restaurant;
    private DatabaseHelper dbHelper;
    private RestaurantDishAdapter dishAdapter;
    private RestaurantComboAdapter comboAdapter;

    private List<FoodItem> combosList = new ArrayList<>();
    private List<FoodItem> recommendedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        dbHelper = new DatabaseHelper(this);

        restaurant = (Restaurant) getIntent().getSerializableExtra("RESTAURANT");
        if (restaurant == null) {
            // Default Wendy's Burgers matching reference screenshot 3
            restaurant = new Restaurant(
                    "REST-WENDYS",
                    "Wendy's Burgers",
                    4.2,
                    "25-30 mins",
                    "Burgers, Fast Food, wrap",
                    "Ravet",
                    "https://images.unsplash.com/photo-1586190848861-99aa4a171e90",
                    true,
                    "ITEMS AT ₹79"
            );
        }

        initViews();
        setupRestaurantInfo();
        setupCombos();
        setupRecommendedDishes();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncCartState();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnRestDetailBack);
        btnSearch = findViewById(R.id.btnRestSearch);
        txtHeaderName = findViewById(R.id.txtHeaderRestName);
        txtHeaderTime = findViewById(R.id.txtHeaderRestTime);
        txtRecommendedTitle = findViewById(R.id.txtRecommendedTitle);
        txtRecommendedChevron = findViewById(R.id.txtRecommendedChevron);
        txtAddress = findViewById(R.id.txtRestDetailAddress);

        recyclerCombos = findViewById(R.id.recyclerCombos);
        recyclerDishes = findViewById(R.id.recyclerRecommendedDishes);

        cardStickyCart = findViewById(R.id.cardStickyBottomCart);
        cardFloatingMenu = findViewById(R.id.cardFloatingMenuPill);
        txtStickyCartCount = findViewById(R.id.txtStickyCartCount);
    }

    private void setupRestaurantInfo() {
        txtHeaderName.setText(restaurant.getName());
        txtHeaderTime.setText(restaurant.getDeliveryTime());
        txtAddress.setText(restaurant.getName() + ", " + restaurant.getArea() + ", Pune • FSSAI Lic. 11522036000214");
    }

    private void setupCombos() {
        combosList = new ArrayList<>();

        if (restaurant.getName().toLowerCase().contains("wendy")) {
            combosList.add(new FoodItem(
                    "CMB-01",
                    "Teriyaki Crispy Veg Burger",
                    "Combos",
                    313.00,
                    "Teriyaki glazed crispy patty with herb dip and fries.",
                    "https://images.unsplash.com/photo-1550547660-d9450f859349",
                    4.3, "20 mins", "510 kcal", true, "Wendy's Burgers", true, 95, true
            ));

            combosList.add(new FoodItem(
                    "CMB-02",
                    "Crispy Chicken Burger Combo",
                    "Combos",
                    313.00,
                    "Crispy fried chicken burger + salted fries + Pepsi can.",
                    "https://images.unsplash.com/photo-1568901346375-23c9450c58cd",
                    4.4, "25 mins", "620 kcal", true, "Wendy's Burgers", false, 140, true
            ));

            combosList.add(new FoodItem(
                    "CMB-03",
                    "Crispy Veg Burger Combo",
                    "Combos",
                    296.00,
                    "Crispy veg burger + medium fries + soft beverage.",
                    "https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5",
                    4.2, "20 mins", "490 kcal", true, "Wendy's Burgers", true, 80, true
            ));
        } else if (restaurant.getName().toLowerCase().contains("kfc")) {
            combosList.add(new FoodItem(
                    "KFC-CMB-01",
                    "Chicken Krisper With Reg Fries",
                    "Combos",
                    273.00,
                    "Crispy boneless fried fillet burger served with golden salted fries.",
                    "https://images.unsplash.com/photo-1513639776629-7b61b0ac49cb",
                    4.3, "25 mins", "580 kcal", true, "KFC", false, 320, true
            ));
            combosList.add(new FoodItem(
                    "KFC-CMB-02",
                    "Hot Wings (6 Pcs) + Dip",
                    "Combos",
                    229.00,
                    "Fiery marinated crunchy wings fried to perfection with sweet chilli dip.",
                    "https://images.unsplash.com/photo-1567620832903-9fc6debc209f",
                    4.5, "20 mins", "490 kcal", true, "KFC", false, 280, true
            ));
        } else {
            combosList.add(new FoodItem(
                    "GEN-CMB-01",
                    restaurant.getName() + " Supreme Combo",
                    "Combos",
                    299.00,
                    "Signature main dish + classic crunchy side + cold beverage.",
                    "https://images.unsplash.com/photo-1568901346375-23c9450c58cd",
                    4.4, "20 mins", "540 kcal", true, restaurant.getName(), true, 110, true
            ));
        }

        comboAdapter = new RestaurantComboAdapter(this, combosList, this);
        recyclerCombos.setAdapter(comboAdapter);
    }

    private void setupRecommendedDishes() {
        recommendedList = new ArrayList<>();

        if (restaurant.getName().toLowerCase().contains("wendy")) {
            // Exact items from Screenshot 3 (media_1794848177471_aa2b02fc.jpg)
            recommendedList.add(new FoodItem(
                    "WEN-01",
                    "Crispy Veg Burger.",
                    "Burgers",
                    79.00,
                    "Crispy potato and peas patty topped with special mayonnaise, crisp lettuce and onion.",
                    "https://images.unsplash.com/photo-1550547660-d9450f859349",
                    4.2, "20 mins", "380 kcal", true, "Wendy's Burgers", true, 188, true
            ));

            recommendedList.add(new FoodItem(
                    "WEN-02",
                    "Crispy Chicken Burger.",
                    "Burgers",
                    115.00,
                    "Crispy fried chicken patty with fresh shredded lettuce and secret house spicy mayo.",
                    "https://images.unsplash.com/photo-1568901346375-23c9450c58cd",
                    4.2, "25 mins", "460 kcal", true, "Wendy's Burgers", false, 255, true
            ));

            recommendedList.add(new FoodItem(
                    "WEN-03",
                    "Spicy Paneer Crunch Burger",
                    "Burgers",
                    129.00,
                    "Crispy cottage cheese patty with fiery paprika dip and sliced tomatoes.",
                    "https://images.unsplash.com/photo-1586190848861-99aa4a171e90",
                    4.4, "20 mins", "440 kcal", true, "Wendy's Burgers", true, 162, true
            ));

            recommendedList.add(new FoodItem(
                    "WEN-04",
                    "Classic French Fries (M)",
                    "Sides",
                    89.00,
                    "Golden salted potato fries fried to a crispy golden crunch.",
                    "https://images.unsplash.com/photo-1573080496219-bb080dd4f877",
                    4.1, "15 mins", "290 kcal", true, "Wendy's Burgers", true, 412, true
            ));

            recommendedList.add(new FoodItem(
                    "WEN-05",
                    "Frosted Chocolate Shake",
                    "Beverages",
                    109.00,
                    "Signature creamy thick shake whipped with decadent Belgian cocoa.",
                    "https://images.unsplash.com/photo-1572490122747-3968b75cc699",
                    4.6, "10 mins", "310 kcal", true, "Wendy's Burgers", true, 204, true
            ));
        } else if (restaurant.getName().toLowerCase().contains("kfc")) {
            recommendedList.add(new FoodItem(
                    "KFC-01",
                    "Chicken Krisper With Reg Fries",
                    "Burgers",
                    273.00,
                    "Tender juicy chicken fillet inside soft sesame bun with salted fries.",
                    "https://images.unsplash.com/photo-1568901346375-23c9450c58cd",
                    4.3, "25 mins", "560 kcal", true, "KFC", false, 312, true
            ));

            recommendedList.add(new FoodItem(
                    "KFC-02",
                    "Hot & Crispy Chicken (2 Pcs)",
                    "Chicken",
                    219.00,
                    "11 secret herbs and spices recipe chicken pieces fried to golden perfection.",
                    "https://images.unsplash.com/photo-1513639776629-7b61b0ac49cb",
                    4.5, "20 mins", "520 kcal", true, "KFC", false, 480, true
            ));

            recommendedList.add(new FoodItem(
                    "KFC-03",
                    "Veg Zinger Burger",
                    "Burgers",
                    169.00,
                    "Crispy veg patty, fresh crunchy lettuce and creamy thousand island mayo.",
                    "https://images.unsplash.com/photo-1550547660-d9450f859349",
                    4.1, "15 mins", "390 kcal", true, "KFC", true, 190, true
            ));
        } else if (restaurant.getName().toLowerCase().contains("sairat")) {
            recommendedList.add(new FoodItem(
                    "SRT-01",
                    "Chicken Tandoori Dum Biryani",
                    "Biryani",
                    174.00,
                    "Aromatic basmati rice layered with smoky tandoori chicken and royal saffron spices.",
                    "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8",
                    4.4, "20 mins", "580 kcal", true, "Sairat Biryani", false, 340, true
            ));

            recommendedList.add(new FoodItem(
                    "SRT-02",
                    "Special Hyderabadi Mutton Biryani",
                    "Biryani",
                    249.00,
                    "Slow-cooked tender mutton cooked on dum with mint, caramelized onions and spicy salan.",
                    "https://images.unsplash.com/photo-1589302168068-964664d93dc0",
                    4.6, "25 mins", "670 kcal", true, "Sairat Biryani", false, 280, true
            ));
        } else {
            recommendedList.add(new FoodItem(
                    "GEN-01",
                    "Signature " + restaurant.getName() + " Special",
                    "Special",
                    189.00,
                    "Chef's specialty recipe crafted with fresh farm ingredients and authentic spices.",
                    "https://images.unsplash.com/photo-1568901346375-23c9450c58cd",
                    4.4, "20 mins", "420 kcal", true, restaurant.getName(), true, 188, true
            ));
        }

        txtRecommendedTitle.setText("Recommended (" + recommendedList.size() + ")");

        dishAdapter = new RestaurantDishAdapter(this, recommendedList, this);
        recyclerDishes.setLayoutManager(new LinearLayoutManager(this));
        recyclerDishes.setAdapter(dishAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSearch.setOnClickListener(v -> {
            Toast.makeText(this, "Search within " + restaurant.getName(), Toast.LENGTH_SHORT).show();
        });

        cardStickyCart.setOnClickListener(v -> {
            startActivity(new Intent(RestaurantDetailActivity.this, CartActivity.class));
        });

        cardFloatingMenu.setOnClickListener(v -> {
            Toast.makeText(this, "🍽️ Categories: Combos, Recommended, Sides, Drinks", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.layoutRecommendedHeader).setOnClickListener(v -> {
            if (recyclerDishes.getVisibility() == View.VISIBLE) {
                recyclerDishes.setVisibility(View.GONE);
                txtRecommendedChevron.setText("▼");
            } else {
                recyclerDishes.setVisibility(View.VISIBLE);
                txtRecommendedChevron.setText("▲");
            }
        });
    }

    private void syncCartState() {
        try {
            int count = dbHelper.getCartCount();
            if (count > 0) {
                cardStickyCart.setVisibility(View.VISIBLE);
                txtStickyCartCount.setText(count + (count == 1 ? " item added" : " items added"));

                List<CartItem> cartItems = dbHelper.getAllCartItems();
                for (CartItem ci : cartItems) {
                    if (dishAdapter != null) {
                        dishAdapter.setItemQuantity(ci.getFoodId(), ci.getQuantity());
                    }
                }
            } else {
                cardStickyCart.setVisibility(View.GONE);
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onAddToCart(FoodItem item) {
        try {
            dbHelper.addToCart(item.getId(), item.getName(), item.getPrice(), 1, item.getImageUrl());
            syncCartState();
            Toast.makeText(this, "Added " + item.getName() + " to cart", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            syncCartState();
        }
    }

    @Override
    public void onQuantityChanged(FoodItem item, int newQty) {
        try {
            if (newQty <= 0) {
                dbHelper.removeFromCart(item.getId());
            } else {
                dbHelper.updateCartQuantity(item.getId(), newQty);
            }
            syncCartState();
        } catch (Exception ignored) {}
    }

    @Override
    public void onDishClick(FoodItem item) {
        Intent intent = new Intent(this, FoodDetailActivity.class);
        intent.putExtra("FOOD_ITEM", item);
        startActivity(intent);
    }

    @Override
    public void onComboAdd(FoodItem combo) {
        onAddToCart(combo);
    }
}
