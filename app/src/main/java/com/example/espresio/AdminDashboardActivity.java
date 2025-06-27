package com.example.espresio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Button btnManageProducts, btnManageIngredients, btnPurchaseIngredients,
            btnSalesReport, btnPurchaseReport, btnLogout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        sharedPreferences = getSharedPreferences("CoffeeShopPrefs", MODE_PRIVATE);

        String userName = sharedPreferences.getString("user_name", "Admin");
        tvWelcome.setText("Welcome, " + userName);

        setupClickListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        btnManageProducts = findViewById(R.id.btn_manage_products);
        btnManageIngredients = findViewById(R.id.btn_manage_ingredients);
        btnPurchaseIngredients = findViewById(R.id.btn_purchase_ingredients);
        btnSalesReport = findViewById(R.id.btn_sales_report);
        btnPurchaseReport = findViewById(R.id.btn_purchase_report);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void setupClickListeners() {
        btnManageProducts.setOnClickListener(v ->
                startActivity(new Intent(this, ManageProductsActivity.class)));

        btnManageIngredients.setOnClickListener(v ->
                startActivity(new Intent(this, ManageIngredientsActivity.class)));

        btnPurchaseIngredients.setOnClickListener(v ->
                startActivity(new Intent(this, PurchaseIngredientsActivity.class)));

        btnSalesReport.setOnClickListener(v ->
                startActivity(new Intent(this, SalesReportActivity.class)));

        btnPurchaseReport.setOnClickListener(v ->
                startActivity(new Intent(this, PurchaseReportActivity.class)));

        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        sharedPreferences.edit().clear().apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
