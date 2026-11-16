package com.foodiehub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodiehub.R;
import com.foodiehub.adapters.CategoryAdapter;
import com.foodiehub.adapters.FoodAdapter;
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

public class MainActivity extends AppCompatActivity implements FoodAdapter.OnFoodClickListener, CategoryAdapter.OnCategorySelectedListener {

    private TextView txtGreeting, txtCartBadge, txtFloatingCount, txtFloatingTotal;
    private EditText edtSearch;
    private FrameLayout btnCart;
    private ImageButton btnHistory;
    private CardView cardFloatingCart;

    private RecyclerView recyclerCategories, recyclerFoods;
    private CategoryAdapter categoryAdapter;
    private FoodAdapter foodAdapter;

    private DatabaseHelper dbHelper;
    private User currentUser;
    private List<FoodItem> masterFoodList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private String selectedCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        currentUser = dbHelper.getLoggedInUser();

        initViews();
        setupCategories();
        fetchFoods();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int count, int after) {
                filterFoods(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnCart.setOnClickListener(v -> openCart());
        cardFloatingCart.setOnClickListener(v -> openCart());
        btnHistory.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class)));

        ImageButton btnLogout = findViewById(R.id.btnNavLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                dbHelper.logout();
                Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finishAffinity();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void initViews() {
        txtGreeting = findViewById(R.id.txtGreeting);
        txtCartBadge = findViewById(R.id.txtCartBadgeCount);
        txtFloatingCount = findViewById(R.id.txtFloatingCartCount);
        txtFloatingTotal = findViewById(R.id.txtFloatingCartTotal);
        edtSearch = findViewById(R.id.edtSearchFood);
        btnCart = findViewById(R.id.btnNavCart);
        btnHistory = findViewById(R.id.btnNavHistory);
        cardFloatingCart = findViewById(R.id.cardFloatingCart);

        recyclerCategories = findViewById(R.id.recyclerCategories);
        recyclerFoods = findViewById(R.id.recyclerFoods);
        recyclerFoods.setLayoutManager(new LinearLayoutManager(this));

        if (currentUser != null) {
            txtGreeting.setText("Welcome, " + currentUser.getName() + " 👋");
        }
    }

    private void setupCategories() {
        categoryList.add(new Category("CAT-01", "All", "ic_all"));
        categoryList.add(new Category("CAT-02", "Burgers", "ic_burger"));
        categoryList.add(new Category("CAT-03", "Pizzas", "ic_pizza"));
        categoryList.add(new Category("CAT-04", "Asian", "ic_noodles"));
        categoryList.add(new Category("CAT-05", "Beverages", "ic_coffee"));
        categoryList.add(new Category("CAT-06", "Desserts", "ic_dessert"));

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
        masterFoodList.add(new FoodItem("FOD-01", "Classic Paneer Tikka Burger", "Burgers", 189.00, "Grilled spiced paneer patty with mint mayo and crisp lettuce", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd", 4.7, "20 mins", "450 kcal", true));
        masterFoodList.add(new FoodItem("FOD-02", "Farmhouse Supreme Pizza", "Pizzas", 299.00, "Fresh pan crust loaded with mozzarella, bell peppers, corn, and mushrooms", "https://images.unsplash.com/photo-1513104890138-7c749659a591", 4.8, "25 mins", "680 kcal", true));
        masterFoodList.add(new FoodItem("FOD-03", "Chilli Garlic Hakka Noodles", "Asian", 169.00, "Wok-tossed noodles with shredded cabbage, carrots, chillies, and garlic aromatics", "https://images.unsplash.com/photo-1585032226651-759b368d7246", 4.5, "15 mins", "380 kcal", true));
        masterFoodList.add(new FoodItem("FOD-04", "Cold Brew Frappe Coffee", "Beverages", 99.00, "Rich espresso blended with chilled milk, cocoa drizzle, and whipped cream", "https://images.unsplash.com/photo-1517701604599-bb29b565090c", 4.6, "10 mins", "210 kcal", true));
        masterFoodList.add(new FoodItem("FOD-05", "Sizzling Chocolate Brownie", "Desserts", 149.00, "Warm fudgy walnut brownie smothered with rich Belgian chocolate ganache", "https://images.unsplash.com/photo-1606313564200-e75d5e30476c", 4.9, "12 mins", "520 kcal", true));
        masterFoodList.add(new FoodItem("FOD-06", "Crispy Peri Peri French Fries", "Burgers", 119.00, "Golden potato fries dusted with authentic African peri-peri spice mix", "https://images.unsplash.com/photo-1573080496219-bb080dd4f877", 4.6, "10 mins", "310 kcal", true));
        masterFoodList.add(new FoodItem("FOD-07", "Margherita Basil Pizza", "Pizzas", 249.00, "Classic pizza with San Marzano tomato sauce, fresh mozzarella, and basil", "https://images.unsplash.com/photo-1604382355076-af4b0eb60143", 4.7, "20 mins", "550 kcal", true));
        masterFoodList.add(new FoodItem("FOD-08", "Veg Manchurian Gravy Bowl", "Asian", 179.00, "Deep-fried vegetable dumplings simmered in a spicy, tangy Indo-Chinese gravy", "https://images.unsplash.com/photo-1541832676-9b763b0239ab", 4.4, "18 mins", "410 kcal", true));

        renderFoodList(masterFoodList);
    }

    private void renderFoodList(List<FoodItem> items) {
        if (foodAdapter == null) {
            foodAdapter = new FoodAdapter(this, items, this);
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
            boolean matchesCat = selectedCategory.equalsIgnoreCase("All") || item.getCategory().equalsIgnoreCase(selectedCategory);
            boolean matchesQuery = q.isEmpty() || item.getName().toLowerCase().contains(q) || item.getDescription().toLowerCase().contains(q);

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
                if (txtCartBadge != null) {
                    txtCartBadge.setText(String.valueOf(count));
                    txtCartBadge.setVisibility(View.VISIBLE);
                }

                List<CartItem> items = dbHelper.getAllCartItems();
                double total = 0.0;
                for (CartItem itm : items) {
                    total += itm.getSubtotal();
                }

                if (txtFloatingCount != null) {
                    txtFloatingCount.setText(count + (count == 1 ? " Item" : " Items"));
                }
                if (txtFloatingTotal != null) {
                    txtFloatingTotal.setText(String.format("₹ %.2f", total));
                }
                if (cardFloatingCart != null) {
                    cardFloatingCart.setVisibility(View.VISIBLE);
                }
            } else {
                if (txtCartBadge != null) txtCartBadge.setVisibility(View.GONE);
                if (cardFloatingCart != null) cardFloatingCart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            // Guard against any UI update issues
        }
    }

    private void openCart() {
        startActivity(new Intent(MainActivity.this, CartActivity.class));
    }
}
