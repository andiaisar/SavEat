package com.example.saveat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saveat.adapter.BahanAdapter;
import com.example.saveat.database.DatabaseHelper;
import com.example.saveat.model.BahanHariIni;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class StockActivity extends AppCompatActivity {

    private RecyclerView rvIngredients;
    private SearchView searchView;
    private TextView tvCategoryTitle, tvEmptyStock;
    private MaterialCardView menuAll, menuFruit, menuVegetable, menuMeat, menuDrink;
    private TextView tvAll, tvFruit, tvVegetable, tvMeat, tvDrink;

    private List<BahanHariIni> allIngredients = new ArrayList<>();
    private List<BahanHariIni> filteredIngredients = new ArrayList<>();
    private BahanAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        rvIngredients = findViewById(R.id.rvIngredients);
        searchView = findViewById(R.id.searchView);
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        tvEmptyStock = findViewById(R.id.tvEmptyStock);

        // Initialize menu views
        menuAll = findViewById(R.id.menuAll);
        menuFruit = findViewById(R.id.menuFruit);
        menuVegetable = findViewById(R.id.menuVegetable);
        menuMeat = findViewById(R.id.menuMeat);
        menuDrink = findViewById(R.id.menuDrink);

        tvAll = findViewById(R.id.tvAll);
        tvFruit = findViewById(R.id.tvFruit);
        tvVegetable = findViewById(R.id.tvVegetable);
        tvMeat = findViewById(R.id.tvMeat);
        tvDrink = findViewById(R.id.tvDrink);

        // Set up floating action button
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(StockActivity.this, TambahEditBahanActivity.class);
            intent.putExtra("mode", "tambah");
            startActivityForResult(intent, 1);
        });

        // Setup RecyclerView
        rvIngredients.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new BahanAdapter(filteredIngredients, new BahanAdapter.OnBahanClickListener() {
            @Override
            public void onBahanClick(int position) {
                Intent intent = new Intent(StockActivity.this, TambahEditBahanActivity.class);
                intent.putExtra("mode", "edit");
                intent.putExtra("bahan_id", filteredIngredients.get(position).getId());
                startActivityForResult(intent, 1);
            }

            @Override
            public void onBahanLongClick(int position) {
                // Handle long click if needed
            }
        });
        rvIngredients.setAdapter(adapter);

        // Load data
        loadIngredients();

        // Setup search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterIngredients(newText);
                return true;
            }
        });

        // Setup menu click listeners
        menuAll.setOnClickListener(v -> {
            filterByCategory("all");
            updateMenuSelection(menuAll, tvAll);
        });

        // ... (other menu listeners)
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadIngredients();
        }
    }

    private void loadIngredients() {
        long userId = getCurrentUserId();
        allIngredients = dbHelper.getAllBahanHariIni(userId);

        if (allIngredients.isEmpty()) {
            rvIngredients.setVisibility(View.GONE);
            tvEmptyStock.setVisibility(View.VISIBLE);
        } else {
            rvIngredients.setVisibility(View.VISIBLE);
            tvEmptyStock.setVisibility(View.GONE);
        }

        filterByCategory("all");
    }

    private void filterIngredients(String query) {
        filteredIngredients.clear();
        if (query.isEmpty()) {
            filteredIngredients.addAll(allIngredients);
        } else {
            for (BahanHariIni ingredient : allIngredients) {
                if (ingredient.getNama().toLowerCase().contains(query.toLowerCase())) {
                    filteredIngredients.add(ingredient);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void filterByCategory(String category) {
        filteredIngredients.clear();
        if (category.equals("all")) {
            filteredIngredients.addAll(allIngredients);
            tvCategoryTitle.setText("Semua Bahan");
        } else {
            // Implement category filtering if you add a category field to BahanHariIni
        }
        adapter.notifyDataSetChanged();
    }

    private void updateMenuSelection(MaterialCardView selectedCard, TextView selectedText) {
        // Reset all cards to default state
        menuAll.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        menuFruit.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        menuVegetable.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        menuMeat.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        menuDrink.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        // Reset all text colors
        tvAll.setTextColor(getResources().getColor(android.R.color.black));
        tvFruit.setTextColor(getResources().getColor(android.R.color.black));
        tvVegetable.setTextColor(getResources().getColor(android.R.color.black));
        tvMeat.setTextColor(getResources().getColor(android.R.color.black));
        tvDrink.setTextColor(getResources().getColor(android.R.color.black));

        // Set selected state
        selectedCard.setCardBackgroundColor(getResources().getColor(R.color.green));
        selectedText.setTextColor(getResources().getColor(android.R.color.white));
    }

    private long getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getLong("user_id", -1);
    }
}