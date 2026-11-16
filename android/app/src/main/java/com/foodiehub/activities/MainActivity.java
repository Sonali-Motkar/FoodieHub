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
import com.foodiehub.adapters.RestaurantAdapter;
import com.foodiehub.database.DatabaseHelper;
import com.foodiehub.models.CartItem;
import com.foodiehub.models.Category;
import com.foodiehub.models.FoodItem;
import com.foodiehub.models.Restaurant;
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
        PopularFoodAdapter.OnPopularItemListener,
        RestaurantAdapter.OnRestaurantClickListener {

    private TextView txtGreeting, txtHeroHeadline, txtFloatingCount, txtFloatingTotal, txtViewAllPopular;
    private TextView txtUserAddressHeader, txtSeeAllRestaurants;
    private LinearLayout layoutLocationSelector;
    private EditText edtSearch;
    private FrameLayout btnGlassMenu, btnNavLogout;
    private ImageView btnFilterTune, imgPromoCombo;
    private Button btnPromoOrderNow;
    private CardView cardPromoBanner, cardFloatingCart;

    private RecyclerView recyclerCategories, recyclerRestaurants, recyclerPopularFoods, recyclerFoods;
    private CategoryAdapter categoryAdapter;
    private RestaurantAdapter restaurantAdapter;
    private PopularFoodAdapter popularFoodAdapter;
    private FoodAdapter foodAdapter;

    private LinearLayout navBtnHome, navBtnOffers, navBtnOrders, navBtnFavorites;

    private DatabaseHelper dbHelper;
    private User currentUser;
    private List<FoodItem> masterFoodList = new ArrayList<>();
    private List<FoodItem> popularList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private List<Restaurant> restaurantList = new ArrayList<>();
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
        setupRestaurants();
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
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        btnGlassMenu.setOnClickListener(v -> {
            Toast.makeText(this, "FoodieHub Dark Glass UI • Swiggy Edition", Toast.LENGTH_SHORT).show();
        });

        btnFilterTune.setOnClickListener(v -> {
            Toast.makeText(this, "Filters applied: Top Rated in Pune (4.0+ ★)", Toast.LENGTH_SHORT).show();
        });

        txtViewAllPopular.setOnClickListener(v -> {
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
        currentUser = dbHelper.getLoggedInUser();
        if (currentUser != null && currentUser.getName() != null) {
            txtGreeting.setText("Hi, " + currentUser.getName().split(" ")[0] + " 👋");
            if (currentUser.getAddress() != null && !currentUser.getAddress().isEmpty()) {
                txtUserAddressHeader.setText(currentUser.getAddress());
            }
        }
    }

    private void initViews() {
        txtGreeting = findViewById(R.id.txtGreeting);
        txtHeroHeadline = findViewById(R.id.txtHeroHeadline);
        txtFloatingCount = findViewById(R.id.txtFloatingCartCount);
        txtFloatingTotal = findViewById(R.id.txtFloatingCartTotal);
        txtViewAllPopular = findViewById(R.id.txtViewAllPopular);
        txtUserAddressHeader = findViewById(R.id.txtUserAddressHeader);
        txtSeeAllRestaurants = findViewById(R.id.txtSeeAllRestaurants);
        layoutLocationSelector = findViewById(R.id.layoutLocationSelector);
        edtSearch = findViewById(R.id.edtSearchFood);

        btnGlassMenu = findViewById(R.id.btnGlassMenu);
        btnNavLogout = findViewById(R.id.btnNavLogout);
        btnFilterTune = findViewById(R.id.btnFilterTune);
        cardPromoBanner = findViewById(R.id.cardPromoBanner);
        btnPromoOrderNow = findViewById(R.id.btnPromoOrderNow);
        imgPromoCombo = findViewById(R.id.imgPromoCombo);
        cardFloatingCart = findViewById(R.id.cardFloatingCart);

        recyclerCategories = findViewById(R.id.recyclerCategories);
        recyclerRestaurants = findViewById(R.id.recyclerRestaurants);
        recyclerPopularFoods = findViewById(R.id.recyclerPopularFoods);
        recyclerFoods = findViewById(R.id.recyclerFoods);

        navBtnHome = findViewById(R.id.navBtnHome);
        navBtnOffers = findViewById(R.id.navBtnOffers);
        navBtnOrders = findViewById(R.id.navBtnOrders);
        navBtnFavorites = findViewById(R.id.navBtnFavorites);

        if (currentUser != null && currentUser.getName() != null) {
            txtGreeting.setText("Hi, " + currentUser.getName().split(" ")[0] + " 👋");
            if (currentUser.getAddress() != null && !currentUser.getAddress().isEmpty()) {
                txtUserAddressHeader.setText(currentUser.getAddress());
            } else {
                txtUserAddressHeader.setText("Flat 402, Green Palms, Kothrud, Pune");
            }
        } else {
            txtGreeting.setText("Hi, Sonali 👋");
            txtUserAddressHeader.setText("Jeevan Nagar, Tathawade, Pune...");
        }

        layoutLocationSelector.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        if (txtSeeAllRestaurants != null) {
            txtSeeAllRestaurants.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, CategoryExplorerActivity.class);
                intent.putExtra("CATEGORY_NAME", selectedCategory != null ? selectedCategory : "Biryani");
                startActivity(intent);
            });
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
            View scroll = findViewById(R.id.nestedScrollHome);
            if (scroll != null) scroll.scrollTo(0, 0);
        });

        navBtnOffers.setOnClickListener(v -> {
            Toast.makeText(this, "🎁 20% OFF applied on Wendy's & KFC! Promo: ONEFREE", Toast.LENGTH_LONG).show();
        });

        navBtnOrders.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
        });

        navBtnFavorites.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });
    }

    private void setupCategories() {
        // 14 Authentic Swiggy Categories from Reference Screenshot 1 (media_1794848131140_2370902b.jpg)
        categoryList = new ArrayList<>();
        categoryList.add(new Category("CAT-01", "Burger", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd"));
        categoryList.add(new Category("CAT-02", "Biryani", "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8"));
        categoryList.add(new Category("CAT-03", "Pizza", "https://images.unsplash.com/photo-1513104890138-7c749659a591"));
        categoryList.add(new Category("CAT-04", "North Indian", "https://images.unsplash.com/photo-1589302168068-964664d93dc0"));
        categoryList.add(new Category("CAT-05", "Chinese", "https://images.unsplash.com/photo-1585032226651-759b368d7246"));
        categoryList.add(new Category("CAT-06", "South Indian", "https://images.unsplash.com/photo-1610192244261-3f33de3f55e4"));
        categoryList.add(new Category("CAT-07", "Cake", "https://images.unsplash.com/photo-1578985545062-69928b1d9587"));
        categoryList.add(new Category("CAT-08", "Ice Cream", "https://images.unsplash.com/photo-1497034825429-c343d7c6a68f"));
        categoryList.add(new Category("CAT-09", "Momo", "https://images.unsplash.com/photo-1625220194771-7ebdea0b70b9"));
        categoryList.add(new Category("CAT-10", "Paratha", "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46"));
        categoryList.add(new Category("CAT-11", "Pasta", "https://images.unsplash.com/photo-1551183053-bf91a1d81141"));
        categoryList.add(new Category("CAT-12", "Khichdi", "https://images.unsplash.com/photo-1601050690597-df0568f70950"));
        categoryList.add(new Category("CAT-13", "Pav Bhaji", "https://images.unsplash.com/photo-1606491956689-2ea866880c84"));
        categoryList.add(new Category("CAT-14", "Dosa", "https://images.unsplash.com/photo-1668236543090-82eba5ee5976"));

        categoryAdapter = new CategoryAdapter(this, categoryList, this);
        recyclerCategories.setAdapter(categoryAdapter);
    }

    private void setupRestaurants() {
        // Authentic Top Restaurant Chains in Pune from Reference Screenshot 2 (media_1794848160605_0da368f4.jpg)
        restaurantList = new ArrayList<>();
        restaurantList.add(new Restaurant(
                "REST-WENDYS",
                "Wendy's Burgers",
                4.2,
                "25-30 mins",
                "Burgers, Fast Food, wrap",
                "Ravet",
                "https://images.unsplash.com/photo-1586190848861-99aa4a171e90",
                true,
                "ITEMS AT ₹79"
        ));

        restaurantList.add(new Restaurant(
                "REST-KFC",
                "KFC",
                3.8,
                "25-30 mins",
                "Burgers, Fast Food, Rolls & Wraps",
                "Puna wale",
                "https://images.unsplash.com/photo-1513639776629-7b61b0ac49cb",
                true,
                "ITEMS AT ₹199"
        ));

        restaurantList.add(new Restaurant(
                "REST-BK",
                "Burger King",
                3.9,
                "25-30 mins",
                "Burgers, American",
                "Tathawade",
                "https://images.unsplash.com/photo-1550547660-d9450f859349",
                true,
                "ITEMS AT ₹99"
        ));

        restaurantList.add(new Restaurant(
                "REST-SUBWAY",
                "Subway",
                4.2,
                "15-20 mins",
                "sandwich, Salads, wrap, Healthy...",
                "Tathawade",
                "https://images.unsplash.com/photo-1509722747041-616f39b57569",
                true,
                "40% OFF UPTO ₹80"
        ));

        restaurantList.add(new Restaurant(
                "REST-SAIRAT",
                "Sairat Biryani",
                4.4,
                "20-25 mins",
                "Biryani, North Indian, Kebabs",
                "Ravet",
                "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8",
                true,
                "ITEMS AT ₹149"
        ));

        restaurantList.add(new Restaurant(
                "REST-BIKKGANE",
                "Bikkgane Biryani",
                4.2,
                "30-35 mins",
                "Biryani, Hyderabadi, Andhra, Rolls",
                "Hinjewadi",
                "https://images.unsplash.com/photo-1589302168068-964664d93dc0",
                true,
                "ITEMS AT ₹149"
        ));

        restaurantList.add(new Restaurant(
                "REST-DOMINOS",
                "Domino's Pizza",
                4.3,
                "20-25 mins",
                "Pizzas, Fast Food, Garlic Bread",
                "Tathawade",
                "https://images.unsplash.com/photo-1513104890138-7c749659a591",
                true,
                "MEALS FROM ₹99"
        ));

        restaurantList.add(new Restaurant(
                "REST-BAKINGO",
                "Bakingo",
                4.4,
                "30-35 mins",
                "Bakery, Desserts, Beverages, Snacks",
                "Hinjawadi",
                "https://images.unsplash.com/photo-1578985545062-69928b1d9587",
                true,
                "FREE CANDLE SET"
        ));

        restaurantAdapter = new RestaurantAdapter(this, restaurantList, this);
        recyclerRestaurants.setAdapter(restaurantAdapter);
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

        // Wendy's Burgers & Burgers
        masterFoodList.add(new FoodItem(
                "FOD-01",
                "Crispy Veg Burger.",
                "Burger",
                79.00,
                "Crispy potato and peas patty topped with special mayonnaise, crisp lettuce and onion.",
                "https://images.unsplash.com/photo-1550547660-d9450f859349",
                4.2, "20 mins", "380 kcal", true, "Wendy's Burgers", true, 188, true
        ));

        masterFoodList.add(new FoodItem(
                "FOD-02",
                "Crispy Chicken Burger.",
                "Burger",
                115.00,
                "Crispy fried chicken patty with fresh shredded lettuce and secret house spicy mayo.",
                "https://images.unsplash.com/photo-1568901346375-23c9450c58cd",
                4.2, "25 mins", "460 kcal", true, "Wendy's Burgers", false, 255, true
        ));

        // KFC Special
        masterFoodList.add(new FoodItem(
                "FOD-03",
                "Chicken Krisper With Reg Fries",
                "Burger",
                273.00,
                "Tender juicy chicken fillet inside soft sesame bun with salted fries.",
                "https://images.unsplash.com/photo-1513639776629-7b61b0ac49cb",
                4.3, "25 mins", "560 kcal", true, "KFC", false, 312, true
        ));

        // Sairat Biryani
        masterFoodList.add(new FoodItem(
                "FOD-04",
                "Chicken Tandoori Dum Biryani",
                "Biryani",
                174.00,
                "Aromatic basmati rice layered with smoky tandoori chicken and royal saffron spices.",
                "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8",
                4.4, "20 mins", "580 kcal", true, "Sairat Biryani", false, 340, true
        ));

        // Pizza
        masterFoodList.add(new FoodItem(
                "FOD-05",
                "Farmhouse Supreme Pizza",
                "Pizza",
                299.00,
                "Handcrafted crust topped with fresh mozzarella, crunchy bell peppers, corn, and mushrooms.",
                "https://images.unsplash.com/photo-1513104890138-7c749659a591",
                4.8, "25 mins", "680 kcal", true, "Domino's Pizza", true, 210, true
        ));

        // Chinese
        masterFoodList.add(new FoodItem(
                "FOD-06",
                "Chilli Garlic Hakka Noodles",
                "Chinese",
                169.00,
                "Wok-tossed noodles with shredded cabbage, fresh carrots, chillies, and garlic aromatics.",
                "https://images.unsplash.com/photo-1585032226651-759b368d7246",
                4.5, "15 mins", "380 kcal", true, "Chinese Wok", true, 160, true
        ));

        // North Indian
        masterFoodList.add(new FoodItem(
                "FOD-07",
                "Paneer Butter Masala & Parathas",
                "North Indian",
                229.00,
                "Cubes of fresh paneer simmered in creamy makhani gravy with butter laccha parathas.",
                "https://images.unsplash.com/photo-1589302168068-964664d93dc0",
                4.7, "20 mins", "520 kcal", true, "Punjab Grill", true, 195, true
        ));

        // South Indian
        masterFoodList.add(new FoodItem(
                "FOD-08",
                "Masala Dosa With Sambhar",
                "South Indian",
                120.00,
                "Golden crispy crepe stuffed with spiced potato mash served with coconut chutney.",
                "https://images.unsplash.com/photo-1668236543090-82eba5ee5976",
                4.6, "15 mins", "340 kcal", true, "Wadeshwar", true, 310, true
        ));

        // Ice Cream / Dessert
        masterFoodList.add(new FoodItem(
                "FOD-09",
                "Belgian Chocolate Ice Cream Cup",
                "Ice Cream",
                99.00,
                "Decadent dark chocolate gelato churned with roasted almond slivers.",
                "https://images.unsplash.com/photo-1497034825429-c343d7c6a68f",
                4.9, "10 mins", "240 kcal", true, "Naturals Ice Cream", true, 410, true
        ));

        setupPopularFoods(masterFoodList);
        renderFoodList(masterFoodList);
    }

    private void setupPopularFoods(List<FoodItem> allItems) {
        popularList = new ArrayList<>();
        for (FoodItem itm : allItems) {
            if (itm.getRating() >= 4.2 || itm.getCategory().equalsIgnoreCase("Burger")) {
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

        // Launch dedicated Swiggy Collection Stores Explorer for Biryani, Pizza, Pastry, Cakes, etc.
        Intent intent = new Intent(MainActivity.this, CategoryExplorerActivity.class);
        intent.putExtra("CATEGORY_NAME", category.getName());
        startActivity(intent);
    }

    @Override
    public void onRestaurantClick(Restaurant restaurant) {
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra("RESTAURANT", restaurant);
        startActivity(intent);
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
                    || item.getDescription().toLowerCase().contains(q)
                    || (item.getRestaurantName() != null && item.getRestaurantName().toLowerCase().contains(q));

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
        View[] animatedViews = {
                findViewById(R.id.layoutHeroTypography),
                findViewById(R.id.layoutSearchBar),
                findViewById(R.id.recyclerCategories),
                findViewById(R.id.cardPromoBanner),
                findViewById(R.id.recyclerRestaurants),
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
