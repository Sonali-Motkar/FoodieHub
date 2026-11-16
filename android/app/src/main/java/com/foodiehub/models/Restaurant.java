package com.foodiehub.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Restaurant implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("rating")
    private double rating;

    @SerializedName("deliveryTime")
    private String deliveryTime;

    @SerializedName("cuisineTags")
    private String cuisineTags;

    @SerializedName("area")
    private String area;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("hasOneFreeDelivery")
    private boolean hasOneFreeDelivery;

    @SerializedName("offerText")
    private String offerText;

    public Restaurant() {}

    public Restaurant(String id, String name, double rating, String deliveryTime, String cuisineTags, String area, String imageUrl, boolean hasOneFreeDelivery, String offerText) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.deliveryTime = deliveryTime;
        this.cuisineTags = cuisineTags;
        this.area = area;
        this.imageUrl = imageUrl;
        this.hasOneFreeDelivery = hasOneFreeDelivery;
        this.offerText = offerText;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getRating() { return rating; }
    public String getDeliveryTime() { return deliveryTime; }
    public String getCuisineTags() { return cuisineTags; }
    public String getArea() { return area; }
    public String getImageUrl() { return imageUrl; }
    public boolean isHasOneFreeDelivery() { return hasOneFreeDelivery; }
    public String getOfferText() { return offerText; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setRating(double rating) { this.rating = rating; }
    public void setDeliveryTime(String deliveryTime) { this.deliveryTime = deliveryTime; }
    public void setCuisineTags(String cuisineTags) { this.cuisineTags = cuisineTags; }
    public void setArea(String area) { this.area = area; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setHasOneFreeDelivery(boolean hasOneFreeDelivery) { this.hasOneFreeDelivery = hasOneFreeDelivery; }
    public void setOfferText(String offerText) { this.offerText = offerText; }
}
