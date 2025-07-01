package com.example.espresio.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.espresio.R;
import com.example.espresio.SaleDetailActivity;
import com.example.espresio.models.Sale;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SalesReportAdapter extends RecyclerView.Adapter<SalesReportAdapter.ViewHolder> {
    private List<Sale> sales;
    private SimpleDateFormat dateFormat;
    private NumberFormat currencyFormat;
    private Context context;

    public SalesReportAdapter(List<Sale> sales, Context context) {
        this.sales = sales;
        this.context = context;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    }

    // Overloaded constructor for backward compatibility
    public SalesReportAdapter(List<Sale> sales) {
        this.sales = sales;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sales_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sale sale = sales.get(position);

        holder.tvSaleDate.setText(dateFormat.format(new Date(sale.getSaleDate())));
        holder.tvEmployee.setText("Employee: " + sale.getEmployeeName());
        holder.tvTotalAmount.setText(currencyFormat.format(sale.getTotalAmount()));

        // Handle items count safely
        int itemCount = (sale.getItems() != null) ? sale.getItems().size() : 0;
        holder.tvItems.setText("Items: " + itemCount);

        holder.tvStatus.setText("Status: " + capitalizeFirst(sale.getStatus()));

        // Set click listener to open detail activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SaleDetailActivity.class);
            intent.putExtra(SaleDetailActivity.EXTRA_SALE_ID, sale.getId());
            context.startActivity(intent);
        });

        // Add ripple effect or visual feedback
        holder.itemView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.7f);
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1.0f);
                    break;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSaleDate, tvEmployee, tvTotalAmount, tvItems, tvStatus;

        ViewHolder(View itemView) {
            super(itemView);
            tvSaleDate = itemView.findViewById(R.id.tv_sale_date);
            tvEmployee = itemView.findViewById(R.id.tv_employee);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvItems = itemView.findViewById(R.id.tv_items);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}