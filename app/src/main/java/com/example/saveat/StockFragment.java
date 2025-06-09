// File: app/src/main/java/com/example/saveat/StockFragment.java

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class StockFragment extends Fragment {

    private RecyclerView rvIngredients;
    private SearchView searchView;
    private TextView tvCategoryTitle, tvEmptyStock;
    private FloatingActionButton fabAdd;

    private Map<MaterialCardView, String> categoryViews = new LinkedHashMap<>();
    private List<BahanHariIni> allIngredients = new ArrayList<>();
    private List<BahanHariIni> filteredIngredients = new ArrayList<>();
    private BahanAdapter adapter;
    private DatabaseHelper dbHelper;
    private String currentCategory = "Semua";

    private static final int ADD_EDIT_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_stock, container, false); //

        dbHelper = new DatabaseHelper(getContext());
        initViews(view);
        setupRecyclerView();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        rvIngredients = view.findViewById(R.id.rvIngredients); //
        searchView = view.findViewById(R.id.searchView); //
        tvCategoryTitle = view.findViewById(R.id.tvCategoryTitle); //
        tvEmptyStock = view.findViewById(R.id.tvEmptyStock); //
        fabAdd = view.findViewById(R.id.fabAdd); //

        categoryViews.put(view.findViewById(R.id.menuAll), "Semua"); //
        categoryViews.put(view.findViewById(R.id.menuFruit), "Buah"); //
        categoryViews.put(view.findViewById(R.id.menuVegetable), "Sayur"); //
        categoryViews.put(view.findViewById(R.id.menuMeat), "Daging"); //
        categoryViews.put(view.findViewById(R.id.menuDrink), "Minuman"); //
    }

    @Override
    public void onResume() {
        super.onResume();
        loadIngredients();
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TambahEditBahanActivity.class);
            intent.putExtra("mode", "tambah");
            startActivityForResult(intent, ADD_EDIT_REQUEST_CODE);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterIngredients(newText);
                return true;
            }
        });

        for (Map.Entry<MaterialCardView, String> entry : categoryViews.entrySet()) {
            entry.getKey().setOnClickListener(v -> filterByCategory(entry.getValue()));
        }
    }

    private void filterByCategory(String category) {
        currentCategory = category;
        tvCategoryTitle.setText(category);
        updateMenuSelection();
        filterIngredients(searchView.getQuery().toString());
    }

    /**
     * PERUBAHAN UTAMA DI SINI
     * Metode ini sekarang hanya mengubah warna latar belakang kartu.
     * Warna teks tidak diubah, sehingga akan selalu terlihat.
     */
    private void updateMenuSelection() {
        if (getContext() == null) return;

        // Definisikan warna untuk status terpilih dan tidak terpilih
        int selectedColor = ContextCompat.getColor(getContext(), R.color.green); //
        int defaultColor = ContextCompat.getColor(getContext(), R.color.bg_category_unselected_color);

        // Loop melalui setiap kartu kategori
        for (Map.Entry<MaterialCardView, String> entry : categoryViews.entrySet()) {
            MaterialCardView card = entry.getKey();
            String categoryName = entry.getValue();

            // Cek apakah kategori saat ini adalah yang sedang dipilih
            if (categoryName.equalsIgnoreCase(currentCategory)) {
                // Jika ya, set warna latar belakang menjadi hijau (terpilih)
                card.setCardBackgroundColor(selectedColor);
            } else {
                // Jika tidak, set warna latar belakang menjadi abu-abu (default)
                card.setCardBackgroundColor(defaultColor);
            }

            // Kita tidak lagi mengubah warna teks di sini.
            // TextView textView = (TextView) card.getChildAt(0);
            // textView.setTextColor(...); <-- Baris ini dihapus
        }
    }

    private void setupRecyclerView() {
        rvIngredients.setLayoutManager(new GridLayoutManager(getContext(), 3)); //
        adapter = new BahanAdapter(filteredIngredients, new BahanAdapter.OnBahanClickListener() {
            @Override
            public void onBahanClick(int position) {
                if (position >= 0 && position < filteredIngredients.size()) {
                    BahanHariIni clickedBahan = filteredIngredients.get(position);
                    Intent intent = new Intent(getActivity(), TambahEditBahanActivity.class); //
                    intent.putExtra("mode", "edit");
                    intent.putExtra("bahan_id", clickedBahan.getId()); //
                    startActivityForResult(intent, ADD_EDIT_REQUEST_CODE);
                }
            }

            @Override
            public void onBahanLongClick(int position) {
                if (position >= 0 && position < filteredIngredients.size()) {
                    showDeleteConfirmationDialog(filteredIngredients.get(position));
                }
            }
        });
        rvIngredients.setAdapter(adapter);
    }

    private void loadIngredients() {
        long userId = getCurrentUserId();
        new Thread(() -> {
            allIngredients.clear();
            allIngredients.addAll(dbHelper.getAllBahanHariIni(userId)); //
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

    private void filterIngredients(String query) {
        List<BahanHariIni> categoryFiltered = new ArrayList<>();
        if ("Semua".equalsIgnoreCase(currentCategory)) {
            categoryFiltered.addAll(allIngredients);
        } else {
            for (BahanHariIni ingredient : allIngredients) {
                if (currentCategory.equalsIgnoreCase(ingredient.getCategory())) { //
                    categoryFiltered.add(ingredient);
                }
            }
        }

        filteredIngredients.clear();
        if (query.isEmpty()) {
            filteredIngredients.addAll(categoryFiltered);
        } else {
            for (BahanHariIni ingredient : categoryFiltered) {
                if (ingredient.getNama().toLowerCase().contains(query.toLowerCase())) { //
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
            updateEmptyView();
        }

        adapter.notifyDataSetChanged();
    }

    private void showDeleteConfirmationDialog(BahanHariIni bahan) {
        new AlertDialog.Builder(getContext())
                .setTitle("Hapus Bahan")
                .setMessage("Apakah Anda yakin ingin menghapus " + bahan.getNama() + "?") //
                .setPositiveButton("Hapus", (dialog, which) -> deleteBahan(bahan.getId())) //
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteBahan(long bahanId) {
        new Thread(() -> {
            dbHelper.hapusBahan(bahanId); //
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
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", MODE_PRIVATE);
            return prefs.getLong("user_id", -1);
        }
        return -1;
    }
}