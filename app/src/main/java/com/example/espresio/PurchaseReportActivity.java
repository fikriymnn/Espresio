package com.example.espresio;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import com.example.espresio.adapters.PurchaseReportAdapter;
import com.example.espresio.models.Purchase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PurchaseReportActivity extends AppCompatActivity {
//    private TextView tvDateRange, tvTotalPurchases, tvTotalAmount;
//    private Button btnSelectDateRange;
//    private RecyclerView rvPurchaseReport;
//    private PurchaseReportAdapter adapter;
//    private List<Purchase> purchases;
//    private FirebaseFirestore db;
//    private Calendar startDate, endDate;
//    private SimpleDateFormat dateFormat;
//    private NumberFormat currencyFormat;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_purchase_report);
//
//        initViews();
//        setupRecyclerView();
//
//        db = FirebaseFirestore.getInstance();
//        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
//
//        // Set default date range (last 30 days)
//        endDate = Calendar.getInstance();
//        startDate = Calendar.getInstance();
//        startDate.add(Calendar.DAY_OF_MONTH, -30);
//
//        updateDateRangeDisplay();
//        loadPurchaseReport();
//
//        btnSelectDateRange.setOnClickListener(v -> showDateRangePicker());
//    }
//
//    private void initViews() {
//        tvDateRange = findViewById(R.id.tv_date_range);
//        tvTotalPurchases = findViewById(R.id.tv_total_purchases);
//        tvTotalAmount = findViewById(R.id.tv_total_amount);
//        btnSelectDateRange = findViewById(R.id.btn_select_date_range);
//        rvPurchaseReport = findViewById(R.id.rv_purchase_report);
//    }
//
//    private void setupRecyclerView() {
//        purchases = new ArrayList<>();
//        adapter = new PurchaseReportAdapter(purchases);
//        rvPurchaseReport.setLayoutManager(new LinearLayoutManager(this));
//        rvPurchaseReport.setAdapter(adapter);
//    }
//
//    private void showDateRangePicker() {
//        DatePickerDialog startDatePicker = new DatePickerDialog(this,
//                (view, year, month, dayOfMonth) -> {
//                    startDate.set(year, month, dayOfMonth, 0, 0, 0);
//
//                    DatePickerDialog endDatePicker = new DatePickerDialog(this,
//                            (view2, year2, month2, dayOfMonth2) -> {
//                                endDate.set(year2, month2, dayOfMonth2, 23, 59, 59);
//                                updateDateRangeDisplay();
//                                loadPurchaseReport();
//                            },
//                            endDate.get(Calendar.YEAR),
//                            endDate.get(Calendar.MONTH),
//                            endDate.get(Calendar.DAY_OF_MONTH));
//
//                    endDatePicker.getDatePicker().setMinDate(startDate.getTimeInMillis());
//                    endDatePicker.show();
//                },
//                startDate.get(Calendar.YEAR),
//                startDate.get(Calendar.MONTH),
//                startDate.get(Calendar.DAY_OF_MONTH));
//
//        startDatePicker.show();
//    }
//
//    private void updateDateRangeDisplay() {
//        String dateRangeText = dateFormat.format(startDate.getTime()) + " - " +
//                dateFormat.format(endDate.getTime());
//        tvDateRange.setText(dateRangeText);
//    }
//
//    private void loadPurchaseReport() {
//        db.collection("purchases")
//                .whereGreaterThanOrEqualTo("purchaseDate", startDate.getTimeInMillis())
//                .whereLessThanOrEqualTo("purchaseDate", endDate.getTimeInMillis())
//                .orderBy("purchaseDate", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    purchases.clear();
//                    double totalAmount = 0;
//
//                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                        Purchase purchase = doc.toObject(Purchase.class);
//                        purchase.setId(doc.getId());
//                        purchases.add(purchase);
//                        totalAmount += purchase.getTotalPrice();
//                    }
//
//                    adapter.notifyDataSetChanged();
//                    updateSummary(purchases.size(), totalAmount);
//                })
//                .addOnFailureListener(e ->
//                        Toast.makeText(this, "Failed to load purchase report", Toast.LENGTH_SHORT).show());
//    }
//
//    private void updateSummary(int totalPurchases, double totalAmount) {
//        tvTotalPurchases.setText("Total Purchases: " + totalPurchases);
//        tvTotalAmount.setText("Total Amount: " + currencyFormat.format(totalAmount));
//    }
}