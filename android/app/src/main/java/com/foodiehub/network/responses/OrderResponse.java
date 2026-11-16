package com.foodiehub.network.responses;

import com.foodiehub.models.Order;
import com.google.gson.annotations.SerializedName;

public class OrderResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("orderId")
    private String orderId;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("orderStatus")
    private String orderStatus;

    @SerializedName("order")
    private Order order;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getOrderId() { return orderId; }
    public double getTotalAmount() { return totalAmount; }
    public String getOrderStatus() { return orderStatus; }
    public Order getOrder() { return order; }
}
