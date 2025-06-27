package com.example.espresio;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.espresio.adapters.ProductIngredientAdapter;
import com.example.espresio.helpers.FirestoreProductHelper;
import com.example.espresio.models.Product;
import com.example.espresio.models.ProductIngredient;
import com.example.espresio.models.Ingredient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class AddEditProductActivity extends AppCompatActivity implements ProductIngredientAdapter.OnIngredientActionListener {
    private EditText etProductName, etProductPrice, etProductDescription;
    private RecyclerView rvIngredients;
    private ProductIngredientAdapter ingredientAdapter;
    private List<ProductIngredient> ingredientList;
    private FloatingActionButton fabAddIngredient;
    private Button btnSave;
    private TextView tvTitle;
    private LinearLayout llEmptyIngredients;
    private ImageView ivBack;
    private ProgressDialog progressDialog;

    private FirestoreProductHelper firestoreHelper;
    private FirebaseFirestore db;
    private List<Ingredient> availableIngredients;
    private boolean isEditMode = false;
    private String productId;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        initViews();
        setupRecyclerView();
        setupProgressDialog();
        setupTextWatchers();

        firestoreHelper = new FirestoreProductHelper();
        db = FirebaseFirestore.getInstance();
        availableIngredients = new ArrayList<>();

        // Load ingredients from Firestore
        loadIngredientsFromFirestore();

        // Check if this is edit mode
        checkEditMode();
    }

    private void initViews() {
        etProductName = findViewById(R.id.et_product_name);
        etProductPrice = findViewById(R.id.et_product_price);
        etProductDescription = findViewById(R.id.et_product_description);
        rvIngredients = findViewById(R.id.rv_ingredients);
        fabAddIngredient = findViewById(R.id.fab_add_ingredient);
        btnSave = findViewById(R.id.btn_save);
        tvTitle = findViewById(R.id.tv_title);
        llEmptyIngredients = findViewById(R.id.ll_empty_ingredients);
        ivBack = findViewById(R.id.iv_back);

        fabAddIngredient.setOnClickListener(v -> showAddIngredientDialog());
        btnSave.setOnClickListener(v -> saveProduct());
        ivBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        ingredientList = new ArrayList<>();
        ingredientAdapter = new ProductIngredientAdapter(ingredientList, this);
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        rvIngredients.setAdapter(ingredientAdapter);
        updateEmptyState();
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateForm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etProductName.addTextChangedListener(textWatcher);
        etProductPrice.addTextChangedListener(textWatcher);
        etProductDescription.addTextChangedListener(textWatcher);
    }

    private void loadIngredientsFromFirestore() {
        db.collection("ingredients")
                .orderBy("name")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    availableIngredients.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Ingredient ingredient = doc.toObject(Ingredient.class);
                        ingredient.setId(doc.getId());
                        availableIngredients.add(ingredient);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load ingredients from database", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkEditMode() {
        if (getIntent().hasExtra("is_edit_mode")) {
            isEditMode = getIntent().getBooleanExtra("is_edit_mode", false);
            productId = getIntent().getStringExtra("product_id");

            if (isEditMode && productId != null) {
                tvTitle.setText("Edit Product");
                btnSave.setText("Update Product");
                loadProductData();
            } else {
                tvTitle.setText("Add Product");
                btnSave.setText("Save Product");
            }
        }
    }

    private void loadProductData() {
        progressDialog.setMessage("Loading product data...");
        progressDialog.show();

        firestoreHelper.getProductById(productId, new FirestoreProductHelper.OnProductLoadedListener() {
            @Override
            public void onSuccess(Product product) {
                progressDialog.dismiss();
                currentProduct = product;
                populateFields(product);
            }

            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
                Toast.makeText(AddEditProductActivity.this,
                        "Failed to load product: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateFields(Product product) {
        etProductName.setText(product.getName());
        etProductPrice.setText(String.valueOf(product.getPrice()));
        etProductDescription.setText(product.getDescription());

        if (product.getIngredients() != null) {
            ingredientList.clear();
            ingredientList.addAll(product.getIngredients());
            ingredientAdapter.notifyDataSetChanged();
            updateEmptyState();
        }
    }

    private void showAddIngredientDialog() {
        if (availableIngredients.isEmpty()) {
            Toast.makeText(this, "No ingredients available. Please add ingredients first.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_ingredient_from_list, null);
        Spinner spinnerIngredient = dialogView.findViewById(R.id.spinner_ingredient);
        EditText etQuantity = dialogView.findViewById(R.id.et_quantity);

        // Setup spinner with available ingredients
        List<String> ingredientNames = new ArrayList<>();
        for (Ingredient ingredient : availableIngredients) {
            ingredientNames.add(ingredient.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ingredientNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIngredient.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Ingredient")
                .setView(dialogView)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                int selectedPosition = spinnerIngredient.getSelectedItemPosition();
                String quantityStr = etQuantity.getText().toString().trim();

                if (validateIngredientInput(selectedPosition, quantityStr)) {
                    Ingredient selectedIngredient = availableIngredients.get(selectedPosition);

                    // Check if ingredient already exists in the list
                    boolean alreadyExists = false;
                    for (ProductIngredient pi : ingredientList) {
                        if (pi.getIngredientName().equals(selectedIngredient.getName())) {
                            alreadyExists = true;
                            break;
                        }
                    }

                    if (alreadyExists) {
                        Toast.makeText(this, "This ingredient is already added", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double quantity = Double.parseDouble(quantityStr);
                    ProductIngredient productIngredient = new ProductIngredient(
                            selectedIngredient.getId(),
                            selectedIngredient.getName(),
                            quantity,
                            selectedIngredient.getUnit()
                    );

                    ingredientList.add(productIngredient);
                    ingredientAdapter.notifyItemInserted(ingredientList.size() - 1);
                    updateEmptyState();
                    validateForm();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private boolean validateIngredientInput(int selectedPosition, String quantityStr) {
        if (selectedPosition < 0) {
            Toast.makeText(this, "Please select an ingredient", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onEditIngredient(ProductIngredient ingredient, int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_ingredient_quantity, null);
        TextView tvIngredientName = dialogView.findViewById(R.id.tv_ingredient_name);
        TextView tvUnit = dialogView.findViewById(R.id.tv_unit);
        EditText etQuantity = dialogView.findViewById(R.id.et_quantity);

        // Populate existing data
        tvIngredientName.setText(ingredient.getIngredientName());
        tvUnit.setText(ingredient.getUnit());
        etQuantity.setText(String.valueOf(ingredient.getQuantity()));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Ingredient Quantity")
                .setView(dialogView)
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                String quantityStr = etQuantity.getText().toString().trim();

                if (quantityStr.isEmpty()) {
                    Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double quantity = Double.parseDouble(quantityStr);
                    if (quantity <= 0) {
                        Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ingredient.setQuantity(quantity);
                    ingredientAdapter.notifyItemChanged(position);
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    public void onDeleteIngredient(ProductIngredient ingredient, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Ingredient")
                .setMessage("Are you sure you want to remove '" + ingredient.getIngredientName() + "' from this product?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    ingredientList.remove(position);
                    ingredientAdapter.notifyItemRemoved(position);
                    updateEmptyState();
                    validateForm();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateEmptyState() {
        if (ingredientList.isEmpty()) {
            llEmptyIngredients.setVisibility(View.VISIBLE);
            rvIngredients.setVisibility(View.GONE);
        } else {
            llEmptyIngredients.setVisibility(View.GONE);
            rvIngredients.setVisibility(View.VISIBLE);
        }
    }

    private void validateForm() {
        String name = etProductName.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();

        boolean isValid = !name.isEmpty() && !priceStr.isEmpty() && !description.isEmpty()
                && !ingredientList.isEmpty();

        if (isValid) {
            try {
                double price = Double.parseDouble(priceStr);
                isValid = price > 0;
            } catch (NumberFormatException e) {
                isValid = false;
            }
        }

        btnSave.setEnabled(isValid);
        btnSave.setAlpha(isValid ? 1.0f : 0.5f);
    }

    private void saveProduct() {
        String name = etProductName.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();

        try {
            double price = Double.parseDouble(priceStr);

            Product product;
            if (isEditMode && currentProduct != null) {
                product = currentProduct;
                product.setName(name);
                product.setPrice(price);
                product.setDescription(description);
                product.setIngredients(new ArrayList<>(ingredientList));
            } else {
                product = new Product(name, price, description, new ArrayList<>(ingredientList));
            }

            progressDialog.setMessage(isEditMode ? "Updating product..." : "Saving product...");
            progressDialog.show();

            if (isEditMode) {
                updateProduct(product);
            } else {
                addProduct(product);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
        }
    }

    private void addProduct(Product product) {
        firestoreHelper.addProduct(product, new FirestoreProductHelper.OnProductOperationListener() {
            @Override
            public void onSuccess(String message) {
                progressDialog.dismiss();
                Toast.makeText(AddEditProductActivity.this, message, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
                Toast.makeText(AddEditProductActivity.this,
                        "Failed to add product: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProduct(Product product) {
        firestoreHelper.updateProduct(productId, product, new FirestoreProductHelper.OnProductOperationListener() {
            @Override
            public void onSuccess(String message) {
                progressDialog.dismiss();
                Toast.makeText(AddEditProductActivity.this, message, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
                Toast.makeText(AddEditProductActivity.this,
                        "Failed to update product: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (hasUnsavedChanges()) {
            new AlertDialog.Builder(this)
                    .setTitle("Discard Changes")
                    .setMessage("You have unsaved changes. Are you sure you want to leave?")
                    .setPositiveButton("Discard", (dialog, which) -> {
                        super.onBackPressed();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    private boolean hasUnsavedChanges() {
        if (!isEditMode) {
            // For add mode, check if any field has content
            return !etProductName.getText().toString().trim().isEmpty() ||
                    !etProductPrice.getText().toString().trim().isEmpty() ||
                    !etProductDescription.getText().toString().trim().isEmpty() ||
                    !ingredientList.isEmpty();
        } else if (currentProduct != null) {
            // For edit mode, check if any field has changed
            try {
                String currentName = etProductName.getText().toString().trim();
                String currentPriceStr = etProductPrice.getText().toString().trim();
                String currentDescription = etProductDescription.getText().toString().trim();

                double currentPrice = currentPriceStr.isEmpty() ? 0 : Double.parseDouble(currentPriceStr);

                return !currentName.equals(currentProduct.getName()) ||
                        currentPrice != currentProduct.getPrice() ||
                        !currentDescription.equals(currentProduct.getDescription()) ||
                        ingredientsChanged();
            } catch (NumberFormatException e) {
                return true; // Invalid price means there are changes
            }
        }
        return false;
    }

    private boolean ingredientsChanged() {
        List<ProductIngredient> originalIngredients = currentProduct.getIngredients();
        if (originalIngredients == null) originalIngredients = new ArrayList<>();

        if (ingredientList.size() != originalIngredients.size()) {
            return true;
        }

        for (int i = 0; i < ingredientList.size(); i++) {
            ProductIngredient current = ingredientList.get(i);
            ProductIngredient original = originalIngredients.get(i);

            if (!current.getIngredientName().equals(original.getIngredientName()) ||
                    current.getQuantity() != original.getQuantity() ||
                    !current.getUnit().equals(original.getUnit())) {
                return true;
            }
        }
        return false;
    }
}