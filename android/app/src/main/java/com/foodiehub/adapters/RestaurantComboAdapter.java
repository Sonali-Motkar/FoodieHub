package com.foodiehub.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.foodiehub.R;
import com.foodiehub.models.FoodItem;

import java.util.List;

public class RestaurantComboAdapter extends RecyclerView.Adapter<RestaurantComboAdapter.ComboViewHolder> {

    public interface OnComboAddListener {
        void onComboAdd(FoodItem combo);
    }

    private Context context;
    private List<FoodItem> comboList;
    private OnComboAddListener listener;

    public RestaurantComboAdapter(Context context, List<FoodItem> comboList, OnComboAddListener listener) {
        this.context = context;
        this.comboList = comboList;
        this.listener = listener;
    }

    public void updateList(List<FoodItem> newList) {
        this.comboList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant_combo, parent, false);
        return new ComboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComboViewHolder holder, int position) {
        FoodItem combo = comboList.get(position);

        holder.txtTitle.setText(combo.getName());
        holder.txtPrice.setText(String.format("₹%.0f", combo.getPrice()));

        if (combo.isVeg()) {
            holder.imgVegNonVeg.setImageResource(R.drawable.ic_veg);
        } else {
            holder.imgVegNonVeg.setImageResource(R.drawable.ic_non_veg);
        }

        Glide.with(context)
                .load(combo.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                .placeholder(R.drawable.bg_glass_card)
                .error(R.drawable.bg_glass_card)
                .into(holder.imgCombo);

        holder.btnAdd.setOnClickListener(v -> {
            ObjectAnimator sx = ObjectAnimator.ofFloat(holder.btnAdd, "scaleX", 0.85f, 1.15f, 1.0f);
            ObjectAnimator sy = ObjectAnimator.ofFloat(holder.btnAdd, "scaleY", 0.85f, 1.15f, 1.0f);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(sx, sy);
            set.setDuration(220);
            set.setInterpolator(new OvershootInterpolator());
            set.start();

            if (listener != null) {
                listener.onComboAdd(combo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comboList != null ? comboList.size() : 0;
    }

    static class ComboViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCombo, imgVegNonVeg;
        TextView txtTitle, txtPrice, btnAdd;

        public ComboViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCombo = itemView.findViewById(R.id.imgCombo);
            imgVegNonVeg = itemView.findViewById(R.id.imgComboVegNonVeg);
            txtTitle = itemView.findViewById(R.id.txtComboTitle);
            txtPrice = itemView.findViewById(R.id.txtComboPrice);
            btnAdd = itemView.findViewById(R.id.btnComboAdd);
        }
    }
}
