package com.foodiehub.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodiehub.R;
import com.foodiehub.activities.OrderTrackingActivity;
import com.foodiehub.models.Order;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
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

        holder.txtOrderId.setText(order.getOrderId());
        holder.txtOrderDate.setText(order.getCreatedAt() != null ? order.getCreatedAt().substring(0, 10) : "Recent");
        holder.txtTotalAmount.setText(String.format("₹ %.2f", order.getTotalAmount()));
        holder.txtAddress.setText(order.getDeliveryAddress());

        String status = order.getOrderStatus();
        holder.txtStatus.setText(status);

        // Dynamic status badge coloring
        if ("Delivered".equalsIgnoreCase(status)) {
            holder.txtStatus.setTextColor(Color.parseColor("#28A745"));
        } else if ("Preparing".equalsIgnoreCase(status) || "Out for Delivery".equalsIgnoreCase(status)) {
            holder.txtStatus.setTextColor(Color.parseColor("#FF9800"));
        } else if ("Cancelled".equalsIgnoreCase(status)) {
            holder.txtStatus.setTextColor(Color.parseColor("#DC3545"));
        } else {
            holder.txtStatus.setTextColor(Color.parseColor("#007BFF"));
        }

        // Summary string of items
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Order.OrderItem item : order.getItems()) {
                sb.append(item.getQuantity()).append("x ").append(item.getFoodName()).append(", ");
            }
            String itemsSummary = sb.toString();
            if (itemsSummary.endsWith(", ")) {
                itemsSummary = itemsSummary.substring(0, itemsSummary.length() - 2);
            }
            holder.txtItemsSummary.setText(itemsSummary);
        } else {
            holder.txtItemsSummary.setText("Custom order items");
        }

        holder.btnTrackOrder.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderTrackingActivity.class);
            intent.putExtra("ORDER_ID", order.getOrderId());
            intent.putExtra("DELIVERY_ADDRESS", order.getDeliveryAddress());
            intent.putExtra("ORDER_TOTAL", order.getTotalAmount());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtOrderDate, txtStatus, txtItemsSummary, txtTotalAmount, txtAddress;
        Button btnTrackOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtHistoryOrderId);
            txtOrderDate = itemView.findViewById(R.id.txtHistoryOrderDate);
            txtStatus = itemView.findViewById(R.id.txtHistoryOrderStatus);
            txtItemsSummary = itemView.findViewById(R.id.txtHistoryItemsSummary);
            txtTotalAmount = itemView.findViewById(R.id.txtHistoryTotal);
            txtAddress = itemView.findViewById(R.id.txtHistoryAddress);
            btnTrackOrder = itemView.findViewById(R.id.btnTrackOrder);
        }
    }
}
