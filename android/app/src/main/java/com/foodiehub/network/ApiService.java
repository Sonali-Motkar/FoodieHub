package com.foodiehub.network;

import com.foodiehub.models.Category;
import com.foodiehub.models.FoodItem;
import com.foodiehub.models.Order;
import com.foodiehub.network.requests.LoginRequest;
import com.foodiehub.network.requests.OrderRequest;
import com.foodiehub.network.requests.RegisterRequest;
import com.foodiehub.network.responses.AuthResponse;
import com.foodiehub.network.responses.FoodResponse;
import com.foodiehub.network.responses.OrderResponse;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Auth
    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    // Food Catalog
    @GET("foods")
    Call<FoodResponse> getFoods(@Query("category") String category, @Query("search") String search);

    @GET("foods/categories")
    Call<Map<String, Object>> getCategories();

    @GET("foods/{id}")
    Call<Map<String, Object>> getFoodDetail(@Path("id") String foodId);

    // Orders
    @POST("orders")
    Call<OrderResponse> createOrder(@Body OrderRequest request);

    @GET("orders/user/{userId}")
    Call<Map<String, Object>> getUserOrders(@Path("userId") String userId);

    @GET("orders")
    Call<Map<String, Object>> getAllOrders();

    @PATCH("orders/{orderId}/status")
    Call<Map<String, Object>> updateOrderStatus(@Path("orderId") String orderId, @Body Map<String, String> statusBody);
}
