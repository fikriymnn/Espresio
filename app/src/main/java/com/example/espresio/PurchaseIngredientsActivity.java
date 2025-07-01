package com.example.espresio;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.espresio.models.Ingredient;
import com.example.espresio.models.Purchase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class PurchaseIngredientsActivity extends AppCompatActivity {
    private Spinner spinnerIngredients;
    private EditText etQuantity, etUnitPrice, etSupplier;
    private TextView tvTotalPrice, tvUnit;
    private Button btnPurchase;

    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private List<Ingredient> ingredients;
    private ArrayAdapter<String> ingredientAdapter;
    private Ingredient selectedIngredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_ingredients);

        initViews();
        setupSpinner();

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("CoffeeShopPrefs", MODE_PRIVATE);

        loadIngredients();
        setupListeners();
    }

    private void initViews() {
        spinnerIngredients = findViewById(R.id.spinner_ingredients);
        etQuantity = findViewById(R.id.et_quantity);
        etUnitPrice = findViewById(R.id.et_unit_price);
        etSupplier = findViewById(R.id.et_supplier);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvUnit = findViewById(R.id.tv_unit);
        btnPurchase = findViewById(R.id.btn_purchase);
    }

    private void setupSpinner() {
        ingredients = new ArrayList<>();
        List<String> ingredientNames = new ArrayList<>();
        ingredientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ingredientNames);
        ingredientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIngredients.setAdapter(ingredientAdapter);
    }

    private void loadIngredients() {
        db.collection("ingredients")
                .orderBy("name")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ingredients.clear();
                    ingredientAdapter.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Ingredient ingredient = doc.toObject(Ingredient.class);
                        ingredient.setId(doc.getId());
                        ingredients.add(ingredient);
                        ingredientAdapter.add(ingredient.getName());
                    }
                    ingredientAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load ingredients", Toast.LENGTH_SHORT).show());
    }

    private void setupListeners() {
        spinnerIngredients.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position < ingredients.size()) {
                    selectedIngredient = ingredients.get(position);
                    // Update unit display ketika ingredient dipilih
                    tvUnit.setText(selectedIngredient.getUnit());
                    calculateTotal();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Reset unit display ketika tidak ada yang dipilih
                tvUnit.setText("unit");
            }
        });

        etQuantity.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        etUnitPrice.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        btnPurchase.setOnClickListener(v -> processPurchase());
    }

    private void calculateTotal() {
        try {
            double quantity = Double.parseDouble(etQuantity.getText().toString());
            double unitPrice = Double.parseDouble(etUnitPrice.getText().toString());
            // Perbaikan: kalkulasi total = quantity * unitPrice
            double total = quantity * unitPrice;
            tvTotalPrice.setText("Total: Rp " + String.format("%.2f", total));
        } catch (NumberFormatException e) {
            tvTotalPrice.setText("Total: Rp 0.00");
        }
    }

    private void processPurchase() {
        if (selectedIngredient == null) {
            Toast.makeText(this, "Please select an ingredient", Toast.LENGTH_SHORT).show();
            return;
        }

        String quantityStr = etQuantity.getText().toString().trim();
        String unitPriceStr = etUnitPrice.getText().toString().trim();
        String supplier = etSupplier.getText().toString().trim();

        if (quantityStr.isEmpty() || unitPriceStr.isEmpty() || supplier.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double quantity = Double.parseDouble(quantityStr);
            double unitPrice = Double.parseDouble(unitPriceStr);

            String adminId = sharedPreferences.getString("user_id", "");
            String adminName = sharedPreferences.getString("user_name", "");

            Purchase purchase = new Purchase(
                    selectedIngredient.getId(),
                    selectedIngredient.getName(),
                    quantity,
                    selectedIngredient.getUnit(),
                    unitPrice,
                    supplier,
                    adminId,
                    adminName
            );

            // Add purchase record
            db.collection("purchases")
                    .add(purchase)
                    .addOnSuccessListener(documentReference -> {
                        // Update ingredient stock
                        updateIngredientStock(quantity);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to record purchase", Toast.LENGTH_SHORT).show());

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity or price", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateIngredientStock(double purchasedQuantity) {
        double newStock = selectedIngredient.getStock() + purchasedQuantity;
        selectedIngredient.setStock(newStock);
        selectedIngredient.setUpdatedAt(System.currentTimeMillis());

        db.collection("ingredients").document(selectedIngredient.getId())
                .set(selectedIngredient)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Purchase completed successfully!", Toast.LENGTH_SHORT).show();
                    clearForm();
                    loadIngredients(); // Refresh ingredient list
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update ingredient stock", Toast.LENGTH_SHORT).show());
    }

    private void clearForm() {
        etQuantity.setText("");
        etUnitPrice.setText("");
        etSupplier.setText("");
        tvTotalPrice.setText("Total: Rp 0.00");
        tvUnit.setText("unit"); // Reset unit display
        spinnerIngredients.setSelection(0);
    }
}