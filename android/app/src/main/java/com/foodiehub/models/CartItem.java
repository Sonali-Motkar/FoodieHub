package com.foodiehub.models;

import java.io.Serializable;

public class CartItem implements Serializable {

    private int cartId;
    private String foodId;
    private String foodName;
    private double unitPrice;
    private int quantity;
    private String imageUrl;

    public CartItem() {}

    public CartItem(int cartId, String foodId, String foodName, double unitPrice, int quantity, String imageUrl) {
        this.cartId = cartId;
        this.foodId = foodId;
        this.foodName = foodName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public CartItem(String foodId, String foodName, double unitPrice, int quantity, String imageUrl) {
        this(0, foodId, foodName, unitPrice, quantity, imageUrl);
    }

    public int getCartId() { return cartId; }
    public String getFoodId() { return foodId; }
    public String getFoodName() { return foodName; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    public void setCartId(int cartId) { this.cartId = cartId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
