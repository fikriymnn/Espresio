package com.example.espresio.helpers;

import com.example.espresio.models.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FirestoreProductHelper {
    private static final String COLLECTION_PRODUCTS = "products";
    private FirebaseFirestore db;

    public FirestoreProductHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public interface OnProductsLoadedListener {
        void onSuccess(List<Product> products);
        void onFailure(String error);
    }

    public interface OnProductOperationListener {
        void onSuccess(String message);
        void onFailure(String error);
    }

    // Load all products
    public void loadProducts(OnProductsLoadedListener listener) {
        db.collection(COLLECTION_PRODUCTS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Product product = document.toObject(Product.class);
                        product.setId(document.getId());
                        products.add(product);
                    }
                    listener.onSuccess(products);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Add new product
    public void addProduct(Product product, OnProductOperationListener listener) {
        product.setCreatedAt(System.currentTimeMillis());
        product.setUpdatedAt(System.currentTimeMillis());

        db.collection(COLLECTION_PRODUCTS)
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    listener.onSuccess("Product added successfully");
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Update product
    public void updateProduct(String productId, Product product, OnProductOperationListener listener) {
        product.setUpdatedAt(System.currentTimeMillis());

        db.collection(COLLECTION_PRODUCTS)
                .document(productId)
                .set(product)
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess("Product updated successfully");
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Delete product
    public void deleteProduct(String productId, OnProductOperationListener listener) {
        db.collection(COLLECTION_PRODUCTS)
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess("Product deleted successfully");
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Toggle product status
    public void toggleProductStatus(String productId, boolean isActive, OnProductOperationListener listener) {
        db.collection(COLLECTION_PRODUCTS)
                .document(productId)
                .update("active", isActive, "updatedAt", System.currentTimeMillis())
                .addOnSuccessListener(aVoid -> {
                    String status = isActive ? "activated" : "deactivated";
                    listener.onSuccess("Product " + status + " successfully");
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Get product by ID
    public void getProductById(String productId, OnProductLoadedListener listener) {
        db.collection(COLLECTION_PRODUCTS)
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            product.setId(documentSnapshot.getId());
                            listener.onSuccess(product);
                        } else {
                            listener.onFailure("Failed to parse product data");
                        }
                    } else {
                        listener.onFailure("Product not found");
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public interface OnProductLoadedListener {
        void onSuccess(Product product);
        void onFailure(String error);
    }
}