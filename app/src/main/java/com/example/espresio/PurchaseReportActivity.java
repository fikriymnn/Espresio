package com.example.espresio;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.espresio.adapters.PurchaseReportAdapter;
import com.example.espresio.models.Purchase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PurchaseReportActivity extends AppCompatActivity {
    private static final String TAG = "PurchaseReportActivity";

    private ImageButton btnBack, btnExport;
    private Button btnStartDate, btnEndDate, btnApplyFilter;
    private Spinner spinnerSupplier, spinnerStatus;
    private TextView tvTotalPurchases, tvTotalAmount;
    private RecyclerView rvPurchases;
    private LinearLayout layoutEmptyState;

    private PurchaseReportAdapter adapter;
    private List<Purchase> allPurchases;
    private List<Purchase> filteredPurchases;
    private FirebaseFirestore db;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    private long startDateFilter = 0;
    private long endDateFilter = System.currentTimeMillis();
    private String selectedSupplier = "All Suppliers";
    private String selectedStatus = "All Status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_report);

        // Initialize Firebase and formatters first
        db = FirebaseFirestore.getInstance();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        initViews();
        setupRecyclerView();
        setupSpinners();
        setupClickListeners();
        setupDefaultDates();
        loadPurchaseData();
    }

    private void initViews() {
        try {
            btnBack = findViewById(R.id.btn_back);
            btnExport = findViewById(R.id.btn_export);
            btnStartDate = findViewById(R.id.btn_start_date);
            btnEndDate = findViewById(R.id.btn_end_date);
            btnApplyFilter = findViewById(R.id.btn_apply_filter);
            spinnerSupplier = findViewById(R.id.spinner_supplier);
            spinnerStatus = findViewById(R.id.spinner_status);
            tvTotalPurchases = findViewById(R.id.tv_total_purchases);
            tvTotalAmount = findViewById(R.id.tv_total_amount);
            rvPurchases = findViewById(R.id.rv_purchases);
            layoutEmptyState = findViewById(R.id.layout_empty_state);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            Toast.makeText(this, "Error initializing interface", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupRecyclerView() {
        try {
            allPurchases = new ArrayList<>();
            filteredPurchases = new ArrayList<>();
            adapter = new PurchaseReportAdapter(this, filteredPurchases);
            rvPurchases.setLayoutManager(new LinearLayoutManager(this));
            rvPurchases.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView", e);
        }
    }

    private void setupSpinners() {
        try {
            // Status spinner
            List<String> statusOptions = Arrays.asList("All Status", "completed", "pending");
            ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, statusOptions);
            statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerStatus.setAdapter(statusAdapter);

            // Supplier spinner will be populated after loading data
            List<String> initialSupplierOptions = Arrays.asList("All Suppliers");
            ArrayAdapter<String> supplierAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, initialSupplierOptions);
            supplierAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSupplier.setAdapter(supplierAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up spinners", e);
        }
    }

    private void setupClickListeners() {
        try {
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            if (btnStartDate != null) {
                btnStartDate.setOnClickListener(v -> showDatePickerDialog(true));
            }

            if (btnEndDate != null) {
                btnEndDate.setOnClickListener(v -> showDatePickerDialog(false));
            }

            if (btnApplyFilter != null) {
                btnApplyFilter.setOnClickListener(v -> applyFilters());
            }

            if (btnExport != null) {
                btnExport.setOnClickListener(v -> exportReport());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners", e);
        }
    }

    private void setupDefaultDates() {
        try {
            // Set default end date to today
            if (btnEndDate != null) {
                btnEndDate.setText(dateFormat.format(new Date()));
            }

            // Set default start date to 30 days ago
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -30);
            startDateFilter = cal.getTimeInMillis();
            if (btnStartDate != null) {
                btnStartDate.setText(dateFormat.format(cal.getTime()));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up default dates", e);
        }
    }

    private void showDatePickerDialog(boolean isStartDate) {
        try {
            Calendar calendar = Calendar.getInstance();
            if (isStartDate && startDateFilter > 0) {
                calendar.setTimeInMillis(startDateFilter);
            } else if (!isStartDate && endDateFilter > 0) {
                calendar.setTimeInMillis(endDateFilter);
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);

                        if (isStartDate) {
                            startDateFilter = selectedDate.getTimeInMillis();
                            if (btnStartDate != null) {
                                btnStartDate.setText(dateFormat.format(selectedDate.getTime()));
                            }
                        } else {
                            // Set end date to end of selected day
                            selectedDate.set(Calendar.HOUR_OF_DAY, 23);
                            selectedDate.set(Calendar.MINUTE, 59);
                            selectedDate.set(Calendar.SECOND, 59);
                            endDateFilter = selectedDate.getTimeInMillis();
                            if (btnEndDate != null) {
                                btnEndDate.setText(dateFormat.format(new Date(selectedDate.getTimeInMillis())));
                            }
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing date picker", e);
        }
    }

    private void loadPurchaseData() {
        try {
            db.collection("purchases")
                    .orderBy("purchaseDate", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        try {
                            allPurchases.clear();
                            Set<String> suppliers = new HashSet<>();
                            suppliers.add("All Suppliers");

                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Purchase purchase = document.toObject(Purchase.class);
                                purchase.setId(document.getId());
                                allPurchases.add(purchase);

                                // Safely add supplier
                                String supplier = purchase.getSupplier();
                                if (supplier != null && !supplier.trim().isEmpty()) {
                                    suppliers.add(supplier);
                                }
                            }

                            // Update supplier spinner
                            List<String> supplierList = new ArrayList<>(suppliers);
                            ArrayAdapter<String> supplierAdapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_spinner_item, supplierList);
                            supplierAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            if (spinnerSupplier != null) {
                                spinnerSupplier.setAdapter(supplierAdapter);
                            }

                            // Apply initial filters
                            applyFilters();

                            Log.d(TAG, "Loaded " + allPurchases.size() + " purchases");
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing purchase data", e);
                            Toast.makeText(this, "Error processing data", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading purchases", e);
                        Toast.makeText(this, "Failed to load purchase data", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up data loading", e);
        }
    }

    private void applyFilters() {
        try {
            if (spinnerSupplier != null && spinnerSupplier.getSelectedItem() != null) {
                selectedSupplier = spinnerSupplier.getSelectedItem().toString();
            }

            if (spinnerStatus != null && spinnerStatus.getSelectedItem() != null) {
                selectedStatus = spinnerStatus.getSelectedItem().toString();
            }

            filteredPurchases.clear();
            double totalAmount = 0;

            for (Purchase purchase : allPurchases) {
                if (purchase == null) continue;

                boolean matchesDateRange = purchase.getPurchaseDate() >= startDateFilter &&
                        purchase.getPurchaseDate() <= endDateFilter;

                boolean matchesSupplier = selectedSupplier.equals("All Suppliers") ||
                        (purchase.getSupplier() != null && purchase.getSupplier().equals(selectedSupplier));

                boolean matchesStatus = selectedStatus.equals("All Status") ||
                        (purchase.getStatus() != null && purchase.getStatus().equalsIgnoreCase(selectedStatus));

                if (matchesDateRange && matchesSupplier && matchesStatus) {
                    filteredPurchases.add(purchase);
                    totalAmount += purchase.getTotalPrice();
                }
            }

            // Update UI
            updateSummary(filteredPurchases.size(), totalAmount);
            if (adapter != null) {
                adapter.updateData(filteredPurchases);
            }

            // Show/hide empty state
            if (filteredPurchases.isEmpty()) {
                if (rvPurchases != null) rvPurchases.setVisibility(View.GONE);
                if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.VISIBLE);
            } else {
                if (rvPurchases != null) rvPurchases.setVisibility(View.VISIBLE);
                if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
            }

            Log.d(TAG, "Applied filters: " + filteredPurchases.size() + " purchases shown");
        } catch (Exception e) {
            Log.e(TAG, "Error applying filters", e);
        }
    }

    private void updateSummary(int totalPurchases, double totalAmount) {
        try {
            if (tvTotalPurchases != null) {
                tvTotalPurchases.setText(String.valueOf(totalPurchases));
            }
            if (tvTotalAmount != null) {
                tvTotalAmount.setText(formatCurrency(totalAmount));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating summary", e);
        }
    }

    private void exportReport() {
        try {
            if (filteredPurchases.isEmpty()) {
                Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder reportContent = new StringBuilder();
            reportContent.append("PURCHASE REPORT\n");
            reportContent.append("Generated on: ").append(dateFormat.format(new Date())).append("\n");

            if (btnStartDate != null && btnEndDate != null) {
                reportContent.append("Period: ").append(btnStartDate.getText()).append(" - ").append(btnEndDate.getText()).append("\n");
            }

            reportContent.append("Supplier Filter: ").append(selectedSupplier).append("\n");
            reportContent.append("Status Filter: ").append(selectedStatus).append("\n");
            reportContent.append("=".repeat(50)).append("\n\n");

            reportContent.append("SUMMARY\n");
            reportContent.append("Total Purchases: ").append(filteredPurchases.size()).append("\n");

            if (tvTotalAmount != null) {
                reportContent.append("Total Amount: ").append(tvTotalAmount.getText()).append("\n");
            }

            reportContent.append("=".repeat(50)).append("\n\n");

            reportContent.append("DETAILED PURCHASES\n");
            reportContent.append(String.format("%-20s %-10s %-15s %-15s %-20s %-10s %-15s\n",
                    "Ingredient", "Qty", "Unit Price", "Total", "Supplier", "Status", "Date"));
            reportContent.append("-".repeat(105)).append("\n");

            for (Purchase purchase : filteredPurchases) {
                if (purchase == null) continue;

                String ingredientName = purchase.getIngredientName() != null ? purchase.getIngredientName() : "N/A";
                String supplier = purchase.getSupplier() != null ? purchase.getSupplier() : "N/A";
                String status = purchase.getStatus() != null ? purchase.getStatus() : "N/A";
                String unit = purchase.getUnit() != null ? purchase.getUnit() : "";

                reportContent.append(String.format("%-20s %-10s %-15s %-15s %-20s %-10s %-15s\n",
                        truncateString(ingredientName, 20),
                        purchase.getQuantity() + " " + unit,
                        formatCurrency(purchase.getUnitPrice()),
                        formatCurrency(purchase.getTotalPrice()),
                        truncateString(supplier, 20),
                        status,
                        dateFormat.format(new Date(purchase.getPurchaseDate()))
                ));
            }

            // Share the report
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Purchase Report - " + dateFormat.format(new Date()));
            shareIntent.putExtra(Intent.EXTRA_TEXT, reportContent.toString());
            startActivity(Intent.createChooser(shareIntent, "Export Purchase Report"));
        } catch (Exception e) {
            Log.e(TAG, "Error exporting report", e);
            Toast.makeText(this, "Error exporting report", Toast.LENGTH_SHORT).show();
        }
    }

    private String truncateString(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str != null ? str : "";
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    private String formatCurrency(double amount) {
        try {
            return currencyFormat.format(amount).replace("IDR", "Rp");
        } catch (Exception e) {
            return "Rp " + String.valueOf(amount);
        }
    }
}