package com.foodiehub.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.foodiehub.R;
import com.foodiehub.models.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategorySelectedListener {
        void onCategorySelected(Category category);
    }

    private Context context;
    private List<Category> categoryList;
    private int selectedPosition = 0;
    private OnCategorySelectedListener listener;

    public CategoryAdapter(Context context, List<Category> categoryList, OnCategorySelectedListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_glass, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.txtCategoryName.setText(category.getName());

        int iconRes = getIconForCategory(category.getName());
        holder.imgIcon.setImageResource(iconRes);

        boolean isSelected = (position == selectedPosition);
        if (isSelected) {
            holder.layoutRoot.setBackgroundResource(R.drawable.bg_glass_card_selected);
            holder.txtCategoryName.setTextColor(Color.WHITE);
            holder.imgIcon.setColorFilter(Color.WHITE);
        } else {
            holder.layoutRoot.setBackgroundResource(R.drawable.bg_glass_card);
            holder.txtCategoryName.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            holder.imgIcon.setColorFilter(ContextCompat.getColor(context, R.color.text_secondary));
        }

        holder.itemView.setOnClickListener(v -> {
            int prevPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prevPos);
            notifyItemChanged(selectedPosition);

            // Bounce micro-interaction
            ObjectAnimator sx = ObjectAnimator.ofFloat(holder.layoutRoot, "scaleX", 0.9f, 1.08f, 1.0f);
            ObjectAnimator sy = ObjectAnimator.ofFloat(holder.layoutRoot, "scaleY", 0.9f, 1.08f, 1.0f);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(sx, sy);
            set.setDuration(260);
            set.setInterpolator(new OvershootInterpolator());
            set.start();

            if (listener != null) {
                listener.onCategorySelected(category);
            }
        });
    }

    private int getIconForCategory(String name) {
        if (name == null) return R.drawable.ic_category_burger;
        String lower = name.toLowerCase();
        if (lower.contains("burger")) return R.drawable.ic_category_burger;
        if (lower.contains("pizza")) return R.drawable.ic_category_pizza;
        if (lower.contains("chicken") || lower.contains("asian")) return R.drawable.ic_category_chicken;
        if (lower.contains("fries") || lower.contains("snack")) return R.drawable.ic_category_fries;
        if (lower.contains("drink") || lower.contains("beverage")) return R.drawable.ic_category_drink;
        return R.drawable.ic_category_burger;
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutRoot;
        ImageView imgIcon;
        TextView txtCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutRoot = itemView.findViewById(R.id.layoutCategoryRoot);
            imgIcon = itemView.findViewById(R.id.imgCategoryIcon);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryTitle);
        }
    }
}
