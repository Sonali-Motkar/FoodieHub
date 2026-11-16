package com.foodiehub.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.foodiehub.R;
import com.foodiehub.models.Restaurant;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant);
    }

    private Context context;
    private List<Restaurant> restaurantList;
    private OnRestaurantClickListener listener;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList, OnRestaurantClickListener listener) {
        this.context = context;
        this.restaurantList = restaurantList;
        this.listener = listener;
    }

    public void updateList(List<Restaurant> newList) {
        this.restaurantList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant_card, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant item = restaurantList.get(position);

        holder.txtName.setText(item.getName());
        holder.txtRating.setText(String.format("%.1f", item.getRating()));
        holder.txtDeliveryTime.setText(item.getDeliveryTime());
        holder.txtCuisines.setText(item.getCuisineTags());
        holder.txtArea.setText(item.getArea());

        if (item.getOfferText() != null && !item.getOfferText().isEmpty()) {
            holder.txtOffer.setText(item.getOfferText());
            holder.txtOffer.setVisibility(View.VISIBLE);
        } else {
            holder.txtOffer.setVisibility(View.GONE);
        }

        holder.layoutOneBadge.setVisibility(item.isHasOneFreeDelivery() ? View.VISIBLE : View.GONE);

        Glide.with(context)
                .load(item.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(16)))
                .placeholder(R.drawable.bg_glass_card)
                .error(R.drawable.bg_glass_card)
                .into(holder.imgRestaurant);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRestaurantClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList != null ? restaurantList.size() : 0;
    }

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRestaurant;
        TextView txtName, txtRating, txtDeliveryTime, txtCuisines, txtArea, txtOffer;
        LinearLayout layoutOneBadge;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
            txtName = itemView.findViewById(R.id.txtRestaurantName);
            txtRating = itemView.findViewById(R.id.txtRestaurantRating);
            txtDeliveryTime = itemView.findViewById(R.id.txtRestaurantDeliveryTime);
            txtCuisines = itemView.findViewById(R.id.txtRestaurantCuisines);
            txtArea = itemView.findViewById(R.id.txtRestaurantArea);
            txtOffer = itemView.findViewById(R.id.txtOfferOverlay);
            layoutOneBadge = itemView.findViewById(R.id.layoutOneBadge);
        }
    }
}
