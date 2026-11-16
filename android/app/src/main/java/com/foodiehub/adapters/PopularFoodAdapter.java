package com.foodiehub.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.foodiehub.R;
import com.foodiehub.models.FoodItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PopularFoodAdapter extends RecyclerView.Adapter<PopularFoodAdapter.ViewHolder> {

    public interface OnPopularItemListener {
        void onItemClick(FoodItem item);
        void onAddToCart(FoodItem item);
    }

    private Context context;
    private List<FoodItem> items;
    private OnPopularItemListener listener;
    private Set<String> favoriteIds = new HashSet<>();

    public PopularFoodAdapter(Context context, List<FoodItem> items, OnPopularItemListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    public void updateList(List<FoodItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_popular_glass, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = items.get(position);

        holder.txtName.setText(item.getName());
        holder.txtPrice.setText(String.format("₹ %.0f", item.getPrice()));

        // Tag: Alternate between "Spicy", "Popular", "Hot"
        if (position % 2 == 0) {
            holder.txtTag.setText("Spicy");
            holder.txtTag.setVisibility(View.VISIBLE);
        } else {
            holder.txtTag.setText("Hot 🔥");
            holder.txtTag.setVisibility(View.VISIBLE);
        }

        Glide.with(context)
                .load(item.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(24)))
                .placeholder(R.drawable.bg_glass_card)
                .error(R.drawable.bg_glass_card)
                .into(holder.imgFood);

        boolean isFav = favoriteIds.contains(item.getId());
        holder.btnFav.setImageResource(isFav ? R.drawable.ic_glass_heart_fill : R.drawable.ic_glass_heart_outline);

        holder.btnFav.setOnClickListener(v -> {
            boolean favNow = !favoriteIds.contains(item.getId());
            if (favNow) favoriteIds.add(item.getId());
            else favoriteIds.remove(item.getId());
            holder.btnFav.setImageResource(favNow ? R.drawable.ic_glass_heart_fill : R.drawable.ic_glass_heart_outline);

            // Bounce micro-interaction
            ObjectAnimator sx = ObjectAnimator.ofFloat(holder.btnFav, "scaleX", 0.7f, 1.3f, 1.0f);
            ObjectAnimator sy = ObjectAnimator.ofFloat(holder.btnFav, "scaleY", 0.7f, 1.3f, 1.0f);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(sx, sy);
            set.setDuration(300);
            set.setInterpolator(new OvershootInterpolator());
            set.start();
        });

        holder.btnAdd.setOnClickListener(v -> {
            // Pulse micro-interaction
            ObjectAnimator sx = ObjectAnimator.ofFloat(holder.btnAdd, "scaleX", 0.8f, 1.2f, 1.0f);
            ObjectAnimator sy = ObjectAnimator.ofFloat(holder.btnAdd, "scaleY", 0.8f, 1.2f, 1.0f);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(sx, sy);
            set.setDuration(250);
            set.start();

            if (listener != null) listener.onAddToCart(item);
        });

        holder.cardRoot.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardRoot;
        ImageView imgFood, btnFav;
        TextView txtTag, txtName, txtPrice;
        FrameLayout btnAdd;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.cardPopularRoot);
            imgFood = itemView.findViewById(R.id.imgPopularFood);
            btnFav = itemView.findViewById(R.id.btnPopularFav);
            txtTag = itemView.findViewById(R.id.txtPopularTag);
            txtName = itemView.findViewById(R.id.txtPopularName);
            txtPrice = itemView.findViewById(R.id.txtPopularPrice);
            btnAdd = itemView.findViewById(R.id.btnPopularAdd);
        }
    }
}
