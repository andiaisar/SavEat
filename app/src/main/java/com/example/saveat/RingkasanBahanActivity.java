package com.example.saveat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saveat.adapter.BahanAdapter;
import com.example.saveat.database.DatabaseHelper;
import com.example.saveat.model.BahanHariIni;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class RingkasanBahanActivity extends AppCompatActivity implements BahanAdapter.OnBahanClickListener {

    private RecyclerView rvBahanHariIni;
    private BahanAdapter bahanAdapter;
    private List<BahanHariIni> bahanList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddBahan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringkasan_bahan);

        // Inisialisasi view
        rvBahanHariIni = findViewById(R.id.rv_bahan_hari_ini);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        fabAddBahan = findViewById(R.id.fab_add_bahan);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ringkasan Bahan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup database helper
        dbHelper = new DatabaseHelper(this);

        // Setup recyclerview
        bahanAdapter = new BahanAdapter(bahanList, this);
        rvBahanHariIni.setLayoutManager(new LinearLayoutManager(this));
        rvBahanHariIni.setAdapter(bahanAdapter);

        // Setup FAB
        fabAddBahan.setOnClickListener(v -> {
            Intent intent = new Intent(RingkasanBahanActivity.this, TambahEditBahanActivity.class);
            intent.putExtra("mode", "tambah");
            startActivityForResult(intent, 1);
        });

        // Load data
        loadBahanHariIni();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBahanHariIni();
    }

    private void loadBahanHariIni() {
        new Thread(() -> {
            long userId = getCurrentUserId();
            List<BahanHariIni> bahan = dbHelper.getAllBahanHariIni(userId);

            runOnUiThread(() -> {
                bahanList.clear();
                bahanList.addAll(bahan);
                bahanAdapter.notifyDataSetChanged();

                // Tampilkan empty state jika tidak ada data
                if (bahanList.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    rvBahanHariIni.setVisibility(View.GONE);
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    rvBahanHariIni.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }

    private long getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getLong("user_id", -1);
    }

    @Override
    public void onBahanClick(int position) {
        // Handle ketika item bahan diklik
        BahanHariIni bahan = bahanList.get(position);
        Intent intent = new Intent(this, TambahEditBahanActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("bahan_id", bahan.getId());
        startActivityForResult(intent, 2);
    }

    @Override
    public void onBahanLongClick(int position) {
        // Handle long click untuk opsi hapus
        BahanHariIni bahan = bahanList.get(position);
        showDeleteConfirmationDialog(bahan);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadBahanHariIni();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showDeleteConfirmationDialog(BahanHariIni bahan) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Hapus Bahan")
                .setMessage("Yakin ingin menghapus " + bahan.getNama() + "?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteBahan(bahan))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteBahan(BahanHariIni bahan) {
        new Thread(() -> {
            boolean success = dbHelper.hapusBahan(bahan.getId());
            runOnUiThread(() -> {
                if (success) {
                    loadBahanHariIni();
                }
            });
        }).start();
    }
}