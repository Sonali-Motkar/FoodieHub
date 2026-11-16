package com.foodiehub.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class FoodItem implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("category")
    private String category;

    @SerializedName("price")
    private double price;

    @SerializedName("description")
    private String description;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("rating")
    private double rating;

    @SerializedName("prepTime")
    private String prepTime;

    @SerializedName("calories")
    private String calories;

    @SerializedName("isAvailable")
    private boolean isAvailable;

    @SerializedName("restaurantName")
    private String restaurantName = "Wendy's Burgers";

    @SerializedName("isVeg")
    private boolean isVeg = true;

    @SerializedName("ratingCount")
    private int ratingCount = 120;

    @SerializedName("isCustomisable")
    private boolean isCustomisable = true;

    public FoodItem() {}

    public FoodItem(String id, String name, String category, double price, String description, String imageUrl, double rating, String prepTime, String calories, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.prepTime = prepTime;
        this.calories = calories;
        this.isAvailable = isAvailable;
        this.restaurantName = "Wendy's Burgers";
        this.isVeg = true;
        this.ratingCount = 120;
        this.isCustomisable = true;
    }

    public FoodItem(String id, String name, String category, double price, String description, String imageUrl, double rating, String prepTime, String calories, boolean isAvailable, String restaurantName, boolean isVeg, int ratingCount, boolean isCustomisable) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.prepTime = prepTime;
        this.calories = calories;
        this.isAvailable = isAvailable;
        this.restaurantName = restaurantName;
        this.isVeg = isVeg;
        this.ratingCount = ratingCount;
        this.isCustomisable = isCustomisable;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public double getRating() { return rating; }
    public String getPrepTime() { return prepTime; }
    public String getCalories() { return calories; }
    public boolean isAvailable() { return isAvailable; }
    public String getRestaurantName() { return restaurantName; }
    public boolean isVeg() { return isVeg; }
    public int getRatingCount() { return ratingCount; }
    public boolean isCustomisable() { return isCustomisable; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setRating(double rating) { this.rating = rating; }
    public void setPrepTime(String prepTime) { this.prepTime = prepTime; }
    public void setCalories(String calories) { this.calories = calories; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public void setVeg(boolean veg) { isVeg = veg; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
    public void setCustomisable(boolean customisable) { isCustomisable = customisable; }
}
