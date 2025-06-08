package com.example.saveat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saveat.adapter.IngredientAdapter;
import com.example.saveat.model.Ingredient;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class StockActivity extends AppCompatActivity {

    private RecyclerView rvIngredients;
    private SearchView searchView;
    private TextView tvCategoryTitle;
    private MaterialCardView menuAll, menuFruit, menuVegetable, menuMeat, menuDrink;
    private TextView tvAll, tvFruit, tvVegetable, tvMeat, tvDrink;

    private List<Ingredient> allIngredients = new ArrayList<>();
    private List<Ingredient> filteredIngredients = new ArrayList<>();
    private IngredientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        // Initialize views
        rvIngredients = findViewById(R.id.rvIngredients);
        searchView = findViewById(R.id.searchView);
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);

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
        adapter = new IngredientAdapter(filteredIngredients, ingredient -> {
            // Handle item click - navigate to detail
            Intent intent = new Intent(StockActivity.this, IngredientDetailActivity.class);
            intent.putExtra("INGREDIENT_ID", ingredient.getId());
            startActivity(intent);
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

        menuFruit.setOnClickListener(v -> {
            filterByCategory("buah");
            updateMenuSelection(menuFruit, tvFruit);
        });

        menuVegetable.setOnClickListener(v -> {
            filterByCategory("sayur");
            updateMenuSelection(menuVegetable, tvVegetable);
        });

        menuMeat.setOnClickListener(v -> {
            filterByCategory("daging");
            updateMenuSelection(menuMeat, tvMeat);
        });

        menuDrink.setOnClickListener(v -> {
            filterByCategory("minuman");
            updateMenuSelection(menuDrink, tvDrink);
        });

        // Set default selection
        filterByCategory("all");
        updateMenuSelection(menuAll, tvAll);
    }

//    private void setupBottomNavigation() {
//        LinearLayout menuHome = findViewById(R.id.menuHome);
//        LinearLayout menuIngredients = findViewById(R.id.menuIngredients);
//        LinearLayout menuRecipe = findViewById(R.id.menuRecipe);
//        LinearLayout menuProfile = findViewById(R.id.menuProfile);
//
//        MaterialCardView circleHome = findViewById(R.id.circleHome);
//        MaterialCardView circleIngredients = findViewById(R.id.circleIngredients);
//        MaterialCardView circleRecipe = findViewById(R.id.circleRecipe);
//        MaterialCardView circleProfile = findViewById(R.id.circleProfile);
//
//        ImageView iconHome = findViewById(R.id.iconHome);
//        ImageView iconIngredients = findViewById(R.id.iconIngredients);
//        ImageView iconRecipe = findViewById(R.id.iconRecipe);
//        ImageView iconProfile = findViewById(R.id.iconProfile);
//
//        // Set ingredients tab as active
//        circleHome.setVisibility(View.GONE);
//        iconHome.setVisibility(View.VISIBLE);
//
//        circleIngredients.setVisibility(View.VISIBLE);
//        iconIngredients.setVisibility(View.GONE);
//
//        circleRecipe.setVisibility(View.GONE);
//        iconRecipe.setVisibility(View.VISIBLE);
//
//        circleProfile.setVisibility(View.GONE);
//        iconProfile.setVisibility(View.VISIBLE);
//
//        // Set navigation listeners
//        menuHome.setOnClickListener(v -> {
//            startActivity(new Intent(this, HomeFragment.class));
//            finish();
//        });
//
//        // Add click listener for Recipe/AI menu item
//        menuRecipe.setOnClickListener(v -> {
//            // Update visual indicators
//            circleHome.setVisibility(View.GONE);
//            iconHome.setVisibility(View.VISIBLE);
//
//            circleIngredients.setVisibility(View.GONE);
//            iconIngredients.setVisibility(View.VISIBLE);
//
//            circleRecipe.setVisibility(View.VISIBLE);
//            iconRecipe.setVisibility(View.GONE);
//
//            circleProfile.setVisibility(View.GONE);
//            iconProfile.setVisibility(View.VISIBLE);
//
//            // Navigate to ChatActivity
//            Intent intent = new Intent(StockActivity.this, ChatActivity.class);
//            startActivity(intent);
//            finish(); // Optional: finish current activity if you don't want it in the back stack
//        });
//
//        // Add click listener for Profile menu item, if needed
//        menuProfile.setOnClickListener(v -> {
//            // Update visual indicators for profile
//            circleHome.setVisibility(View.GONE);
//            iconHome.setVisibility(View.VISIBLE);
//
//            circleIngredients.setVisibility(View.GONE);
//            iconIngredients.setVisibility(View.VISIBLE);
//
//            circleRecipe.setVisibility(View.GONE);
//            iconRecipe.setVisibility(View.VISIBLE);
//
//            circleProfile.setVisibility(View.VISIBLE);
//            iconProfile.setVisibility(View.GONE);
//
//            // TODO: Navigate to ProfileActivity when implemented
//            // Example:
//            // Intent intent = new Intent(StockActivity.this, ProfileActivity.class);
//            // startActivity(intent);
//            // finish();
//        });
//    }

    private void loadIngredients() {
        // Replace with actual resource IDs that exist in your project
        // You may need to add these drawables or use existing ones
        int appleDrawable = R.drawable.ic_launcher_foreground; // Placeholder
        int bananaDrawable = R.drawable.ic_launcher_foreground; // Placeholder
        int vegetableDrawable = R.drawable.ic_launcher_foreground; // Placeholder
        int meatDrawable = R.drawable.ic_launcher_foreground; // Placeholder
        int drinkDrawable = R.drawable.ic_launcher_foreground; // Placeholder

        // Add sample data
        allIngredients.add(new Ingredient("1", "Apel", "buah", appleDrawable));
        allIngredients.add(new Ingredient("2", "Pisang", "buah", bananaDrawable));
        allIngredients.add(new Ingredient("3", "Sawi", "sayur", vegetableDrawable));
        allIngredients.add(new Ingredient("4", "Kangkung", "sayur", vegetableDrawable));
        allIngredients.add(new Ingredient("5", "Sapi", "daging", meatDrawable));
        allIngredients.add(new Ingredient("6", "Ayam", "daging", meatDrawable));
        allIngredients.add(new Ingredient("7", "Air Mineral", "minuman", drinkDrawable));

        filteredIngredients.addAll(allIngredients);
        adapter.notifyDataSetChanged();
    }

    private void filterIngredients(String query) {
        filteredIngredients.clear();
        if (query.isEmpty()) {
            filteredIngredients.addAll(allIngredients);
        } else {
            for (Ingredient ingredient : allIngredients) {
                if (ingredient.getName().toLowerCase().contains(query.toLowerCase())) {
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
            for (Ingredient ingredient : allIngredients) {
                if (ingredient.getCategory().equals(category)) {
                    filteredIngredients.add(ingredient);
                }
            }
            switch (category) {
                case "buah":
                    tvCategoryTitle.setText("Buah");
                    break;
                case "sayur":
                    tvCategoryTitle.setText("Sayur");
                    break;
                case "daging":
                    tvCategoryTitle.setText("Daging");
                    break;
                case "minuman":
                    tvCategoryTitle.setText("Minuman");
                    break;
            }
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
}