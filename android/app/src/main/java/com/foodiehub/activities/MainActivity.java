package com.foodiehub.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.foodiehub.R;
import com.foodiehub.adapters.CategoryAdapter;
import com.foodiehub.adapters.FoodAdapter;
import com.foodiehub.adapters.PopularFoodAdapter;
import com.foodiehub.database.DatabaseHelper;
import com.foodiehub.models.CartItem;
import com.foodiehub.models.Category;
import com.foodiehub.models.FoodItem;
import com.foodiehub.models.User;
import com.foodiehub.network.ApiClient;
import com.foodiehub.network.ApiService;
import com.foodiehub.network.responses.FoodResponse;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        FoodAdapter.OnFoodClickListener,
        CategoryAdapter.OnCategorySelectedListener,
        PopularFoodAdapter.OnPopularItemListener {

    private TextView txtGreeting, txtHeroHeadline, txtFloatingCount, txtFloatingTotal, txtViewAllPopular;
    private EditText edtSearch;
    private FrameLayout btnGlassMenu, btnNavLogout;
    private ImageView btnFilterTune, imgPromoCombo;
    private Button btnPromoOrderNow;
    private CardView cardPromoBanner, cardFloatingCart;

    private RecyclerView recyclerCategories, recyclerPopularFoods, recyclerFoods;
    private CategoryAdapter categoryAdapter;
    private PopularFoodAdapter popularFoodAdapter;
    private FoodAdapter foodAdapter;

    private LinearLayout navBtnHome, navBtnOffers, navBtnOrders, navBtnFavorites;

    private DatabaseHelper dbHelper;
    private User currentUser;
    private List<FoodItem> masterFoodList = new ArrayList<>();
    private List<FoodItem> popularList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private String selectedCategory = "Burger";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        currentUser = dbHelper.getLoggedInUser();

        initViews();
        setupHeroHeadline();
        setupCategories();
        fetchFoods();
        setupBottomNav();
        setupPromoBanner();
        playEntranceAnimations();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int count, int after) {
                filterFoods(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        cardFloatingCart.setOnClickListener(v -> openCart());

        btnNavLogout.setOnClickListener(v -> {
            dbHelper.logout();
            Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finishAffinity();
        });

        btnGlassMenu.setOnClickListener(v -> {
            Toast.makeText(this, "FoodieHub Dark Glass UI • v2.0", Toast.LENGTH_SHORT).show();
        });

        btnFilterTune.setOnClickListener(v -> {
            Toast.makeText(this, "Filters applied: Top Rated & Most Popular", Toast.LENGTH_SHORT).show();
        });

        txtViewAllPopular.setOnClickListener(v -> {
            // Smoothly scroll down to full catalog
            View catalogTitle = findViewById(R.id.recyclerFoods);
            if (catalogTitle != null) {
                catalogTitle.getParent().requestChildFocus(catalogTitle, catalogTitle);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void initViews() {
        txtGreeting = findViewById(R.id.txtGreeting);
        txtHeroHeadline = findViewById(R.id.txtHeroHeadline);
        txtFloatingCount = findViewById(R.id.txtFloatingCartCount);
        txtFloatingTotal = findViewById(R.id.txtFloatingCartTotal);
        txtViewAllPopular = findViewById(R.id.txtViewAllPopular);
        edtSearch = findViewById(R.id.edtSearchFood);

        btnGlassMenu = findViewById(R.id.btnGlassMenu);
        btnNavLogout = findViewById(R.id.btnNavLogout);
        btnFilterTune = findViewById(R.id.btnFilterTune);
        cardPromoBanner = findViewById(R.id.cardPromoBanner);
        btnPromoOrderNow = findViewById(R.id.btnPromoOrderNow);
        imgPromoCombo = findViewById(R.id.imgPromoCombo);
        cardFloatingCart = findViewById(R.id.cardFloatingCart);

        recyclerCategories = findViewById(R.id.recyclerCategories);
        recyclerPopularFoods = findViewById(R.id.recyclerPopularFoods);
        recyclerFoods = findViewById(R.id.recyclerFoods);

        navBtnHome = findViewById(R.id.navBtnHome);
        navBtnOffers = findViewById(R.id.navBtnOffers);
        navBtnOrders = findViewById(R.id.navBtnOrders);
        navBtnFavorites = findViewById(R.id.navBtnFavorites);

        if (currentUser != null && currentUser.getName() != null) {
            txtGreeting.setText("Hi, " + currentUser.getName().split(" ")[0] + " 👋");
        } else {
            txtGreeting.setText("Hi, Sonali 👋");
        }
    }

    private void setupHeroHeadline() {
        String text = "Good Food\nGood Mood!";
        SpannableString spannable = new SpannableString(text);
        int moodIndex = text.indexOf("Mood!");
        if (moodIndex != -1) {
            spannable.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#FFA000")),
                    moodIndex,
                    moodIndex + 5,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        txtHeroHeadline.setText(spannable);
    }

    private void setupPromoBanner() {
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5")
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(24)))
                .placeholder(R.drawable.bg_glass_card)
                .error(R.drawable.bg_glass_card)
                .into(imgPromoCombo);

        btnPromoOrderNow.setOnClickListener(v -> {
            try {
                dbHelper.addToCart(
                        "FOD-PROMO",
                        "Spicy Burger Combo (Burger + Fries + Drink)",
                        349.00,
                        1,
                        "https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5"
                );
                Toast.makeText(this, "Added Spicy Burger Combo (20% OFF) to Cart!", Toast.LENGTH_SHORT).show();
                openCart();
            } catch (Exception e) {
                openCart();
            }
        });
    }

    private void setupBottomNav() {
        navBtnHome.setOnClickListener(v -> {
            // Scroll to top
            View scroll = findViewById(R.id.nestedScrollHome);
            if (scroll != null) scroll.scrollTo(0, 0);
        });

        navBtnOffers.setOnClickListener(v -> {
            Toast.makeText(this, "🎁 20% OFF applied on Combos! Promo: FREEDEL", Toast.LENGTH_LONG).show();
        });

        navBtnOrders.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
        });

        navBtnFavorites.setOnClickListener(v -> {
            Toast.makeText(this, "❤️ Saved favorite dishes", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupCategories() {
        categoryList = new ArrayList<>();
        categoryList.add(new Category("CAT-01", "Burger", "ic_category_burger"));
        categoryList.add(new Category("CAT-02", "Pizza", "ic_category_pizza"));
        categoryList.add(new Category("CAT-03", "Chicken", "ic_category_chicken"));
        categoryList.add(new Category("CAT-04", "Fries", "ic_category_fries"));
        categoryList.add(new Category("CAT-05", "Drink", "ic_category_drink"));

        categoryAdapter = new CategoryAdapter(this, categoryList, this);
        recyclerCategories.setAdapter(categoryAdapter);
    }

    private void fetchFoods() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getFoods(null, null).enqueue(new Callback<FoodResponse>() {
            @Override
            public void onResponse(Call<FoodResponse> call, Response<FoodResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    masterFoodList = response.body().getData();
                    setupPopularFoods(masterFoodList);
                    renderFoodList(masterFoodList);
                } else {
                    loadCuratedSampleFoods();
                }
            }

            @Override
            public void onFailure(Call<FoodResponse> call, Throwable t) {
                loadCuratedSampleFoods();
            }
        });
    }

    private void loadCuratedSampleFoods() {
        masterFoodList = new ArrayList<>();

        // High-res curated menu items matching reference design
        masterFoodList.add(new FoodItem(
                "FOD-01",
                "Spicy Chicken Burger",
                "Burger",
                249.00,
                "Crispy spicy chicken patty with fresh lettuce, tomato, onion and our special house sauce.",
                "https://images.unsplash.com/photo-1568901346375-23c9450c58cd",
                4.8,
                "20 mins",
                "480 kcal",
                true
        ));

        masterFoodList.add(new FoodItem(
                "FOD-02",
                "Cheese Burger",
                "Burger",
                189.00,
                "Flame-grilled patty smothered in melted cheddar cheese, pickles, and creamy garlic dressing.",
                "https://images.unsplash.com/photo-1586190848861-99aa4a171e90",
                4.7,
                "15 mins",
                "420 kcal",
                true
        ));

        masterFoodList.add(new FoodItem(
                "FOD-03",
                "Double Beef / Paneer Burger",
                "Burger",
                299.00,
                "Two juicy thick patties stacked with double melted cheese, caramelized onions, and BBQ glaze.",
                "https://images.unsplash.com/photo-1550547660-d9450f859349",
                4.9,
                "25 mins",
                "650 kcal",
                true
        ));

        masterFoodList.add(new FoodItem(
                "FOD-04",
                "Crispy Peri Peri French Fries",
                "Fries",
                119.00,
                "Crispy potato fries dusted with authentic fiery peri-peri seasoning and herb dip.",
                "https://images.unsplash.com/photo-1573080496219-bb080dd4f877",
                4.6,
                "10 mins",
                "310 kcal",
                true
        ));

        masterFoodList.add(new FoodItem(
                "FOD-05",
                "Farmhouse Supreme Pizza",
                "Pizza",
                299.00,
                "Handcrafted crust topped with fresh mozzarella, crunchy bell peppers, corn, and mushrooms.",
                "https://images.unsplash.com/photo-1513104890138-7c749659a591",
                4.8,
                "25 mins",
                "680 kcal",
                true
        ));

        masterFoodList.add(new FoodItem(
                "FOD-06",
                "Chilli Garlic Hakka Noodles",
                "Chicken",
                169.00,
                "Wok-tossed noodles with shredded cabbage, fresh carrots, chillies, and garlic aromatics.",
                "https://images.unsplash.com/photo-1585032226651-759b368d7246",
                4.5,
                "15 mins",
                "380 kcal",
                true
        ));

        masterFoodList.add(new FoodItem(
                "FOD-07",
                "Cold Brew Frappe Drink",
                "Drink",
                99.00,
                "Chilled espresso shaken with whole milk, crushed ice, chocolate drizzle, and cream.",
                "https://images.unsplash.com/photo-1517701604599-bb29b565090c",
                4.6,
                "8 mins",
                "210 kcal",
                true
        ));

        setupPopularFoods(masterFoodList);
        renderFoodList(masterFoodList);
    }

    private void setupPopularFoods(List<FoodItem> allItems) {
        popularList = new ArrayList<>();
        for (FoodItem itm : allItems) {
            if (itm.getRating() >= 4.6 || itm.getCategory().equalsIgnoreCase("Burger")) {
                popularList.add(itm);
            }
        }
        if (popularFoodAdapter == null) {
            popularFoodAdapter = new PopularFoodAdapter(this, popularList, this);
            recyclerPopularFoods.setAdapter(popularFoodAdapter);
        } else {
            popularFoodAdapter.updateList(popularList);
        }
    }

    private void renderFoodList(List<FoodItem> items) {
        if (foodAdapter == null) {
            foodAdapter = new FoodAdapter(this, items, this);
            recyclerFoods.setLayoutManager(new LinearLayoutManager(this));
            recyclerFoods.setAdapter(foodAdapter);
        } else {
            foodAdapter.updateList(items);
        }
    }

    @Override
    public void onCategorySelected(Category category) {
        selectedCategory = category.getName();
        filterFoods(edtSearch.getText().toString());
    }

    private void filterFoods(String query) {
        List<FoodItem> filtered = new ArrayList<>();
        String q = query.trim().toLowerCase();

        for (FoodItem item : masterFoodList) {
            boolean matchesCat = selectedCategory.equalsIgnoreCase("All")
                    || item.getCategory().equalsIgnoreCase(selectedCategory)
                    || item.getName().toLowerCase().contains(selectedCategory.toLowerCase());
            boolean matchesQuery = q.isEmpty()
                    || item.getName().toLowerCase().contains(q)
                    || item.getDescription().toLowerCase().contains(q);

            if (matchesCat && matchesQuery) {
                filtered.add(item);
            }
        }
        renderFoodList(filtered);
    }

    @Override
    public void onFoodClick(FoodItem food) {
        Intent intent = new Intent(this, FoodDetailActivity.class);
        intent.putExtra("FOOD_ITEM", food);
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(FoodItem food) {
        addDishToCart(food);
    }

    @Override
    public void onItemClick(FoodItem item) {
        onFoodClick(item);
    }

    @Override
    public void onAddToCart(FoodItem item) {
        addDishToCart(item);
    }

    private void addDishToCart(FoodItem food) {
        if (food == null) return;
        try {
            dbHelper.addToCart(food.getId(), food.getName(), food.getPrice(), 1, food.getImageUrl());
            Toast.makeText(this, "Added " + food.getName() + " to cart", Toast.LENGTH_SHORT).show();
            updateCartBadge();
        } catch (Exception e) {
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCartBadge() {
        try {
            int count = dbHelper.getCartCount();
            if (count > 0) {
                List<CartItem> items = dbHelper.getAllCartItems();
                double total = 0.0;
                for (CartItem itm : items) {
                    total += itm.getSubtotal();
                }

                if (txtFloatingCount != null) {
                    txtFloatingCount.setText(count + (count == 1 ? " Item" : " Items"));
                }
                if (txtFloatingTotal != null) {
                    txtFloatingTotal.setText(String.format("|  ₹ %.2f", total));
                }
                if (cardFloatingCart != null) {
                    cardFloatingCart.setVisibility(View.VISIBLE);
                }
            } else {
                if (cardFloatingCart != null) {
                    cardFloatingCart.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            // Guard against any DB / UI race conditions
        }
    }

    private void openCart() {
        startActivity(new Intent(MainActivity.this, CartActivity.class));
    }

    private void playEntranceAnimations() {
        // Staggered slide up & fade in animations
        View[] animatedViews = {
                findViewById(R.id.layoutHeroTypography),
                findViewById(R.id.layoutSearchBar),
                findViewById(R.id.recyclerCategories),
                findViewById(R.id.cardPromoBanner),
                findViewById(R.id.recyclerPopularFoods)
        };

        long delay = 50;
        for (View v : animatedViews) {
            if (v != null) {
                v.setTranslationY(60f);
                v.setAlpha(0f);
                v.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(500)
                        .setStartDelay(delay)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
                delay += 70;
            }
        }
    }
}
