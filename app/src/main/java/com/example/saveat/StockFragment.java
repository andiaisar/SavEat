package com.example.saveat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saveat.adapter.IngredientAdapter;
import com.example.saveat.model.Ingredient;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class StockFragment extends Fragment {

    private RecyclerView rvIngredients;
    private SearchView searchView;
    private TextView tvCategoryTitle;
    private MaterialCardView menuAll, menuFruit, menuVegetable, menuMeat, menuDrink;
    private TextView tvAll, tvFruit, tvVegetable, tvMeat, tvDrink;

    private List<Ingredient> allIngredients = new ArrayList<>();
    private List<Ingredient> filteredIngredients = new ArrayList<>();
    private IngredientAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_stock, container, false);

        // Initialize views
        rvIngredients = view.findViewById(R.id.rvIngredients);
        searchView = view.findViewById(R.id.searchView);
        tvCategoryTitle = view.findViewById(R.id.tvCategoryTitle);

        // Initialize menu views
        menuAll = view.findViewById(R.id.menuAll);
        menuFruit = view.findViewById(R.id.menuFruit);
        menuVegetable = view.findViewById(R.id.menuVegetable);
        menuMeat = view.findViewById(R.id.menuMeat);
        menuDrink = view.findViewById(R.id.menuDrink);

        tvAll = view.findViewById(R.id.tvAll);
        tvFruit = view.findViewById(R.id.tvFruit);
        tvVegetable = view.findViewById(R.id.tvVegetable);
        tvMeat = view.findViewById(R.id.tvMeat);
        tvDrink = view.findViewById(R.id.tvDrink);

        // Set up floating action button
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TambahEditBahanActivity.class);
            intent.putExtra("mode", "tambah");
            startActivity(intent);
        });

        // Setup RecyclerView
        rvIngredients.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new IngredientAdapter(filteredIngredients, ingredient -> {
            // Handle item click - navigate to detail
            Intent intent = new Intent(getActivity(), IngredientDetailActivity.class);
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

        return view;
    }

    private void loadIngredients() {
        int appleDrawable = R.drawable.ic_launcher_foreground; // Placeholder
        int bananaDrawable = R.drawable.ic_launcher_foreground; // Placeholder
        int vegetableDrawable = R.drawable.ic_launcher_foreground; // Placeholder
        int meatDrawable = R.drawable.ic_launcher_foreground; // Placeholder
        int drinkDrawable = R.drawable.ic_launcher_foreground; // Placeholder

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
        menuAll.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        menuFruit.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        menuVegetable.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        menuMeat.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        menuDrink.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        tvAll.setTextColor(getResources().getColor(android.R.color.black));
        tvFruit.setTextColor(getResources().getColor(android.R.color.black));
        tvVegetable.setTextColor(getResources().getColor(android.R.color.black));
        tvMeat.setTextColor(getResources().getColor(android.R.color.black));
        tvDrink.setTextColor(getResources().getColor(android.R.color.black));

        selectedCard.setCardBackgroundColor(getResources().getColor(R.color.green));
        selectedText.setTextColor(getResources().getColor(android.R.color.white));
    }
}