package com.example.espresio;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.espresio.adapters.ProductAdapter;
import com.example.espresio.adapters.SaleItemAdapter;
import com.example.espresio.models.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SalesActivity extends AppCompatActivity {
    private static final String TAG = "SalesActivity";

    private RecyclerView rvProducts, rvSaleItems;
    private TextView tvTotal;
    private Button btnProcessSale, btnClearAll;
    private ProductAdapter productAdapter;
    private SaleItemAdapter saleItemAdapter;
    private List<Product> productList;
    private List<SaleItem> saleItems;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private double totalAmount = 0;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        initViews();
        setupRecyclerViews();

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("CoffeeShopPrefs", MODE_PRIVATE);
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        loadProducts();
        setupClickListeners();
    }

    private void initViews() {
        rvProducts = findViewById(R.id.rv_products);
        rvSaleItems = findViewById(R.id.rv_sale_items);
        tvTotal = findViewById(R.id.tv_total);
        btnProcessSale = findViewById(R.id.btn_process_sale);
        btnClearAll = findViewById(R.id.btn_clear_all);
    }

    private void setupRecyclerViews() {
        productList = new ArrayList<>();
        saleItems = new ArrayList<>();

        ProductAdapter.OnProductActionListener productActionListener = new ProductAdapter.OnProductActionListener() {
            @Override
            public void onEditProduct(Product product) {
                // For sales activity, clicking on product adds it to cart
                addToSale(product);
            }

            @Override
            public void onDeleteProduct(Product product) {
                // Not used in sales activity
            }

            @Override
            public void onToggleProductStatus(Product product) {
                // Not used in sales activity
            }
        };

        productAdapter = new ProductAdapter(productList, productActionListener);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(productAdapter);

        saleItemAdapter = new SaleItemAdapter(saleItems, this::updateSaleItem, this::removeSaleItem);
        rvSaleItems.setLayoutManager(new LinearLayoutManager(this));
        rvSaleItems.setAdapter(saleItemAdapter);
    }

    private void setupClickListeners() {
        btnProcessSale.setOnClickListener(v -> processSale());
        btnClearAll.setOnClickListener(v -> clearAllItems());
    }

    private void loadProducts() {
        Log.d(TAG, "Starting to load products...");

        // Try multiple approaches to load products
        loadProductsWithActiveFilter();
    }

    private void loadProductsWithActiveFilter() {
        Log.d(TAG, "Loading products with active filter...");

        db.collection("products")
                .whereEqualTo("active", true)  // Try "active" instead of "isActive"
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "Success loading products with 'active' field. Count: " + queryDocumentSnapshots.size());

                    if (queryDocumentSnapshots.size() > 0) {
                        processProductResults(queryDocumentSnapshots);
                    } else {
                        // If no results with "active", try with "isActive"
                        loadProductsWithIsActiveFilter();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load products with 'active' field", e);
                    // Try with "isActive" field
                    loadProductsWithIsActiveFilter();
                });
    }

    private void loadProductsWithIsActiveFilter() {
        Log.d(TAG, "Loading products with isActive filter...");

        db.collection("products")
                .whereEqualTo("isActive", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "Success loading products with 'isActive' field. Count: " + queryDocumentSnapshots.size());

                    if (queryDocumentSnapshots.size() > 0) {
                        processProductResults(queryDocumentSnapshots);
                    } else {
                        // If still no results, load all products
                        loadAllProducts();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load products with 'isActive' field", e);
                    // Load all products as fallback
                    loadAllProducts();
                });
    }

    private void loadAllProducts() {
        Log.d(TAG, "Loading all products as fallback...");

        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "Success loading all products. Count: " + queryDocumentSnapshots.size());

                    productList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            Product product = doc.toObject(Product.class);
                            product.setId(doc.getId());

                            // Only add active products to the list
                            if (product.isActive()) {
                                productList.add(product);
                                Log.d(TAG, "Added product: " + product.getName());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing product document: " + doc.getId(), e);
                        }
                    }

                    Log.d(TAG, "Final product list size: " + productList.size());
                    productAdapter.notifyDataSetChanged();

                    if (productList.isEmpty()) {
                        Toast.makeText(this, "No active products found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load all products", e);
                    Toast.makeText(this, "Failed to load products: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void processProductResults(com.google.firebase.firestore.QuerySnapshot queryDocumentSnapshots) {
        productList.clear();
        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
            try {
                Product product = doc.toObject(Product.class);
                product.setId(doc.getId());
                productList.add(product);
                Log.d(TAG, "Added product: " + product.getName());
            } catch (Exception e) {
                Log.e(TAG, "Error parsing product document: " + doc.getId(), e);
            }
        }

        Log.d(TAG, "Final product list size: " + productList.size());
        productAdapter.notifyDataSetChanged();

        if (productList.isEmpty()) {
            Toast.makeText(this, "No active products found", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToSale(Product product) {
        // Check if product already in cart
        for (SaleItem item : saleItems) {
            if (item.getProductId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                item.setSubtotal(item.getQuantity() * item.getPrice());
                updateTotal();
                saleItemAdapter.notifyDataSetChanged();
                return;
            }
        }

        // Add new item to cart
        SaleItem newItem = new SaleItem(product.getId(), product.getName(), 1, product.getPrice());
        saleItems.add(newItem);
        saleItemAdapter.notifyDataSetChanged();
        updateTotal();
    }

    private void updateSaleItem(SaleItem item, int newQuantity) {
        if (newQuantity <= 0) {
            removeSaleItem(item);
            return;
        }

        item.setQuantity(newQuantity);
        item.setSubtotal(newQuantity * item.getPrice());
        updateTotal();
        saleItemAdapter.notifyDataSetChanged();
    }

    private void removeSaleItem(SaleItem item) {
        saleItems.remove(item);
        saleItemAdapter.notifyDataSetChanged();
        updateTotal();
    }

    private void updateTotal() {
        totalAmount = 0;
        for (SaleItem item : saleItems) {
            totalAmount += item.getSubtotal();
        }
        tvTotal.setText("Total: " + currencyFormat.format(totalAmount));
        btnProcessSale.setEnabled(!saleItems.isEmpty());
    }

    private void processSale() {
        if (saleItems.isEmpty()) {
            Toast.makeText(this, "No items in cart", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Confirm Sale")
                .setMessage("Process sale with total: " + currencyFormat.format(totalAmount) + "?")
                .setPositiveButton("Confirm", (dialog, which) -> executeSale())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void executeSale() {
        String employeeId = sharedPreferences.getString("user_id", "");
        String employeeName = sharedPreferences.getString("user_name", "");

        // Check ingredient availability first
        checkIngredientAvailability(() -> {
            // Create sale record
            Sale sale = new Sale(employeeId, employeeName, new ArrayList<>(saleItems), totalAmount);

            db.collection("sales")
                    .add(sale)
                    .addOnSuccessListener(documentReference -> {
                        // Update ingredient stocks
                        updateIngredientStocks();

                        Toast.makeText(this, "Sale processed successfully!", Toast.LENGTH_SHORT).show();
                        clearAllItems();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to process sale", Toast.LENGTH_SHORT).show());
        });
    }

    private void checkIngredientAvailability(Runnable onSuccess) {
        // This is a simplified check - in production you'd want more robust validation
        List<String> productIds = new ArrayList<>();
        for (SaleItem item : saleItems) {
            productIds.add(item.getProductId());
        }

        // Get all products and their ingredients
        db.collection("products")
                .whereIn("name", productIds)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean canProceed = true;
                    StringBuilder insufficientStock = new StringBuilder();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        product.setId(doc.getId());

                        // Find quantity ordered for this product
                        int orderedQty = 0;
                        for (SaleItem item : saleItems) {
                            if (item.getProductId().equals(product.getId())) {
                                orderedQty = item.getQuantity();
                                break;
                            }
                        }

                        // Check if we have enough ingredients
                        if (product.getIngredients() != null) {
                            for (ProductIngredient pi : product.getIngredients()) {
                                double requiredQty = pi.getQuantity() * orderedQty;
                                // In a real app, you'd check actual ingredient stock here
                                // For now, we'll assume ingredients are available
                            }
                        }
                    }

                    if (canProceed) {
                        onSuccess.run();
                    } else {
                        Toast.makeText(this, "Insufficient ingredients: " + insufficientStock.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateIngredientStocks() {
        for (SaleItem saleItem : saleItems) {
            // Get product details to update ingredient stocks
            db.collection("products").document(saleItem.getProductId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null && product.getIngredients() != null) {
                            for (ProductIngredient pi : product.getIngredients()) {
                                double usedQty = pi.getQuantity() * saleItem.getQuantity();
                                updateIngredientStock(pi.getIngredientId(), usedQty);
                            }
                        }
                    });
        }
    }

    private void updateIngredientStock(String ingredientId, double usedQuantity) {
        db.collection("ingredients").document(ingredientId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Ingredient ingredient = documentSnapshot.toObject(Ingredient.class);
                    if (ingredient != null) {
                        double newStock = ingredient.getStock() - usedQuantity;
                        ingredient.setStock(Math.max(0, newStock)); // Don't allow negative stock
                        ingredient.setUpdatedAt(System.currentTimeMillis());

                        db.collection("ingredients").document(ingredientId)
                                .set(ingredient);
                    }
                });
    }

    private void clearAllItems() {
        saleItems.clear();
        saleItemAdapter.notifyDataSetChanged();
        updateTotal();
    }
}