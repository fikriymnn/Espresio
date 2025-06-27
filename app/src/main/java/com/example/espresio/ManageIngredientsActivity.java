package com.example.espresio;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.espresio.adapters.IngredientAdapter;
import com.example.espresio.dialogs.AddEditIngredientDialog;
import com.example.espresio.models.Ingredient;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageIngredientsActivity extends AppCompatActivity implements IngredientAdapter.OnIngredientClickListener {
    private RecyclerView rvIngredients;
    private Button btnAddIngredient;
    private IngredientAdapter adapter;
    private List<Ingredient> ingredients;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_ingredients);

        initViews();
        setupRecyclerView();

        db = FirebaseFirestore.getInstance();
        loadIngredients();

        btnAddIngredient.setOnClickListener(v -> showAddIngredientDialog());
    }

    private void initViews() {
        rvIngredients = findViewById(R.id.rv_ingredients);
        btnAddIngredient = findViewById(R.id.btn_add_ingredient);
    }

    private void setupRecyclerView() {
        ingredients = new ArrayList<>();
        adapter = new IngredientAdapter(ingredients, this);
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        rvIngredients.setAdapter(adapter);
    }

    private void loadIngredients() {
        db.collection("ingredients")
                .orderBy("name")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ingredients.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Ingredient ingredient = doc.toObject(Ingredient.class);
                        ingredient.setId(doc.getId());
                        ingredients.add(ingredient);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load ingredients", Toast.LENGTH_SHORT).show());
    }

    private void showAddIngredientDialog() {
        AddEditIngredientDialog dialog = new AddEditIngredientDialog(this, null, ingredient -> {
            // Convert Ingredient object to Map for Firestore
            Map<String, Object> ingredientMap = new HashMap<>();
            ingredientMap.put("name", ingredient.getName());
            ingredientMap.put("stock", ingredient.getStock());
            ingredientMap.put("unit", ingredient.getUnit());
            ingredientMap.put("minStock", ingredient.getMinStock());
            ingredientMap.put("createdAt", ingredient.getCreatedAt());
            ingredientMap.put("updatedAt", ingredient.getUpdatedAt());

            db.collection("ingredients")
                    .add(ingredientMap)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Ingredient added successfully", Toast.LENGTH_SHORT).show();
                        loadIngredients();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to add ingredient", Toast.LENGTH_SHORT).show());
        });
        dialog.show();
    }

    @Override
    public void onEditClick(Ingredient ingredient) {
        AddEditIngredientDialog dialog = new AddEditIngredientDialog(this, ingredient, updatedIngredient -> {
            updatedIngredient.setId(ingredient.getId());
            updatedIngredient.setCreatedAt(ingredient.getCreatedAt());
            updatedIngredient.setUpdatedAt(System.currentTimeMillis());

            // Convert Ingredient object to Map for Firestore
            Map<String, Object> ingredientMap = new HashMap<>();
            ingredientMap.put("name", updatedIngredient.getName());
            ingredientMap.put("stock", updatedIngredient.getStock());
            ingredientMap.put("unit", updatedIngredient.getUnit());
            ingredientMap.put("minStock", updatedIngredient.getMinStock());
            ingredientMap.put("createdAt", updatedIngredient.getCreatedAt());
            ingredientMap.put("updatedAt", updatedIngredient.getUpdatedAt());

            db.collection("ingredients").document(ingredient.getId())
                    .set(ingredientMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Ingredient updated successfully", Toast.LENGTH_SHORT).show();
                        loadIngredients();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to update ingredient", Toast.LENGTH_SHORT).show());
        });
        dialog.show();
    }

    @Override
    public void onDeleteClick(Ingredient ingredient) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Are you sure you want to delete " + ingredient.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteIngredient(ingredient))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteIngredient(Ingredient ingredient) {
        db.collection("ingredients").document(ingredient.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Ingredient deleted successfully", Toast.LENGTH_SHORT).show();
                    loadIngredients();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to delete ingredient", Toast.LENGTH_SHORT).show());
    }
}