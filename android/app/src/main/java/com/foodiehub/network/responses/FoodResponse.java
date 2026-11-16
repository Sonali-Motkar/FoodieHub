package com.foodiehub.network.responses;

import com.foodiehub.models.FoodItem;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FoodResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("count")
    private int count;

    @SerializedName("data")
    private List<FoodItem> data;

    public boolean isSuccess() { return success; }
    public int getCount() { return count; }
    public List<FoodItem> getData() { return data; }
}
