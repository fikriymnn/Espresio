package com.example.espresio;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.espresio.adapters.ProductAdapter;
import com.example.espresio.helpers.FirestoreProductHelper;
import com.example.espresio.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ManageProductsActivity extends AppCompatActivity implements ProductAdapter.OnProductActionListener {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> filteredProductList;
    private FloatingActionButton fabAddProduct;
    private EditText etSearch;
    private LinearLayout llEmptyState; // Changed from TextView to LinearLayout
    private TextView tvEmptyStateText; // Add reference to the actual TextView inside LinearLayout
    private ImageView ivBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;

    private FirestoreProductHelper firestoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        initViews();
        setupRecyclerView();
        setupProgressDialog();
        setupSearchFilter();

        firestoreHelper = new FirestoreProductHelper();
        loadProducts();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_products);
        fabAddProduct = findViewById(R.id.fab_add_product);
        etSearch = findViewById(R.id.et_search);
        llEmptyState = findViewById(R.id.tv_empty_state); // This is actually a LinearLayout
        ivBack = findViewById(R.id.iv_back);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        // Get reference to the TextView inside the LinearLayout
        tvEmptyStateText = llEmptyState.findViewById(R.id.tv_empty_state_text);

        fabAddProduct.setOnClickListener(v -> openAddProductDialog());
        ivBack.setOnClickListener(v -> onBackPressed());

        swipeRefreshLayout.setOnRefreshListener(this::loadProducts);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();
        productAdapter = new ProductAdapter(filteredProductList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(productAdapter);
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    private void loadProducts() {
        if (!swipeRefreshLayout.isRefreshing()) {
            progressDialog.show();
        }

        firestoreHelper.loadProducts(new FirestoreProductHelper.OnProductsLoadedListener() {
            @Override
            public void onSuccess(List<Product> products) {
                hideLoading();
                productList.clear();
                productList.addAll(products);

                // Apply current search filter
                String currentQuery = etSearch.getText().toString();
                filterProducts(currentQuery);
            }

            @Override
            public void onFailure(String error) {
                hideLoading();
                Toast.makeText(ManageProductsActivity.this, "Failed to load products: " + error,
                        Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }

    private void hideLoading() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setupSearchFilter() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterProducts(String query) {
        filteredProductList.clear();
        if (query.isEmpty()) {
            filteredProductList.addAll(productList);
        } else {
            for (Product product : productList) {
                if (product.getName().toLowerCase().contains(query.toLowerCase()) ||
                        product.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    filteredProductList.add(product);
                }
            }
        }
        productAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredProductList.isEmpty()) {
            llEmptyState.setVisibility(View.VISIBLE); // Show LinearLayout
            recyclerView.setVisibility(View.GONE);
            if (etSearch.getText().toString().isEmpty()) {
                tvEmptyStateText.setText("No products available.\nTap + to add your first product!");
            } else {
                tvEmptyStateText.setText("No products found matching your search.");
            }
        } else {
            llEmptyState.setVisibility(View.GONE); // Hide LinearLayout
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void openAddProductDialog() {
        Intent intent = new Intent(this, AddEditProductActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onEditProduct(Product product) {
        Intent intent = new Intent(this, AddEditProductActivity.class);
        intent.putExtra("product_id", product.getId());
        intent.putExtra("is_edit_mode", true);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onDeleteProduct(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete '" + product.getName() + "'?\n\nThis action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteProduct(product);
                })
                .setNegativeButton("Cancel", null)
                .setIcon(R.drawable.ic_warning)
                .show();
    }

    @Override
    public void onToggleProductStatus(Product product) {
        String action = product.isActive() ? "deactivate" : "activate";
        String message = "Are you sure you want to " + action + " '" + product.getName() + "'?";

        new AlertDialog.Builder(this)
                .setTitle("Change Product Status")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> {
                    toggleProductStatus(product);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteProduct(Product product) {
        progressDialog.setMessage("Deleting product...");
        progressDialog.show();

        firestoreHelper.deleteProduct(product.getId(), new FirestoreProductHelper.OnProductOperationListener() {
            @Override
            public void onSuccess(String message) {
                progressDialog.dismiss();
                Toast.makeText(ManageProductsActivity.this, message, Toast.LENGTH_SHORT).show();
                loadProducts(); // Refresh list
            }

            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
                Toast.makeText(ManageProductsActivity.this, "Failed to delete product: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleProductStatus(Product product) {
        progressDialog.setMessage("Updating product status...");
        progressDialog.show();

        boolean newStatus = !product.isActive();

        firestoreHelper.toggleProductStatus(product.getId(), newStatus,
                new FirestoreProductHelper.OnProductOperationListener() {
                    @Override
                    public void onSuccess(String message) {
                        progressDialog.dismiss();
                        Toast.makeText(ManageProductsActivity.this, message, Toast.LENGTH_SHORT).show();

                        // Update local data
                        product.setActive(newStatus);
                        product.setUpdatedAt(System.currentTimeMillis());
                        productAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String error) {
                        progressDialog.dismiss();
                        Toast.makeText(ManageProductsActivity.this, "Failed to update product: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Refresh product list after add/edit
            loadProducts();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadProducts();
    }
}