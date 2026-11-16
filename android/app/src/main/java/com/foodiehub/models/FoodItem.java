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
}
