package com.foodiehub.network.requests;

import com.foodiehub.models.CartItem;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderRequest {

    @SerializedName("userId")
    private String userId;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("items")
    private List<CartItem> items;

    @SerializedName("deliveryAddress")
    private String deliveryAddress;

    @SerializedName("paymentMode")
    private String paymentMode;

    @SerializedName("subtotal")
    private double subtotal;

    @SerializedName("taxAmount")
    private double taxAmount;

    @SerializedName("deliveryFee")
    private double deliveryFee;

    @SerializedName("totalAmount")
    private double totalAmount;

    public OrderRequest(String userId, String customerName, List<CartItem> items, String deliveryAddress, String paymentMode, double subtotal, double taxAmount, double deliveryFee, double totalAmount) {
        this.userId = userId;
        this.customerName = customerName;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.paymentMode = paymentMode;
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.deliveryFee = deliveryFee;
        this.totalAmount = totalAmount;
    }

    public OrderRequest(List<CartItem> items, String deliveryAddress, double subtotal, double taxAmount, double deliveryFee, double totalAmount) {
        this("USR-101", "Sonali Motkar", items, deliveryAddress, "Cash on Delivery", subtotal, taxAmount, deliveryFee, totalAmount);
    }
}
