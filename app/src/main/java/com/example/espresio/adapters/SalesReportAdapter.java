package com.example.espresio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.espresio.R;
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

    public SalesReportAdapter(List<Sale> sales) {
        this.sales = sales;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        holder.tvItems.setText("Items: " + sale.getItems().size());
        holder.tvStatus.setText("Status: " + sale.getStatus());
    }

    @Override
    public int getItemCount() {
        return sales.size();
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