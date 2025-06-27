package com.example.espresio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.espresio.R;
import com.example.espresio.models.Ingredient;
import java.text.DecimalFormat;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private List<Ingredient> ingredients;
    private OnIngredientClickListener listener;
    private Context context;
    private DecimalFormat decimalFormat;

    public interface OnIngredientClickListener {
        void onEditClick(Ingredient ingredient);
        void onDeleteClick(Ingredient ingredient);
    }

    public IngredientAdapter(List<Ingredient> ingredients, OnIngredientClickListener listener) {
        this.ingredients = ingredients;
        this.listener = listener;
        this.decimalFormat = new DecimalFormat("#,##0.##");
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvStock, tvUnit, tvMinStock, tvStockStatus;
        private ImageButton btnEdit, btnDelete;
        private View stockIndicator;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_ingredient_name);
            tvStock = itemView.findViewById(R.id.tv_stock);
            tvUnit = itemView.findViewById(R.id.tv_unit);
            tvMinStock = itemView.findViewById(R.id.tv_min_stock);
            tvStockStatus = itemView.findViewById(R.id.tv_stock_status);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            stockIndicator = itemView.findViewById(R.id.stock_indicator);
        }

        public void bind(Ingredient ingredient) {
            tvName.setText(ingredient.getName());
            tvStock.setText(decimalFormat.format(ingredient.getStock()));
            tvUnit.setText(ingredient.getUnit());
            tvMinStock.setText("Min: " + decimalFormat.format(ingredient.getMinStock()));

            // Check stock status
            boolean isLowStock = ingredient.getStock() <= ingredient.getMinStock();

            if (isLowStock) {
                tvStockStatus.setText("Low Stock");
                tvStockStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
                stockIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            } else {
                tvStockStatus.setText("In Stock");
                tvStockStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
                stockIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
            }

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(ingredient);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(ingredient);
                }
            });
        }
    }
}
