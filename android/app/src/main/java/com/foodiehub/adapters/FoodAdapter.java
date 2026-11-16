package com.foodiehub.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.foodiehub.R;
import com.foodiehub.models.FoodItem;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    public interface OnFoodClickListener {
        void onFoodClick(FoodItem food);
        void onAddToCartClick(FoodItem food);
    }

    private Context context;
    private List<FoodItem> foodList;
    private OnFoodClickListener listener;

    public FoodAdapter(Context context, List<FoodItem> foodList, OnFoodClickListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.listener = listener;
    }

    public void updateList(List<FoodItem> newList) {
        this.foodList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_card, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = foodList.get(position);

        holder.txtName.setText(item.getName());
        holder.txtCategory.setText(item.getCategory());
        holder.txtPrice.setText(String.format("₹ %.2f", item.getPrice()));
        holder.txtRating.setText(String.format("%.1f ★", item.getRating()));
        holder.txtPrepTime.setText(item.getPrepTime());

        // Load image with Glide fallback
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.bg_rounded_card)
                .error(R.drawable.bg_rounded_card)
                .into(holder.imgFood);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onFoodClick(item);
        });

        holder.btnAddToCart.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCartClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return foodList != null ? foodList.size() : 0;
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView txtName, txtCategory, txtPrice, txtRating, txtPrepTime;
        Button btnAddToCart;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFoodItem);
            txtName = itemView.findViewById(R.id.txtFoodName);
            txtCategory = itemView.findViewById(R.id.txtFoodCategory);
            txtPrice = itemView.findViewById(R.id.txtFoodPrice);
            txtRating = itemView.findViewById(R.id.txtFoodRating);
            txtPrepTime = itemView.findViewById(R.id.txtFoodPrepTime);
            btnAddToCart = itemView.findViewById(R.id.btnQuickAddToCart);
        }
    }
}
