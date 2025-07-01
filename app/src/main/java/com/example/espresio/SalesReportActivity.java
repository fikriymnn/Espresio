package com.example.espresio;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.espresio.adapters.SalesReportAdapter;
import com.example.espresio.models.Sale;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SalesReportActivity extends AppCompatActivity {
    private TextView tvDateRange, tvTotalSales, tvTotalAmount;
    private Button btnSelectDateRange;
    private RecyclerView rvSalesReport;
    private SalesReportAdapter adapter;
    private List<Sale> sales;
    private FirebaseFirestore db;
    private Calendar startDate, endDate;
    private SimpleDateFormat dateFormat;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sales Report");
        }

        initViews();
        setupRecyclerView();

        db = FirebaseFirestore.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        // Set default date range (last 7 days)
        endDate = Calendar.getInstance();
        startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -7);

        updateDateRangeDisplay();
        loadSalesReport();

        btnSelectDateRange.setOnClickListener(v -> showDateRangePicker());
    }

    private void initViews() {
        tvDateRange = findViewById(R.id.tv_date_range);
        tvTotalSales = findViewById(R.id.tv_total_sales);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        btnSelectDateRange = findViewById(R.id.btn_select_date_range);
        rvSalesReport = findViewById(R.id.rv_sales_report);
    }

    private void setupRecyclerView() {
        sales = new ArrayList<>();
        adapter = new SalesReportAdapter(sales);
        rvSalesReport.setLayoutManager(new LinearLayoutManager(this));
        rvSalesReport.setAdapter(adapter);
    }

    private void showDateRangePicker() {
        // Show start date picker first
        DatePickerDialog startDatePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    startDate.set(year, month, dayOfMonth, 0, 0, 0);

                    // Then show end date picker
                    DatePickerDialog endDatePicker = new DatePickerDialog(this,
                            (view2, year2, month2, dayOfMonth2) -> {
                                endDate.set(year2, month2, dayOfMonth2, 23, 59, 59);
                                updateDateRangeDisplay();
                                loadSalesReport();
                            },
                            endDate.get(Calendar.YEAR),
                            endDate.get(Calendar.MONTH),
                            endDate.get(Calendar.DAY_OF_MONTH));

                    endDatePicker.getDatePicker().setMinDate(startDate.getTimeInMillis());
                    endDatePicker.show();
                },
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH));

        startDatePicker.show();
    }

    private void updateDateRangeDisplay() {
        String dateRangeText = dateFormat.format(startDate.getTime()) + " - " +
                dateFormat.format(endDate.getTime());
        tvDateRange.setText(dateRangeText);
    }

    private void loadSalesReport() {
        db.collection("sales")
                .whereGreaterThanOrEqualTo("saleDate", startDate.getTimeInMillis())
                .whereLessThanOrEqualTo("saleDate", endDate.getTimeInMillis())
                .orderBy("saleDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    sales.clear();
                    double totalAmount = 0;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Sale sale = doc.toObject(Sale.class);
                        sale.setId(doc.getId());
                        sales.add(sale);
                        totalAmount += sale.getTotalAmount();
                    }

                    adapter.notifyDataSetChanged();
                    updateSummary(sales.size(), totalAmount);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load sales report", Toast.LENGTH_SHORT).show());
    }

    private void updateSummary(int totalSales, double totalAmount) {
        tvTotalSales.setText("Total Sales: " + totalSales);
        tvTotalAmount.setText("Total Amount: " + currencyFormat.format(totalAmount));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}