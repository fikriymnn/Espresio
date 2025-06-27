package com.example.espresio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EmployeeDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Button btnSales, btnLogout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        initViews();
        sharedPreferences = getSharedPreferences("CoffeeShopPrefs", MODE_PRIVATE);

        String userName = sharedPreferences.getString("user_name", "Employee");
        tvWelcome.setText("Welcome, " + userName);

        btnSales.setOnClickListener(v ->
                startActivity(new Intent(this, SalesActivity.class)));

        btnLogout.setOnClickListener(v -> logout());
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        btnSales = findViewById(R.id.btn_sales);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void logout() {
        sharedPreferences.edit().clear().apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
