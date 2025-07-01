package com.example.espresio.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.espresio.R;
import com.example.espresio.models.Purchase;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PurchaseReportAdapter extends RecyclerView.Adapter<PurchaseReportAdapter.PurchaseViewHolder> {
    private static final String TAG = "PurchaseReportAdapter";

    private Context context;
    private List<Purchase> purchases;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    public PurchaseReportAdapter(Context context, List<Purchase> purchases) {
        this.context = context;
        this.purchases = purchases;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.item_purchase_report, parent, false);
            return new PurchaseViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error creating view holder", e);
            // Create a simple fallback view if layout is missing
            TextView fallbackView = new TextView(context);
            fallbackView.setText("Error loading item");
            fallbackView.setPadding(16, 16, 16, 16);
            return new PurchaseViewHolder(fallbackView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        try {
            if (position >= purchases.size()) {
                Log.w(TAG, "Position out of bounds: " + position);
                return;
            }

            Purchase purchase = purchases.get(position);
            if (purchase == null) {
                Log.w(TAG, "Purchase is null at position: " + position);
                return;
            }

            holder.bind(purchase);
        } catch (Exception e) {
            Log.e(TAG, "Error binding view holder at position " + position, e);
        }
    }

    @Override
    public int getItemCount() {
        return purchases != null ? purchases.size() : 0;
    }

    public void updateData(List<Purchase> newPurchases) {
        try {
            this.purchases = newPurchases != null ? newPurchases : this.purchases;
            notifyDataSetChanged();
        } catch (Exception e) {
            Log.e(TAG, "Error updating data", e);
        }
    }

    private String formatCurrency(double amount) {
        try {
            return currencyFormat.format(amount).replace("IDR", "Rp");
        } catch (Exception e) {
            return "Rp " + String.valueOf(amount);
        }
    }

    public class PurchaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngredientName, tvQuantity, tvUnitPrice, tvSupplier,
                tvPurchaseDate, tvAdminName, tvTotalPrice, tvStatus;

        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                tvIngredientName = itemView.findViewById(R.id.tv_ingredient_name);
                tvQuantity = itemView.findViewById(R.id.tv_quantity);
                tvUnitPrice = itemView.findViewById(R.id.tv_unit_price);
                tvSupplier = itemView.findViewById(R.id.tv_supplier);
                tvPurchaseDate = itemView.findViewById(R.id.tv_purchase_date);
                tvAdminName = itemView.findViewById(R.id.tv_admin_name);
                tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
                tvStatus = itemView.findViewById(R.id.tv_status);
            } catch (Exception e) {
                Log.e(TAG, "Error initializing view holder", e);
            }
        }

        public void bind(Purchase purchase) {
            try {
                // Safely set ingredient name
                if (tvIngredientName != null) {
                    String ingredientName = purchase.getIngredientName();
                    tvIngredientName.setText(ingredientName != null ? ingredientName : "N/A");
                }

                // Safely set quantity
                if (tvQuantity != null) {
                    String unit = purchase.getUnit() != null ? purchase.getUnit() : "";
                    tvQuantity.setText(String.valueOf(purchase.getQuantity()) + " " + unit);
                }

                // Safely set unit price
                if (tvUnitPrice != null) {
                    tvUnitPrice.setText(formatCurrency(purchase.getUnitPrice()));
                }

                // Safely set supplier
                if (tvSupplier != null) {
                    String supplier = purchase.getSupplier();
                    tvSupplier.setText(supplier != null ? supplier : "N/A");
                }

                // Safely set purchase date
                if (tvPurchaseDate != null) {
                    try {
                        tvPurchaseDate.setText(dateFormat.format(new Date(purchase.getPurchaseDate())));
                    } catch (Exception e) {
                        tvPurchaseDate.setText("N/A");
                    }
                }

                // Safely set admin name
                if (tvAdminName != null) {
                    String adminName = purchase.getAdminName();
                    tvAdminName.setText(adminName != null ? adminName : "N/A");
                }

                // Safely set total price
                if (tvTotalPrice != null) {
                    tvTotalPrice.setText(formatCurrency(purchase.getTotalPrice()));
                }

                // Safely set status
                if (tvStatus != null) {
                    String status = purchase.getStatus();
                    if (status != null) {
                        tvStatus.setText(status.toUpperCase());
                        setStatusBackground(status.toLowerCase());
                    } else {
                        tvStatus.setText("N/A");
                        setStatusBackground("default");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error binding purchase data", e);
            }
        }

        private void setStatusBackground(String status) {
            try {
                if (tvStatus == null || context == null) return;

                int statusColor;
                switch (status) {
                    case "completed":
                        statusColor = R.color.green;
                        break;
                    case "pending":
                        statusColor = R.color.orange;
                        break;
                    default:
                        statusColor = R.color.text_secondary;
                        break;
                }

                // Try to set background drawable first, fallback to color if drawables don't exist
                try {
                    int statusBackground;
                    switch (status) {
                        case "completed":
                            statusBackground = R.drawable.status_completed;
                            break;
                        case "pending":
                            statusBackground = R.drawable.status_pending;
                            break;
                        default:
                            statusBackground = R.drawable.status_default;
                            break;
                    }
                    tvStatus.setBackgroundResource(statusBackground);
                } catch (Exception e) {
                    // Fallback to color if drawable doesn't exist
                    try {
                        tvStatus.setBackgroundColor(ContextCompat.getColor(context, statusColor));
                        tvStatus.setPadding(8, 4, 8, 4);
                    } catch (Exception e2) {
                        Log.w(TAG, "Could not set status background", e2);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting status background", e);
            }
        }
    }
}