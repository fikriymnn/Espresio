package com.example.espresio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.espresio.R;
import com.example.espresio.models.Product;
import com.example.espresio.models.ProductIngredient;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private OnProductActionListener listener;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    public interface OnProductActionListener {
        void onEditProduct(Product product);
        void onDeleteProduct(Product product);
        void onToggleProductStatus(Product product);
    }

    public ProductAdapter(List<Product> products, OnProductActionListener listener) {
        this.products = products;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProductName, tvProductPrice, tvProductDescription,
                tvIngredientCount, tvUpdatedAt, tvStatusLabel;
        private ImageView ivEdit, ivDelete;
        private Switch switchStatus;
        private View statusIndicator;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductDescription = itemView.findViewById(R.id.tv_product_description);
            tvIngredientCount = itemView.findViewById(R.id.tv_ingredient_count);
            tvUpdatedAt = itemView.findViewById(R.id.tv_updated_at);
            tvStatusLabel = itemView.findViewById(R.id.tv_status_label);
            ivEdit = itemView.findViewById(R.id.iv_edit);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            switchStatus = itemView.findViewById(R.id.switch_status);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
        }

        public void bind(Product product) {
            tvProductName.setText(product.getName());
            tvProductPrice.setText(currencyFormat.format(product.getPrice()));
            tvProductDescription.setText(product.getDescription());

            // Show ingredient count
            int ingredientCount = product.getIngredients() != null ? product.getIngredients().size() : 0;
            tvIngredientCount.setText(ingredientCount + " ingredients");

            // Show last updated time
            String updateTime = dateFormat.format(new Date(product.getUpdatedAt()));
            tvUpdatedAt.setText("Updated: " + updateTime);

            // Set status
            switchStatus.setChecked(product.isActive());
            switchStatus.setOnCheckedChangeListener(null); // Prevent unwanted triggers

            if (product.isActive()) {
                tvStatusLabel.setText("Active");
                tvStatusLabel.setTextColor(itemView.getContext().getColor(R.color.success));
                statusIndicator.setBackgroundColor(itemView.getContext().getColor(R.color.success));
            } else {
                tvStatusLabel.setText("Inactive");
                tvStatusLabel.setTextColor(itemView.getContext().getColor(R.color.error));
                statusIndicator.setBackgroundColor(itemView.getContext().getColor(R.color.error));
            }

            // Set click listeners
            ivEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditProduct(product);
                }
            });

            ivDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteProduct(product);
                }
            });

            switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onToggleProductStatus(product);
                }
            });

            // Make entire item clickable for edit
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditProduct(product);
                }
            });
        }
    }
}