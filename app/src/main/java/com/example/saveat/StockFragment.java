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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;


import com.example.saveat.adapter.BahanAdapter;
import com.example.saveat.database.DatabaseHelper;
import com.example.saveat.model.BahanHariIni;
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

    private List<BahanHariIni> allIngredients = new ArrayList<>();
    private List<BahanHariIni> filteredIngredients = new ArrayList<>();
    private BahanAdapter adapter;
    private DatabaseHelper dbHelper;

    private static final int ADD_EDIT_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_stock, container, false);

        dbHelper = new DatabaseHelper(getContext());

        // Initialize views
        rvIngredients = view.findViewById(R.id.rvIngredients);
        searchView = view.findViewById(R.id.searchView);
        tvCategoryTitle = view.findViewById(R.id.tvCategoryTitle);
        tvEmptyStock = view.findViewById(R.id.tvEmptyStock);
        fabAdd = view.findViewById(R.id.fabAdd);

        setupRecyclerView();
        setupListeners();

        return view;
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
                // Navigate to edit activity
                Intent intent = new Intent(getActivity(), TambahEditBahanActivity.class);
                intent.putExtra("mode", "edit");
                intent.putExtra("bahan_id", filteredIngredients.get(position).getId());
                startActivityForResult(intent, ADD_EDIT_REQUEST_CODE);
            }

            @Override
            public void onBahanLongClick(int position) {
                // Show delete confirmation
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
    }

    private void loadIngredients() {
        long userId = getCurrentUserId();
        // Load from database in a background thread
        new Thread(() -> {
            allIngredients.clear();
            allIngredients.addAll(dbHelper.getAllBahanHariIni(userId));
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    updateEmptyView();
                    filterIngredients(searchView.getQuery().toString());
                });
            }
        }).start();
    }

    private void updateEmptyView() {
        if (allIngredients.isEmpty()) {
            rvIngredients.setVisibility(View.GONE);
            tvEmptyStock.setVisibility(View.VISIBLE);
        } else {
            rvIngredients.setVisibility(View.VISIBLE);
            tvEmptyStock.setVisibility(View.GONE);
        }
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
            loadIngredients();
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
        // Default ke user ID 1 jika tidak ditemukan, sesuaikan jika perlu
        return prefs.getLong("user_id", 1L);
    }
}