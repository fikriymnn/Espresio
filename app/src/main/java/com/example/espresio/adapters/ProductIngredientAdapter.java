package com.example.espresio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.espresio.R;
import com.example.espresio.models.ProductIngredient;
import java.text.DecimalFormat;
import java.util.List;

public class ProductIngredientAdapter extends RecyclerView.Adapter<ProductIngredientAdapter.IngredientViewHolder> {
    private List<ProductIngredient> ingredients;
    private OnIngredientActionListener listener;
    private DecimalFormat decimalFormat;

    public interface OnIngredientActionListener {
        void onEditIngredient(ProductIngredient ingredient, int position);
        void onDeleteIngredient(ProductIngredient ingredient, int position);
    }

    public ProductIngredientAdapter(List<ProductIngredient> ingredients, OnIngredientActionListener listener) {
        this.ingredients = ingredients;
        this.listener = listener;
        this.decimalFormat = new DecimalFormat("#.##");
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        ProductIngredient ingredient = ingredients.get(position);
        holder.bind(ingredient, position);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        private TextView tvIngredientName, tvQuantity;
        private ImageView ivEdit, ivDelete;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredientName = itemView.findViewById(R.id.tv_ingredient_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            ivEdit = itemView.findViewById(R.id.iv_edit);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }

        public void bind(ProductIngredient ingredient, int position) {
            tvIngredientName.setText(ingredient.getIngredientName());

            String quantityText = decimalFormat.format(ingredient.getQuantity()) + " " + ingredient.getUnit();
            tvQuantity.setText(quantityText);

            ivEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditIngredient(ingredient, position);
                }
            });

            ivDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteIngredient(ingredient, position);
                }
            });

            // Make entire item clickable for edit
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditIngredient(ingredient, position);
                }
            });
        }
    }
}