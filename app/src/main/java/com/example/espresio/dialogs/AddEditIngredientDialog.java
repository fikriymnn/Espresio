package com.example.espresio.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.espresio.R;
import com.example.espresio.models.Ingredient;

public class AddEditIngredientDialog extends Dialog {
    private EditText etName, etStock, etMinStock;
    private AutoCompleteTextView spinnerUnit;
    private Button btnSave, btnCancel;
    private TextView tvTitle;

    private Ingredient ingredient;
    private OnIngredientSaveListener listener;
    private boolean isEditMode;

    public interface OnIngredientSaveListener {
        void onSave(Ingredient ingredient);
    }

    public AddEditIngredientDialog(@NonNull Context context, Ingredient ingredient, OnIngredientSaveListener listener) {
        super(context);
        this.ingredient = ingredient;
        this.listener = listener;
        this.isEditMode = ingredient != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_edit_ingredient);

        initViews();
        setupSpinner();
        setupData();
        setupClickListeners();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_dialog_title);
        etName = findViewById(R.id.et_ingredient_name);
        etStock = findViewById(R.id.et_stock);
        etMinStock = findViewById(R.id.et_min_stock);
        spinnerUnit = findViewById(R.id.spinner_unit);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setupSpinner() {
        String[] units = {"grams", "ml"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, units);
        spinnerUnit.setAdapter(adapter);
    }

    private void setupData() {
        if (isEditMode) {
            tvTitle.setText("Edit Ingredient");
            etName.setText(ingredient.getName());
            etStock.setText(String.valueOf(ingredient.getStock()));
            etMinStock.setText(String.valueOf(ingredient.getMinStock()));

            // Set AutoCompleteTextView text
            spinnerUnit.setText(ingredient.getUnit(), false);
        } else {
            tvTitle.setText("Add Ingredient");
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveIngredient());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void saveIngredient() {
        String name = etName.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        String minStockStr = etMinStock.getText().toString().trim();
        String unit = spinnerUnit.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(stockStr)) {
            etStock.setError("Stock is required");
            etStock.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(minStockStr)) {
            etMinStock.setError("Minimum stock is required");
            etMinStock.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(unit)) {
            spinnerUnit.setError("Unit is required");
            spinnerUnit.requestFocus();
            return;
        }

        double stock, minStock;
        try {
            stock = Double.parseDouble(stockStr);
            minStock = Double.parseDouble(minStockStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        if (stock < 0 || minStock < 0) {
            Toast.makeText(getContext(), "Stock values cannot be negative", Toast.LENGTH_SHORT).show();
            return;
        }

        Ingredient newIngredient;
        if (isEditMode) {
            newIngredient = ingredient;
            newIngredient.setName(name);
            newIngredient.setStock(stock);
            newIngredient.setUnit(unit);
            newIngredient.setMinStock(minStock);
            newIngredient.setUpdatedAt(System.currentTimeMillis());
        } else {
            newIngredient = new Ingredient(name, stock, unit, minStock);
        }

        if (listener != null) {
            listener.onSave(newIngredient);
        }

        dismiss();
    }
}