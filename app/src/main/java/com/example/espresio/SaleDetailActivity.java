package com.example.espresio;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.espresio.adapters.SaleDetailItemAdapter;
import com.example.espresio.models.Sale;
import com.example.espresio.models.SaleItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaleDetailActivity extends AppCompatActivity {
    public static final String EXTRA_SALE_ID = "extra_sale_id";

    private TextView tvSaleId, tvSaleDateDetail, tvStatusDetail, tvEmployeeDetail;
    private TextView tvSubtotal, tvTax, tvTotalDetail;
    private RecyclerView rvSaleItems;
    private SaleDetailItemAdapter adapter;

    private FirebaseFirestore db;
    private SimpleDateFormat dateFormat;
    private NumberFormat currencyFormat;
    private String saleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_detail);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sale Details");
        }

        initViews();
        initFormats();

        saleId = getIntent().getStringExtra(EXTRA_SALE_ID);
        if (saleId != null) {
            loadSaleDetails();
        } else {
            Toast.makeText(this, "Sale ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        tvSaleId = findViewById(R.id.tv_sale_id);
        tvSaleDateDetail = findViewById(R.id.tv_sale_date_detail);
        tvStatusDetail = findViewById(R.id.tv_status_detail);
        tvEmployeeDetail = findViewById(R.id.tv_employee_detail);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        //tvTax = findViewById(R.id.tv_tax);
        tvTotalDetail = findViewById(R.id.tv_total_detail);
        rvSaleItems = findViewById(R.id.rv_sale_items);

        setupRecyclerView();
    }

    private void initFormats() {
        db = FirebaseFirestore.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    }

    private void setupRecyclerView() {
        rvSaleItems.setLayoutManager(new LinearLayoutManager(this));
        rvSaleItems.setNestedScrollingEnabled(false);
    }

    private void loadSaleDetails() {
        db.collection("sales")
                .document(saleId)
                .get()
                .addOnSuccessListener(this::displaySaleDetails)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load sale details", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void displaySaleDetails(DocumentSnapshot document) {
        if (!document.exists()) {
            Toast.makeText(this, "Sale not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Sale sale = document.toObject(Sale.class);
        if (sale == null) {
            Toast.makeText(this, "Error loading sale data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        sale.setId(document.getId());
        populateViews(sale);
    }

    private void populateViews(Sale sale) {
        // Header information
        String saleIdText = "Sale ID: #" + sale.getId().substring(0, Math.min(8, sale.getId().length()));
        tvSaleId.setText(saleIdText);
        tvSaleDateDetail.setText(dateFormat.format(new Date(sale.getSaleDate())));
        tvStatusDetail.setText(capitalizeFirst(sale.getStatus()));
        tvEmployeeDetail.setText("Served by: " + sale.getEmployeeName());

        // Setup items adapter
        adapter = new SaleDetailItemAdapter(sale.getItems());
        rvSaleItems.setAdapter(adapter);

        // Calculate and display summary
        displaySummary(sale);
    }

    private void displaySummary(Sale sale) {
        double subtotal = 0;
        if (sale.getItems() != null) {
            for (SaleItem item : sale.getItems()) {
                subtotal += item.getSubtotal();
            }
        }

        // Assuming 10% tax rate
        //double tax = subtotal * 0.10;
        double total = sale.getTotalAmount();

        tvSubtotal.setText(currencyFormat.format(subtotal));
        //tvTax.setText(currencyFormat.format(tax));
        tvTotalDetail.setText(currencyFormat.format(total));
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}