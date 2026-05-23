package com.example.myorders.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myorders.R;
import com.example.myorders.models.Order;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void updateData(List<Order> newOrderList) {
        this.orderList = newOrderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvTitle.setText(order.getVehicleType());
        holder.tvSubtitle.setText(order.getDateTime() + "  |  Order ID: " + order.getOrderId());
        holder.tvPrice.setText("₹ " + order.getPrice());
        holder.tvPickup.setText(order.getPickupAddress());
        holder.tvDropoff.setText(order.getDropoffAddress());

        // Status styling
        String status = order.getStatus();
        holder.tvStatus.setText(status.toUpperCase());
        if (status.equalsIgnoreCase("cancelled")) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.cancelled_text));
        } else if (status.equalsIgnoreCase("completed")) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tab_selected); // yellow background as general capsule
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            // Booked Again
            holder.tvStatus.setBackgroundResource(R.drawable.bg_info_banner); // light purple
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.banner_text));
        }

        // Action click effects
        holder.btnInvoice.setOnClickListener(v -> 
            Toast.makeText(context, "Downloading invoice for " + order.getOrderId(), Toast.LENGTH_SHORT).show()
        );

        holder.btnBookAgain.setOnClickListener(v -> 
            Toast.makeText(context, "Booking trip again: " + order.getVehicleType(), Toast.LENGTH_SHORT).show()
        );

        holder.btnMenu.setOnClickListener(v -> 
            Toast.makeText(context, "More actions for " + order.getOrderId(), Toast.LENGTH_SHORT).show()
        );
        
        holder.itemView.setOnClickListener(v ->
            Toast.makeText(context, "Selected Order ID: " + order.getOrderId(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle, tvPrice, tvPickup, tvDropoff, tvStatus;
        View btnInvoice, btnBookAgain;
        ImageView btnMenu;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_order_title);
            tvSubtitle = itemView.findViewById(R.id.tv_order_subtitle);
            tvPrice = itemView.findViewById(R.id.tv_order_price);
            tvPickup = itemView.findViewById(R.id.tv_pickup_address);
            tvDropoff = itemView.findViewById(R.id.tv_dropoff_address);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnInvoice = itemView.findViewById(R.id.btn_invoice);
            btnBookAgain = itemView.findViewById(R.id.btn_book_again);
            btnMenu = itemView.findViewById(R.id.btn_menu);
        }
    }
}
