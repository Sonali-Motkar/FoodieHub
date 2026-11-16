package com.foodiehub.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.foodiehub.R;
import com.foodiehub.models.CartItem;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface CartItemListener {
        void onQuantityChanged(CartItem item, int newQty);
        void onItemRemoved(CartItem item);
    }

    private Context context;
    private List<CartItem> cartItems;
    private CartItemListener listener;

    public CartAdapter(Context context, List<CartItem> cartItems, CartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    public void updateItems(List<CartItem> newItems) {
        this.cartItems = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_card, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.txtName.setText(item.getFoodName());
        holder.txtUnitPrice.setText(String.format("₹ %.2f", item.getUnitPrice()));
        holder.txtQuantity.setText(String.valueOf(item.getQuantity()));
        holder.txtSubtotal.setText(String.format("₹ %.2f", item.getSubtotal()));

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.bg_rounded_card)
                .error(R.drawable.bg_rounded_card)
                .into(holder.imgItem);

        holder.btnIncrement.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityChanged(item, item.getQuantity() + 1);
            }
        });

        holder.btnDecrement.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityChanged(item, item.getQuantity() - 1);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemRemoved(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView txtName, txtUnitPrice, txtQuantity, txtSubtotal;
        ImageButton btnDecrement, btnIncrement, btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgCartThumb);
            txtName = itemView.findViewById(R.id.txtCartItemName);
            txtUnitPrice = itemView.findViewById(R.id.txtCartItemPrice);
            txtQuantity = itemView.findViewById(R.id.txtCartItemQty);
            txtSubtotal = itemView.findViewById(R.id.txtCartItemSubtotal);
            btnDecrement = itemView.findViewById(R.id.btnQtyDecrement);
            btnIncrement = itemView.findViewById(R.id.btnQtyIncrement);
            btnDelete = itemView.findViewById(R.id.btnCartItemDelete);
        }
    }
}
