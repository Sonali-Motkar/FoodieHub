package com.foodiehub.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.foodiehub.R;
import com.foodiehub.activities.CartActivity;
import com.foodiehub.activities.OrderTrackingActivity;
import com.foodiehub.database.DatabaseHelper;
import com.foodiehub.models.Order;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders;
    private DatabaseHelper dbHelper;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
        this.dbHelper = new DatabaseHelper(context);
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        // Date & Total
        holder.txtOrderDate.setText("Ordered: " + (order.getCreatedAt() != null ? order.getCreatedAt() : "Nov 15, 11:26 PM"));
        holder.txtTotalAmount.setText(String.format("Bill Total: ₹ %.0f", order.getTotalAmount()));

        // Status badge
        String status = order.getOrderStatus() != null ? order.getOrderStatus() : "Delivered";
        holder.txtStatus.setText(status);

        // Restaurant metadata & items
        if (position % 2 == 0) {
            holder.txtRestaurantName.setText("KFC");
            holder.txtRestaurantLocation.setText("Puna wale");
            holder.imgRestaurantLogo.setImageResource(R.drawable.ic_category_chicken);
        } else {
            holder.txtRestaurantName.setText("Sairat Biryani");
            holder.txtRestaurantLocation.setText("Ravet");
            holder.imgRestaurantLogo.setImageResource(R.drawable.ic_category_burger);
        }

        // Summary string of items
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            Order.OrderItem first = order.getItems().get(0);
            holder.txtItemQtyBadge.setText(first.getQuantity() + " X");
            holder.txtItemsSummary.setText(first.getFoodName());
        } else {
            holder.txtItemQtyBadge.setText("1 X");
            holder.txtItemsSummary.setText("Chicken Krisper With Reg Fries");
        }

        // Star rating micro-interactions
        holder.txtStarsFood.setOnClickListener(v -> {
            holder.txtStarsFood.setText("★★★★★");
            holder.txtStarsFood.setTextColor(Color.parseColor("#FFA000"));
            bounceView(holder.txtStarsFood);
            Toast.makeText(context, "Rated 5 ★ for Food Quality!", Toast.LENGTH_SHORT).show();
        });

        holder.txtStarsDelivery.setOnClickListener(v -> {
            holder.txtStarsDelivery.setText("★★★★★");
            holder.txtStarsDelivery.setTextColor(Color.parseColor("#FFA000"));
            bounceView(holder.txtStarsDelivery);
            Toast.makeText(context, "Rated 5 ★ for Delivery Partner!", Toast.LENGTH_SHORT).show();
        });

        // REORDER action: adds item back to cart and goes to CartActivity
        holder.btnReorder.setOnClickListener(v -> {
            bounceView(holder.btnReorder);
            try {
                if (order.getItems() != null && !order.getItems().isEmpty()) {
                    for (Order.OrderItem oi : order.getItems()) {
                        dbHelper.addToCart(
                                oi.getFoodId() != null ? oi.getFoodId() : "FOD-REORDER",
                                oi.getFoodName(),
                                oi.getPrice() > 0 ? oi.getPrice() : 189.0,
                                oi.getQuantity() > 0 ? oi.getQuantity() : 1,
                                ""
                        );
                    }
                } else {
                    dbHelper.addToCart("FOD-REORDER", "Chicken Krisper With Reg Fries", 273.0, 1, "");
                }
                Toast.makeText(context, "Items added to cart! Proceeding to checkout...", Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, CartActivity.class));
            } catch (Exception e) {
                context.startActivity(new Intent(context, CartActivity.class));
            }
        });

        // Live Track action: opens OrderTrackingActivity with live radar and scooter map
        holder.btnTrackLiveDelivery.setOnClickListener(v -> {
            bounceView(holder.btnTrackLiveDelivery);
            Intent intent = new Intent(context, OrderTrackingActivity.class);
            intent.putExtra("ORDER_ID", order.getOrderId() != null ? order.getOrderId() : "ORD-7294");
            intent.putExtra("DELIVERY_ADDRESS", order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "Flat 402, Green Palms, Kothrud, Pune");
            intent.putExtra("ORDER_TOTAL", order.getTotalAmount() > 0 ? order.getTotalAmount() : 273.0);
            context.startActivity(intent);
        });
    }

    private void bounceView(View v) {
        ObjectAnimator sx = ObjectAnimator.ofFloat(v, "scaleX", 0.9f, 1.1f, 1.0f);
        ObjectAnimator sy = ObjectAnimator.ofFloat(v, "scaleY", 0.9f, 1.1f, 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(sx, sy);
        set.setDuration(240);
        set.setInterpolator(new OvershootInterpolator());
        set.start();
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRestaurantLogo;
        TextView txtRestaurantName, txtRestaurantLocation, txtStatus;
        TextView txtItemQtyBadge, txtItemsSummary;
        TextView txtStarsFood, txtStarsDelivery;
        Button btnReorder, btnTrackLiveDelivery;
        TextView txtOrderDate, txtTotalAmount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRestaurantLogo = itemView.findViewById(R.id.imgRestaurantLogo);
            txtRestaurantName = itemView.findViewById(R.id.txtRestaurantName);
            txtRestaurantLocation = itemView.findViewById(R.id.txtRestaurantLocation);
            txtStatus = itemView.findViewById(R.id.txtHistoryOrderStatus);
            txtItemQtyBadge = itemView.findViewById(R.id.txtItemQtyBadge);
            txtItemsSummary = itemView.findViewById(R.id.txtHistoryItemsSummary);
            txtStarsFood = itemView.findViewById(R.id.txtStarsFood);
            txtStarsDelivery = itemView.findViewById(R.id.txtStarsDelivery);
            btnReorder = itemView.findViewById(R.id.btnReorder);
            btnTrackLiveDelivery = itemView.findViewById(R.id.btnTrackLiveDelivery);
            txtOrderDate = itemView.findViewById(R.id.txtHistoryOrderDate);
            txtTotalAmount = itemView.findViewById(R.id.txtHistoryTotal);
        }
    }
}
