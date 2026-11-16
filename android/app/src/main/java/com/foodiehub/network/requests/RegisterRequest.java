package com.foodiehub.network.requests;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("phone")
    private String phone;

    @SerializedName("deliveryAddress")
    private String deliveryAddress;

    public RegisterRequest(String name, String email, String password, String phone, String deliveryAddress) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.deliveryAddress = deliveryAddress;
    }
}
