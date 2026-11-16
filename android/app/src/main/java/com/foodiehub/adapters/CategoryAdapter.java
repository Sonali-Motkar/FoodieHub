package com.foodiehub.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_chip, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.txtCategoryName.setText(category.getName());

        boolean isSelected = (position == selectedPosition);
        holder.txtCategoryName.setBackgroundResource(isSelected ? R.drawable.bg_primary_button : R.drawable.bg_rounded_card);
        holder.txtCategoryName.setTextColor(isSelected ? Color.WHITE : Color.parseColor("#495057"));

        holder.itemView.setOnClickListener(v -> {
            int prevPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prevPos);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onCategorySelected(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategoryName = itemView.findViewById(R.id.txtChipCategory);
        }
    }
}
