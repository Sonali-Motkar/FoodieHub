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
        String nameLower = restaurant.getName().toLowerCase();
        String cuisineLower = (restaurant.getCuisineTags() != null) ? restaurant.getCuisineTags().toLowerCase() : "";

        if (nameLower.contains("biryani") || cuisineLower.contains("biryani") || nameLower.contains("bikkgane") || nameLower.contains("behrouz")) {
            // Authentic Swiggy Biryani Combos
            combosList.add(new FoodItem(
                    "CMB-BIR-01",
                    "Hyderabadi Chicken Biryani Combo",
                    "Combos",
                    349.00,
                    "Authentic dum chicken biryani + spicy peanut salan + cooling raita + 2 Gulab Jamuns.",
                    "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8",
                    4.5, "25 mins", "680 kcal", true, restaurant.getName(), false, 280, true
            ));
            combosList.add(new FoodItem(
                    "CMB-BIR-02",
                    "Royal Veg Dum Biryani Meal",
                    "Combos",
                    299.00,
                    "Fragrant saffron rice with paneer & garden veggies + Dal Makhani + fresh Mint Raita.",
                    "https://images.unsplash.com/photo-1589302168068-964664d93dc0",
                    4.3, "20 mins", "540 kcal", true, restaurant.getName(), true, 195, true
            ));
            combosList.add(new FoodItem(
                    "CMB-BIR-03",
                    "Murg Dum Biryani & Kebab Duo",
                    "Combos",
                    399.00,
                    "Single chicken dum biryani + 2 pc Reshmi Chicken Tikka + chilled Thums Up can.",
                    "https://images.unsplash.com/photo-1633945274405-b6c8069047b0",
                    4.6, "30 mins", "720 kcal", true, restaurant.getName(), false, 320, true
            ));
        } else if (nameLower.contains("pizza") || cuisineLower.contains("pizza") || nameLower.contains("domino") || nameLower.contains("ovenstory")) {
            // Authentic Swiggy Pizza Combos
            combosList.add(new FoodItem(
                    "CMB-PIZ-01",
                    "Farmhouse Supreme Feast (For 2)",
                    "Combos",
                    419.00,
                    "Medium Farmhouse Veggie Pizza + Stuffed Cheesy Garlic Bread + 2 Coke Cans.",
                    "https://images.unsplash.com/photo-1513104890138-7c749659a591",
                    4.5, "25 mins", "620 kcal", true, restaurant.getName(), true, 310, true
            ));
            combosList.add(new FoodItem(
                    "CMB-PIZ-02",
                    "Peppy Paneer Meal Combo",
                    "Combos",
                    369.00,
                    "Medium Peppy Paneer Pizza + 1 Zingy White Sauce Dip + Choco Lava Cake.",
                    "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38",
                    4.4, "20 mins", "560 kcal", true, restaurant.getName(), true, 220, true
            ));
            combosList.add(new FoodItem(
                    "CMB-PIZ-03",
                    "Non-Veg Loaded Pizza Combo",
                    "Combos",
                    489.00,
                    "Medium Barbecue Pepper Chicken Pizza + 4 Pcs Peri Peri Wings + Pepsi.",
                    "https://images.unsplash.com/photo-1534308983496-4fabb1a015ee",
                    4.6, "25 mins", "710 kcal", true, restaurant.getName(), false, 185, true
            ));
        } else if (nameLower.contains("cake") || nameLower.contains("pastry") || nameLower.contains("bakingo") || nameLower.contains("cakeman") || nameLower.contains("sweet truth")) {
            // Authentic Swiggy Cake & Pastry Combos
            combosList.add(new FoodItem(
                    "CMB-CAK-01",
                    "Choco Truffle Pastry Duo + Cold Brew",
                    "Combos",
                    289.00,
                    "2 slices of signature Belgian Dark Chocolate Truffle + Chilled Caramel Cold Coffee.",
                    "https://images.unsplash.com/photo-1578985545062-69928b1d9587",
                    4.7, "20 mins", "460 kcal", true, restaurant.getName(), true, 260, true
            ));
            combosList.add(new FoodItem(
                    "CMB-CAK-02",
                    "Assorted Pastry Box (4 Flavours)",
                    "Combos",
                    429.00,
                    "Dutch Truffle, Red Velvet Cream Cheese, Blueberry Cheesecake & Nutella Crunch.",
                    "https://images.unsplash.com/photo-1588195538326-c5b1e9f80a1b",
                    4.8, "15 mins", "680 kcal", true, restaurant.getName(), true, 310, true
            ));
        } else if (nameLower.contains("north") || cuisineLower.contains("north") || nameLower.contains("thali") || nameLower.contains("aashirvaad") || nameLower.contains("rollsking")) {
            // Authentic North Indian Combos
            combosList.add(new FoodItem(
                    "CMB-NI-01",
                    "Royal Punjab Deluxe Thali",
                    "Combos",
                    329.00,
                    "Paneer Butter Masala + Dal Makhani + 2 Butter Naan + Jeera Rice + Salad + Sweet.",
                    "https://images.unsplash.com/photo-1589302168068-964664d93dc0",
                    4.6, "25 mins", "620 kcal", true, restaurant.getName(), true, 410, true
            ));
            combosList.add(new FoodItem(
                    "CMB-NI-02",
                    "Boneless Butter Chicken Thali Box",
                    "Combos",
                    369.00,
                    "Rich Murg Makhani + Dal Makhani + 2 Laccha Parathas + Steamed Basmati Rice.",
                    "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46",
                    4.7, "25 mins", "710 kcal", true, restaurant.getName(), false, 345, true
            ));
        } else if (nameLower.contains("kfc")) {
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
            // Default Wendy's / Burger King style combos
            combosList.add(new FoodItem(
                    "CMB-01",
                    "Teriyaki Crispy Veg Burger",
                    "Combos",
                    313.00,
                    "Teriyaki glazed crispy patty with herb dip and fries.",
                    "https://images.unsplash.com/photo-1550547660-d9450f859349",
                    4.3, "20 mins", "510 kcal", true, restaurant.getName(), true, 95, true
            ));
            combosList.add(new FoodItem(
                    "CMB-02",
                    "Crispy Chicken Burger Combo",
                    "Combos",
                    313.00,
                    "Crispy fried chicken burger + salted fries + Pepsi can.",
                    "https://images.unsplash.com/photo-1568901346375-23c9450c58cd",
                    4.4, "25 mins", "620 kcal", true, restaurant.getName(), false, 140, true
            ));
            combosList.add(new FoodItem(
                    "CMB-03",
                    "Crispy Veg Burger Combo",
                    "Combos",
                    296.00,
                    "Crispy veg burger + medium fries + soft beverage.",
                    "https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5",
                    4.2, "20 mins", "490 kcal", true, restaurant.getName(), true, 80, true
            ));
        }

        comboAdapter = new RestaurantComboAdapter(this, combosList, this);
        recyclerCombos.setAdapter(comboAdapter);
    }

    private void setupRecommendedDishes() {
        recommendedList = new ArrayList<>();
        String nameLower = restaurant.getName().toLowerCase();
        String cuisineLower = (restaurant.getCuisineTags() != null) ? restaurant.getCuisineTags().toLowerCase() : "";

        if (nameLower.contains("biryani") || cuisineLower.contains("biryani") || nameLower.contains("bikkgane") || nameLower.contains("behrouz") || nameLower.contains("sairat")) {
            // Authentic Swiggy Biryani Dishes
            recommendedList.add(new FoodItem(
                    "BIR-01",
                    "Hyderabadi Chicken Dum Biryani",
                    "Biryani",
                    279.00,
                    "Authentic kacchi dum biryani layered with marinated succulent chicken and saffron long-grain basmati.",
                    "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8",
                    4.5, "25 mins", "620 kcal", true, restaurant.getName(), false, 312, true
            ));
            recommendedList.add(new FoodItem(
                    "BIR-02",
                    "Dum Gosht Biryani (Mutton)",
                    "Biryani",
                    449.00,
                    "Melt-in-mouth tender cuts of mutton slow-cooked with caramelized onions, fresh mint and whole spices.",
                    "https://images.unsplash.com/photo-1589302168068-964664d93dc0",
                    4.6, "30 mins", "710 kcal", true, restaurant.getName(), false, 184, true
            ));
            recommendedList.add(new FoodItem(
                    "BIR-03",
                    "Special Paneer Tikka Biryani",
                    "Biryani",
                    249.00,
                    "Chargrilled cottage cheese tikka cubes infused in rich biryani gravy and aromatic basmati rice.",
                    "https://images.unsplash.com/photo-1633945274405-b6c8069047b0",
                    4.3, "20 mins", "530 kcal", true, restaurant.getName(), true, 220, true
            ));
            recommendedList.add(new FoodItem(
                    "BIR-04",
                    "Murgh Malai Tikka Kebab (6 Pcs)",
                    "Kebabs",
                    289.00,
                    "Velvety boneless chicken kebabs marinated in cashew cream, green cardamom and charred in clay tandoor.",
                    "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28",
                    4.5, "20 mins", "420 kcal", true, restaurant.getName(), false, 195, true
            ));
            recommendedList.add(new FoodItem(
                    "BIR-05",
                    "Spicy Mirchi Ka Salan & Raita Bowl",
                    "Sides",
                    79.00,
                    "Traditional Hyderabadi peanut and sesame chilli gravy paired with tempered cooling cucumber curd.",
                    "https://images.unsplash.com/photo-1601050690597-df0568f70950",
                    4.2, "10 mins", "180 kcal", true, restaurant.getName(), true, 140, true
            ));
        } else if (nameLower.contains("pizza") || cuisineLower.contains("pizza") || nameLower.contains("domino") || nameLower.contains("ovenstory")) {
            // Authentic Swiggy Pizza Dishes
            recommendedList.add(new FoodItem(
                    "PIZ-01",
                    "Farmhouse Supreme Pizza (Medium)",
                    "Pizzas",
                    299.00,
                    "Herbed tomato base topped with grilled mushrooms, crunchy green bell peppers, diced tomato and mozzarella.",
                    "https://images.unsplash.com/photo-1513104890138-7c749659a591",
                    4.5, "20 mins", "580 kcal", true, restaurant.getName(), true, 410, true
            ));
            recommendedList.add(new FoodItem(
                    "PIZ-02",
                    "Classic Margherita Cheese Burst",
                    "Pizzas",
                    199.00,
                    "Golden crust loaded with 100% genuine stringy mozzarella cheese and aromatic Italian oregano basil.",
                    "https://images.unsplash.com/photo-1574071318508-1cdbab80d002",
                    4.6, "15 mins", "490 kcal", true, restaurant.getName(), true, 530, true
            ));
            recommendedList.add(new FoodItem(
                    "PIZ-03",
                    "Spicy Barbecue Chicken Delight",
                    "Pizzas",
                    349.00,
                    "Smoky barbecue glazed tender chicken chunks, golden sweet corn and molten cheddar cheese.",
                    "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38",
                    4.6, "25 mins", "640 kcal", true, restaurant.getName(), false, 280, true
            ));
            recommendedList.add(new FoodItem(
                    "PIZ-04",
                    "Stuffed Cheesy Garlic Breadsticks",
                    "Sides",
                    139.00,
                    "Freshly oven-baked fluffy bread stuffed with jalapeño, sweet corn and gooey mozzarella.",
                    "https://images.unsplash.com/photo-1573080496219-bb080dd4f877",
                    4.7, "15 mins", "310 kcal", true, restaurant.getName(), true, 340, true
            ));
            recommendedList.add(new FoodItem(
                    "PIZ-05",
                    "Warm Molten Choco Lava Cake",
                    "Desserts",
                    109.00,
                    "Decadent warm chocolate cake with a delicious gooey molten chocolate center.",
                    "https://images.unsplash.com/photo-1606313564200-e75d5e30476c",
                    4.8, "10 mins", "340 kcal", true, restaurant.getName(), true, 620, true
            ));
        } else if (nameLower.contains("cake") || nameLower.contains("pastry") || nameLower.contains("bakingo") || nameLower.contains("cakeman") || nameLower.contains("sweet truth")) {
            // Authentic Swiggy Pastry & Cake Dishes
            recommendedList.add(new FoodItem(
                    "CAK-01",
                    "Belgian Dark Chocolate Truffle Pastry",
                    "Pastry",
                    119.00,
                    "Rich Dutch chocolate sponge filled with silky dark chocolate ganache and chocolate curls.",
                    "https://images.unsplash.com/photo-1578985545062-69928b1d9587",
                    4.7, "15 mins", "380 kcal", true, restaurant.getName(), true, 310, true
            ));
            recommendedList.add(new FoodItem(
                    "CAK-02",
                    "Red Velvet Cream Cheese Pastry",
                    "Pastry",
                    139.00,
                    "Classic crimson red velvet layers with tangy and sweet Philadelphia whipped cream cheese.",
                    "https://images.unsplash.com/photo-1588195538326-c5b1e9f80a1b",
                    4.6, "15 mins", "340 kcal", true, restaurant.getName(), true, 240, true
            ));
            recommendedList.add(new FoodItem(
                    "CAK-03",
                    "Blueberry Glaze Cheesecake Slice",
                    "Cheesecake",
                    169.00,
                    "Baked New York style cream cheesecake topped with luscious Canadian wild blueberry compote.",
                    "https://images.unsplash.com/photo-1551024709-8f23befc6f87",
                    4.8, "15 mins", "420 kcal", true, restaurant.getName(), true, 195, true
            ));
            recommendedList.add(new FoodItem(
                    "CAK-04",
                    "Warm Nutella Walnut Fudge Brownie",
                    "Brownies",
                    99.00,
                    "Dense, ultra-fudgy Belgian chocolate brownie studded with roasted walnuts and Nutella drizzle.",
                    "https://images.unsplash.com/photo-1606313564200-e75d5e30476c",
                    4.9, "10 mins", "290 kcal", true, restaurant.getName(), true, 480, true
            ));
        } else if (nameLower.contains("north") || cuisineLower.contains("north") || nameLower.contains("thali") || nameLower.contains("aashirvaad") || nameLower.contains("rollsking")) {
            // Authentic North Indian Dishes
            recommendedList.add(new FoodItem(
                    "NI-01",
                    "Paneer Butter Masala (Full)",
                    "Main Course",
                    269.00,
                    "Farm-fresh malai paneer cooked in a rich, buttery tomato cashew gravy with kasuri methi.",
                    "https://images.unsplash.com/photo-1589302168068-964664d93dc0",
                    4.6, "20 mins", "510 kcal", true, restaurant.getName(), true, 380, true
            ));
            recommendedList.add(new FoodItem(
                    "NI-02",
                    "Dal Makhani (Slow Cooked 12 Hrs)",
                    "Main Course",
                    219.00,
                    "Whole black lentils slow-cooked overnight with white butter and fresh cream on charcoal embers.",
                    "https://images.unsplash.com/photo-1589302168068-964664d93dc0",
                    4.7, "20 mins", "440 kcal", true, restaurant.getName(), true, 420, true
            ));
            recommendedList.add(new FoodItem(
                    "NI-03",
                    "Butter Garlic Naan (2 Pcs)",
                    "Breads",
                    89.00,
                    "Tandoor-charred refined wheat bread brushed with golden dairy butter and roasted garlic flakes.",
                    "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46",
                    4.6, "10 mins", "260 kcal", true, restaurant.getName(), true, 510, true
            ));
            recommendedList.add(new FoodItem(
                    "NI-04",
                    "Boneless Murgh Makhani (Butter Chicken)",
                    "Main Course",
                    329.00,
                    "Smoky shredded chicken tikka chunks simmered in mild velvety tomato-butter gravy.",
                    "https://images.unsplash.com/photo-1606491956689-2ea866880c84",
                    4.7, "25 mins", "580 kcal", true, restaurant.getName(), false, 490, true
            ));
        } else if (restaurant.getName().toLowerCase().contains("wendy")) {
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
