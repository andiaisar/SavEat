package com.example.saveat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saveat.adapter.BahanAdapter;
import com.example.saveat.database.DatabaseHelper;
import com.example.saveat.model.BahanHariIni;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class StockFragment extends Fragment {

    private RecyclerView rvIngredients;
    private SearchView searchView;
    private TextView tvCategoryTitle, tvEmptyStock;
    private FloatingActionButton fabAdd;
    private MaterialCardView menuAll, menuFruit, menuVegetable, menuMeat, menuDrink;
    private TextView tvAll, tvFruit, tvVegetable, tvMeat, tvDrink;
    private List<MaterialCardView> menuCards;
    private List<TextView> menuTexts;

    private List<BahanHariIni> allIngredients = new ArrayList<>();
    private List<BahanHariIni> filteredIngredients = new ArrayList<>();
    private BahanAdapter adapter;
    private DatabaseHelper dbHelper;
    private String currentCategory = "Semua";

    private static final int ADD_EDIT_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_stock, container, false);

        dbHelper = new DatabaseHelper(getContext());
        initViews(view);
        setupRecyclerView();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        rvIngredients = view.findViewById(R.id.rvIngredients);
        searchView = view.findViewById(R.id.searchView);
        tvCategoryTitle = view.findViewById(R.id.tvCategoryTitle);
        tvEmptyStock = view.findViewById(R.id.tvEmptyStock);
        fabAdd = view.findViewById(R.id.fabAdd);

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

        menuCards = new ArrayList<>();
        menuCards.add(menuAll);
        menuCards.add(menuFruit);
        menuCards.add(menuVegetable);
        menuCards.add(menuMeat);
        menuCards.add(menuDrink);

        menuTexts = new ArrayList<>();
        menuTexts.add(tvAll);
        menuTexts.add(tvFruit);
        menuTexts.add(tvVegetable);
        menuTexts.add(tvMeat);
        menuTexts.add(tvDrink);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadIngredients();
    }

    private void setupRecyclerView() {
        rvIngredients.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new BahanAdapter(filteredIngredients, new BahanAdapter.OnBahanClickListener() {
            @Override
            public void onBahanClick(int position) {
                Intent intent = new Intent(getActivity(), TambahEditBahanActivity.class);
                intent.putExtra("mode", "edit");
                intent.putExtra("bahan_id", filteredIngredients.get(position).getId());
                startActivityForResult(intent, ADD_EDIT_REQUEST_CODE);
            }

            @Override
            public void onBahanLongClick(int position) {
                showDeleteConfirmationDialog(filteredIngredients.get(position));
            }
        });
        rvIngredients.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TambahEditBahanActivity.class);
            intent.putExtra("mode", "tambah");
            startActivityForResult(intent, ADD_EDIT_REQUEST_CODE);
        });

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

        menuAll.setOnClickListener(v -> filterByCategory("Semua"));
        menuFruit.setOnClickListener(v -> filterByCategory("Buah"));
        menuVegetable.setOnClickListener(v -> filterByCategory("Sayur"));
        menuMeat.setOnClickListener(v -> filterByCategory("Daging"));
        menuDrink.setOnClickListener(v -> filterByCategory("Minuman"));
    }

    private void loadIngredients() {
        long userId = getCurrentUserId();
        new Thread(() -> {
            allIngredients.clear();
            allIngredients.addAll(dbHelper.getAllBahanHariIni(userId));
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    updateEmptyView();
                    filterByCategory(currentCategory);
                });
            }
        }).start();
    }

    private void updateEmptyView() {
        if (allIngredients.isEmpty()) {
            rvIngredients.setVisibility(View.GONE);
            tvEmptyStock.setVisibility(View.VISIBLE);
            tvEmptyStock.setText("Stok dapurmu kosong! Tekan tombol + untuk menambah.");
        } else {
            rvIngredients.setVisibility(View.VISIBLE);
            tvEmptyStock.setVisibility(View.GONE);
        }
    }

    private void filterByCategory(String category) {
        currentCategory = category;
        tvCategoryTitle.setText(category);
        updateMenuSelection();
        filterIngredients(searchView.getQuery().toString());
    }

    private void filterIngredients(String query) {
        List<BahanHariIni> categoryFiltered = new ArrayList<>();
        if ("Semua".equalsIgnoreCase(currentCategory)) {
            categoryFiltered.addAll(allIngredients);
        } else {
            for (BahanHariIni ingredient : allIngredients) {
                if (currentCategory.equalsIgnoreCase(ingredient.getCategory())) {
                    categoryFiltered.add(ingredient);
                }
            }
        }

        filteredIngredients.clear();
        if (query.isEmpty()) {
            filteredIngredients.addAll(categoryFiltered);
        } else {
            for (BahanHariIni ingredient : categoryFiltered) {
                if (ingredient.getNama().toLowerCase().contains(query.toLowerCase())) {
                    filteredIngredients.add(ingredient);
                }
            }
        }

        if (!allIngredients.isEmpty() && filteredIngredients.isEmpty()){
            tvEmptyStock.setVisibility(View.VISIBLE);
            tvEmptyStock.setText("Bahan tidak ditemukan");
            rvIngredients.setVisibility(View.GONE);
        } else if (!allIngredients.isEmpty()) {
            tvEmptyStock.setVisibility(View.GONE);
            rvIngredients.setVisibility(View.VISIBLE);
        } else {
            // Handle case where allIngredients itself is empty
            updateEmptyView();
        }

        adapter.notifyDataSetChanged();
    }

    private void updateMenuSelection() {
        int selectedColor = ContextCompat.getColor(getContext(), R.color.green);
        int defaultColor = ContextCompat.getColor(getContext(), R.color.white);
        int selectedTextColor = ContextCompat.getColor(getContext(), R.color.white);
        int defaultTextColor = ContextCompat.getColor(getContext(), R.color.black);

        for (int i = 0; i < menuCards.size(); i++) {
            MaterialCardView card = menuCards.get(i);
            TextView text = menuTexts.get(i);
            String menuText = text.getText().toString();

            if (menuText.equalsIgnoreCase(currentCategory)) {
                card.setCardBackgroundColor(selectedColor);
                text.setTextColor(selectedTextColor);
            } else {
                card.setCardBackgroundColor(defaultColor);
                text.setTextColor(defaultTextColor);
            }
        }
    }

    private void showDeleteConfirmationDialog(BahanHariIni bahan) {
        new AlertDialog.Builder(getContext())
                .setTitle("Hapus Bahan")
                .setMessage("Apakah Anda yakin ingin menghapus " + bahan.getNama() + "?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteBahan(bahan.getId()))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteBahan(long bahanId) {
        new Thread(() -> {
            dbHelper.hapusBahan(bahanId);
            if (getActivity() != null) {
                getActivity().runOnUiThread(this::loadIngredients);
            }
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            loadIngredients();
        }
    }

    private long getCurrentUserId() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getLong("user_id", -1);
    }
}