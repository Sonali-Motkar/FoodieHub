package com.foodiehub.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {

    @SerializedName("orderId")
    private String orderId;

    @SerializedName("userId")
    private String userId;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("items")
    private List<OrderItem> items;

    @SerializedName("subtotal")
    private double subtotal;

    @SerializedName("taxAmount")
    private double taxAmount;

    @SerializedName("deliveryFee")
    private double deliveryFee;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("deliveryAddress")
    private String deliveryAddress;

    @SerializedName("orderStatus")
    private String orderStatus;

    @SerializedName("createdAt")
    private String createdAt;

    public static class OrderItem implements Serializable {
        @SerializedName("foodId")
        private String foodId;

        @SerializedName("foodName")
        private String foodName;

        @SerializedName("quantity")
        private int quantity;

        @SerializedName("priceAtPurchase")
        private double priceAtPurchase;

        @SerializedName("subtotal")
        private double subtotal;

        public String getFoodId() { return foodId; }
        public String getFoodName() { return foodName; }
        public int getQuantity() { return quantity; }
        public double getPriceAtPurchase() { return priceAtPurchase; }
        public double getPrice() { return priceAtPurchase; }
        public double getSubtotal() { return subtotal; }
    }

    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public String getCustomerName() { return customerName; }
    public List<OrderItem> getItems() { return items; }
    public double getSubtotal() { return subtotal; }
    public double getTaxAmount() { return taxAmount; }
    public double getDeliveryFee() { return deliveryFee; }
    public double getTotalAmount() { return totalAmount; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getOrderStatus() { return orderStatus; }
    public String getCreatedAt() { return createdAt; }

    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
}
