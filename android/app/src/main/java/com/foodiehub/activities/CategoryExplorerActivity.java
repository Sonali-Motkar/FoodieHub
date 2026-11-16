package com.foodiehub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodiehub.R;
import com.foodiehub.adapters.RestaurantAdapter;
import com.foodiehub.database.DatabaseHelper;
import com.foodiehub.models.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class CategoryExplorerActivity extends AppCompatActivity implements RestaurantAdapter.OnRestaurantClickListener {

    private FrameLayout btnBack, btnCart;
    private EditText edtSearch;
    private TextView txtTitle, txtSubtitle, txtRestaurantCount;
    private TextView chipFilter, chipSortBy, chipFastDelivery, chipPureVeg, chipRating4Plus, chipCostUnder300;
    private RecyclerView recyclerStores;
    private CardView cardStickyCart;
    private TextView txtStickyCount;

    private RestaurantAdapter adapter;
    private DatabaseHelper dbHelper;
    private String categoryName = "Biryani";
    private List<Restaurant> masterStoresList = new ArrayList<>();
    private List<Restaurant> filteredStoresList = new ArrayList<>();

    private boolean filterRatingApplied = false;
    private boolean filterVegApplied = false;
    private boolean filterFastApplied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_explorer);

        dbHelper = new DatabaseHelper(this);

        String passedCat = getIntent().getStringExtra("CATEGORY_NAME");
        if (passedCat != null && !passedCat.trim().isEmpty()) {
            categoryName = passedCat.trim();
        }

        initViews();
        setupCategoryHeader();
        loadStoresForCategory();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStickyCartState();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnCategoryBack);
        btnCart = findViewById(R.id.btnCategoryCart);
        edtSearch = findViewById(R.id.edtCategorySearch);
        txtTitle = findViewById(R.id.txtCategoryTitle);
        txtSubtitle = findViewById(R.id.txtCategorySubtitle);
        txtRestaurantCount = findViewById(R.id.txtRestaurantCount);

        chipFilter = findViewById(R.id.chipFilter);
        chipSortBy = findViewById(R.id.chipSortBy);
        chipFastDelivery = findViewById(R.id.chipFastDelivery);
        chipPureVeg = findViewById(R.id.chipPureVeg);
        chipRating4Plus = findViewById(R.id.chipRating4Plus);
        chipCostUnder300 = findViewById(R.id.chipCostUnder300);

        recyclerStores = findViewById(R.id.recyclerCategoryStores);
        cardStickyCart = findViewById(R.id.cardCategoryStickyCart);
        txtStickyCount = findViewById(R.id.txtCategoryStickyCount);

        recyclerStores.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupCategoryHeader() {
        txtTitle.setText(categoryName);

        String lower = categoryName.toLowerCase();
        if (lower.contains("biryani")) {
            txtSubtitle.setText("Taste these delectable classics, delectable biryanis to make your day.");
        } else if (lower.contains("pizza")) {
            txtSubtitle.setText("Cheesy, piping hot slices and gourmet crusts crafted for you.");
        } else if (lower.contains("pastry")) {
            txtSubtitle.setText("Fall in love with these perfect slices of heavenly pastries.");
        } else if (lower.contains("cake")) {
            txtSubtitle.setText("Feast on amazing cakes to satisfy your sweet tooth.");
        } else if (lower.contains("north")) {
            txtSubtitle.setText("Indulge with the best of North Indian cuisines.");
        } else if (lower.contains("burger")) {
            txtSubtitle.setText("Juicy patties, melted cheese and freshly toasted brioche buns.");
        } else if (lower.contains("chinese")) {
            txtSubtitle.setText("Wok-tossed noodles, spicy gravies, and authentic oriental flavours.");
        } else if (lower.contains("south")) {
            txtSubtitle.setText("Crispy dosas, fluffy idlis and steaming filter coffee.");
        } else {
            txtSubtitle.setText("Explore the most loved stores and signature dishes in Pune.");
        }
    }

    private void loadStoresForCategory() {
        masterStoresList = new ArrayList<>();
        String lower = categoryName.toLowerCase();

        if (lower.contains("biryani")) {
            // Authentic stores from User Reference Screenshot 1 & 2
            masterStoresList.add(new Restaurant(
                    "REST-BIKKGANE", "Bikkgane Biryani", 4.2, "30-35 mins",
                    "Biryani, Hyderabadi, Andhra, Rolls", "Hinjewadi",
                    "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8", true, "ITEMS AT ₹149"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-BEHROUZ", "Behrouz Biryani", 4.0, "30-35 mins",
                    "Royal Biryani, Mughlai, Kebabs", "Hinjawadi",
                    "https://images.unsplash.com/photo-1589302168068-964664d93dc0", true, "50% OFF UPTO ₹100"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-BIRYANILIFE", "The Biryani Life", 4.0, "25-30 mins",
                    "Biryani, Mughlai, Lucknowi", "Hinjawadi",
                    "https://images.unsplash.com/photo-1633945274405-b6c8069047b0", true, "FREE DESSERT"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-BADSHAH", "Biryani Badshah", 4.1, "30-35 mins",
                    "Biryani, Hyderabadi, Andhra", "Hinjewadi",
                    "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8", false, "ITEMS AT ₹179"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-SAIRAT", "Sairat Biryani", 4.4, "20-25 mins",
                    "Biryani, North Indian, Kebabs", "Ravet",
                    "https://images.unsplash.com/photo-1589302168068-964664d93dc0", true, "ITEMS AT ₹149"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-ITC-CHEF", "ITC Master Chef Creations", 4.2, "30-40 mins",
                    "North Indian, Biryani, Kebabs, Rolls", "Ravet",
                    "https://images.unsplash.com/photo-1633945274405-b6c8069047b0", true, "ITEMS AT ₹199"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-1881-DUM", "1881 Dum House: Lucknow's", 4.6, "20-30 mins",
                    "North Indian, Biryani, Awadhi", "Ravet South",
                    "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8", true, "TOP RATED"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-HOUSE-BIRYAN", "House of Biryan", 3.9, "25-30 mins",
                    "Biryani, Kebabs, Rolls, North Indian", "Hinjewadi",
                    "https://images.unsplash.com/photo-1589302168068-964664d93dc0", true, "ITEMS AT ₹129"
            ));
        } else if (lower.contains("pizza")) {
            masterStoresList.add(new Restaurant(
                    "REST-DOMINOS", "Domino's Pizza", 4.3, "20-25 mins",
                    "Pizzas, Fast Food, Garlic Bread", "Tathawade",
                    "https://images.unsplash.com/photo-1513104890138-7c749659a591", true, "MEALS FROM ₹99"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-OVENSTORY", "Ovenstory Pizza", 4.1, "25-30 mins",
                    "Pizzas, Italian, Pastas, Cheesy", "Hinjewadi",
                    "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38", true, "BUY 1 GET 1 FREE"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-PIZZAHUT", "Pizza Hut", 4.0, "30-35 mins",
                    "Pizzas, Pasta, Wings", "Wakad",
                    "https://images.unsplash.com/photo-1534308983496-4fabb1a015ee", true, "ITEMS AT ₹179"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-LAPINOZ", "La Pino'z Pizza", 4.2, "20-25 mins",
                    "Pizzas, Giant Slices, Italian", "Ravet",
                    "https://images.unsplash.com/photo-1574071318508-1cdbab80d002", true, "40% OFF UPTO ₹80"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-MOJOPIZZA", "Mojo Pizza - 2X Toppings", 4.4, "25-30 mins",
                    "Pizzas, Desserts, Wings", "Tathawade",
                    "https://images.unsplash.com/photo-1513104890138-7c749659a591", true, "FLAT 50% OFF"
            ));
        } else if (lower.contains("pastry") || lower.contains("cake")) {
            // Authentic stores from User Reference Screenshot 3 & 4
            masterStoresList.add(new Restaurant(
                    "REST-BAKINGO", "Bakingo", 4.4, "30-35 mins",
                    "Bakery, Desserts, Beverages, Snacks", "Hinjawadi",
                    "https://images.unsplash.com/photo-1578985545062-69928b1d9587", true, "FREE CANDLE SET"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-CAKEMAN", "Cakeman", 4.5, "25-30 mins",
                    "Desserts, Bakery, Cakes & Pastries", "Pimple Saudagar",
                    "https://images.unsplash.com/photo-1588195538326-c5b1e9f80a1b", true, "ITEMS AT ₹99"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-SWEETTRUTH", "Sweet Truth - Cake and Desserts", 4.3, "20-25 mins",
                    "Desserts, Ice Cream, Bakery", "Hinjawadi",
                    "https://images.unsplash.com/photo-1551024709-8f23befc6f87", true, "60% OFF UPTO ₹120"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-ITC-SUNFEAST", "ITC Sunfeast Baked Creations", 4.4, "25-30 mins",
                    "Bakery, Dessert, Cakes & Pastries", "Hinjewadi",
                    "https://images.unsplash.com/photo-1578985545062-69928b1d9587", true, "ITEMS AT ₹119"
            ));
        } else if (lower.contains("north")) {
            // Authentic stores from User Reference Screenshot 5
            masterStoresList.add(new Restaurant(
                    "REST-ITC-AASHIRVAAD", "ITC Aashirvaad Soul Creations", 4.2, "30-35 mins",
                    "North Indian, Thalis, Pure Veg, Healthy", "Ravet",
                    "https://images.unsplash.com/photo-1589302168068-964664d93dc0", true, "PURE VEG • THALIS"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-CHRISTY", "Christy Restaurant", 3.5, "30-40 mins",
                    "Chinese, Biryani, North Indian", "Nigdi",
                    "https://images.unsplash.com/photo-1585032226651-759b368d7246", false, "ITEMS AT ₹139"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-ITC-CHEF-NI", "ITC Master Chef Creations", 4.2, "30-35 mins",
                    "North Indian, Biryani, Kebabs, Rolls", "Ravet",
                    "https://images.unsplash.com/photo-1633945274405-b6c8069047b0", true, "ITEMS AT ₹199"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-ROLLSKING", "Rollsking", 4.0, "30-35 mins",
                    "Rolls & Wraps, Fast Food, Snacks", "Phase 1",
                    "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46", true, "40% OFF"
            ));
        } else {
            // Default Burger & Pune favorites
            masterStoresList.add(new Restaurant(
                    "REST-WENDYS", "Wendy's Burgers", 4.2, "25-30 mins",
                    "Burgers, Fast Food, wrap", "Ravet",
                    "https://images.unsplash.com/photo-1586190848861-99aa4a171e90", true, "ITEMS AT ₹79"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-KFC", "KFC", 3.8, "25-30 mins",
                    "Burgers, Fast Food, Rolls & Wraps", "Puna wale",
                    "https://images.unsplash.com/photo-1513639776629-7b61b0ac49cb", true, "ITEMS AT ₹199"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-BK", "Burger King", 3.9, "25-30 mins",
                    "Burgers, American", "Tathawade",
                    "https://images.unsplash.com/photo-1550547660-d9450f859349", true, "ITEMS AT ₹99"
            ));
            masterStoresList.add(new Restaurant(
                    "REST-SUBWAY", "Subway", 4.2, "15-20 mins",
                    "Sandwich, Salads, Wrap, Healthy", "Tathawade",
                    "https://images.unsplash.com/photo-1509722747041-616f39b57569", true, "40% OFF UPTO ₹80"
            ));
        }

        filteredStoresList = new ArrayList<>(masterStoresList);
        adapter = new RestaurantAdapter(this, filteredStoresList, this);
        recyclerStores.setAdapter(adapter);

        txtRestaurantCount.setText(masterStoresList.size() + " Restaurants to explore in Pune");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCart.setOnClickListener(v -> {
            startActivity(new Intent(CategoryExplorerActivity.this, CartActivity.class));
        });

        cardStickyCart.setOnClickListener(v -> {
            startActivity(new Intent(CategoryExplorerActivity.this, CartActivity.class));
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        chipRating4Plus.setOnClickListener(v -> {
            filterRatingApplied = !filterRatingApplied;
            toggleChipAppearance(chipRating4Plus, filterRatingApplied);
            applyFilters();
        });

        chipPureVeg.setOnClickListener(v -> {
            filterVegApplied = !filterVegApplied;
            toggleChipAppearance(chipPureVeg, filterVegApplied);
            applyFilters();
        });

        chipFastDelivery.setOnClickListener(v -> {
            filterFastApplied = !filterFastApplied;
            toggleChipAppearance(chipFastDelivery, filterFastApplied);
            applyFilters();
        });

        chipSortBy.setOnClickListener(v -> {
            Toast.makeText(this, "Sorted by: Relevance & Delivery Time", Toast.LENGTH_SHORT).show();
        });

        chipFilter.setOnClickListener(v -> {
            Toast.makeText(this, "Showing top " + categoryName + " outlets in Pune", Toast.LENGTH_SHORT).show();
        });

        chipCostUnder300.setOnClickListener(v -> {
            Toast.makeText(this, "Filtering budget meals under ₹300", Toast.LENGTH_SHORT).show();
        });
    }

    private void toggleChipAppearance(TextView chip, boolean active) {
        if (active) {
            chip.setBackgroundResource(R.drawable.bg_pill_selected);
            chip.setTextColor(getResources().getColor(R.color.white));
        } else {
            chip.setBackgroundResource(R.drawable.bg_glass_card);
            chip.setTextColor(getResources().getColor(R.color.text_white));
        }
    }

    private void applyFilters() {
        String query = edtSearch.getText().toString().trim().toLowerCase();
        List<Restaurant> result = new ArrayList<>();

        for (Restaurant r : masterStoresList) {
            boolean matchesSearch = query.isEmpty()
                    || r.getName().toLowerCase().contains(query)
                    || r.getCuisineTags().toLowerCase().contains(query)
                    || r.getArea().toLowerCase().contains(query);

            boolean matchesRating = !filterRatingApplied || r.getRating() >= 4.0;
            boolean matchesVeg = !filterVegApplied || r.getCuisineTags().toLowerCase().contains("veg");
            boolean matchesFast = !filterFastApplied || r.getDeliveryTime().contains("15") || r.getDeliveryTime().contains("20");

            if (matchesSearch && matchesRating && matchesVeg && matchesFast) {
                result.add(r);
            }
        }

        filteredStoresList = result;
        adapter.updateList(filteredStoresList);
        txtRestaurantCount.setText(filteredStoresList.size() + " Restaurants to explore in Pune");
    }

    private void updateStickyCartState() {
        int count = dbHelper.getCartCount();
        if (count > 0) {
            cardStickyCart.setVisibility(View.VISIBLE);
            txtStickyCount.setText(count + (count == 1 ? " ITEM ADDED" : " ITEMS ADDED"));
        } else {
            cardStickyCart.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRestaurantClick(Restaurant restaurant) {
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra("RESTAURANT", restaurant);
        startActivity(intent);
    }
}
