package com.foodiehub.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.foodiehub.R;
import com.foodiehub.models.FoodItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantDishAdapter extends RecyclerView.Adapter<RestaurantDishAdapter.DishViewHolder> {

    public interface OnDishActionListener {
        void onAddToCart(FoodItem item);
        void onQuantityChanged(FoodItem item, int newQty);
        void onDishClick(FoodItem item);
    }

    private Context context;
    private List<FoodItem> dishList;
    private OnDishActionListener listener;
    private Map<String, Integer> itemQuantities = new HashMap<>();

    public RestaurantDishAdapter(Context context, List<FoodItem> dishList, OnDishActionListener listener) {
        this.context = context;
        this.dishList = dishList;
        this.listener = listener;
    }

    public void updateList(List<FoodItem> newList) {
        this.dishList = newList;
        notifyDataSetChanged();
    }

    public void setItemQuantity(String itemId, int qty) {
        if (qty > 0) {
            itemQuantities.put(itemId, qty);
        } else {
            itemQuantities.remove(itemId);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant_dish, parent, false);
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        FoodItem item = dishList.get(position);

        holder.txtDishName.setText(item.getName());
        holder.txtDishPrice.setText(String.format("₹%.0f", item.getPrice()));
        holder.txtDishStarRating.setText(String.format("★ %.1f", item.getRating()));
        holder.txtDishRatingCount.setText("(" + (item.getRatingCount() > 0 ? item.getRatingCount() : 180) + ")");
        holder.txtDishDescription.setText(item.getDescription());

        // Veg / Non-Veg Indicator
        if (item.isVeg()) {
            holder.imgVegNonVeg.setImageResource(R.drawable.ic_veg);
        } else {
            holder.imgVegNonVeg.setImageResource(R.drawable.ic_non_veg);
        }

        // Load image with rounded corners
        Glide.with(context)
                .load(item.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                .placeholder(R.drawable.bg_glass_card)
                .error(R.drawable.bg_glass_card)
                .into(holder.imgDish);

        // Quantity logic
        int currentQty = itemQuantities.containsKey(item.getId()) ? itemQuantities.get(item.getId()) : 0;
        if (currentQty > 0) {
            holder.btnDishAdd.setVisibility(View.GONE);
            holder.layoutDishCounter.setVisibility(View.VISIBLE);
            holder.txtDishQty.setText(String.valueOf(currentQty));
        } else {
            holder.btnDishAdd.setVisibility(View.VISIBLE);
            holder.layoutDishCounter.setVisibility(View.GONE);
        }

        // Add button tap
        holder.btnDishAdd.setOnClickListener(v -> {
            int newQty = 1;
            itemQuantities.put(item.getId(), newQty);
            holder.btnDishAdd.setVisibility(View.GONE);
            holder.layoutDishCounter.setVisibility(View.VISIBLE);
            holder.txtDishQty.setText("1");

            // Bounce micro-interaction
            bounceView(holder.cardAddAction);

            if (listener != null) {
                listener.onAddToCart(item);
                listener.onQuantityChanged(item, newQty);
            }
        });

        holder.btnDishPlus.setOnClickListener(v -> {
            int q = itemQuantities.containsKey(item.getId()) ? itemQuantities.get(item.getId()) : 0;
            q++;
            itemQuantities.put(item.getId(), q);
            holder.txtDishQty.setText(String.valueOf(q));
            bounceView(holder.cardAddAction);
            if (listener != null) {
                listener.onQuantityChanged(item, q);
            }
        });

        holder.btnDishMinus.setOnClickListener(v -> {
            int q = itemQuantities.containsKey(item.getId()) ? itemQuantities.get(item.getId()) : 1;
            q--;
            if (q <= 0) {
                itemQuantities.remove(item.getId());
                holder.btnDishAdd.setVisibility(View.VISIBLE);
                holder.layoutDishCounter.setVisibility(View.GONE);
            } else {
                itemQuantities.put(item.getId(), q);
                holder.txtDishQty.setText(String.valueOf(q));
            }
            bounceView(holder.cardAddAction);
            if (listener != null) {
                listener.onQuantityChanged(item, q);
            }
        });

        holder.txtMoreDetails.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDishClick(item);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDishClick(item);
            }
        });
    }

    private void bounceView(View v) {
        ObjectAnimator sx = ObjectAnimator.ofFloat(v, "scaleX", 0.9f, 1.08f, 1.0f);
        ObjectAnimator sy = ObjectAnimator.ofFloat(v, "scaleY", 0.9f, 1.08f, 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(sx, sy);
        set.setDuration(220);
        set.setInterpolator(new OvershootInterpolator());
        set.start();
    }

    @Override
    public int getItemCount() {
        return dishList != null ? dishList.size() : 0;
    }

    static class DishViewHolder extends RecyclerView.ViewHolder {
        ImageView imgVegNonVeg, imgDish;
        TextView txtDishName, txtDishPrice, txtDishStarRating, txtDishRatingCount, txtDishDescription, txtMoreDetails;
        CardView cardAddAction;
        TextView btnDishAdd, btnDishMinus, btnDishPlus, txtDishQty, txtCustomisable;
        LinearLayout layoutDishCounter;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVegNonVeg = itemView.findViewById(R.id.imgVegNonVeg);
            imgDish = itemView.findViewById(R.id.imgDish);
            txtDishName = itemView.findViewById(R.id.txtDishName);
            txtDishPrice = itemView.findViewById(R.id.txtDishPrice);
            txtDishStarRating = itemView.findViewById(R.id.txtDishStarRating);
            txtDishRatingCount = itemView.findViewById(R.id.txtDishRatingCount);
            txtDishDescription = itemView.findViewById(R.id.txtDishDescription);
            txtMoreDetails = itemView.findViewById(R.id.txtMoreDetails);
            cardAddAction = itemView.findViewById(R.id.cardAddAction);
            btnDishAdd = itemView.findViewById(R.id.btnDishAdd);
            btnDishMinus = itemView.findViewById(R.id.btnDishMinus);
            btnDishPlus = itemView.findViewById(R.id.btnDishPlus);
            txtDishQty = itemView.findViewById(R.id.txtDishQty);
            txtCustomisable = itemView.findViewById(R.id.txtCustomisable);
            layoutDishCounter = itemView.findViewById(R.id.layoutDishCounter);
        }
    }
}
