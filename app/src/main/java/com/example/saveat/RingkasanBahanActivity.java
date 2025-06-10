package com.example.saveat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
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
    private LinearLayout tvEmptyState;
    private FloatingActionButton fabAddBahan;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringkasan_bahan);

        initViews();
        dbHelper = new DatabaseHelper(this);
        setupRecyclerView();
        setupListeners();
    }

    private void initViews() {
        rvBahanHariIni = findViewById(R.id.rv_bahan_hari_ini);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        fabAddBahan = findViewById(R.id.fab_add_bahan);
        btnBack = findViewById(R.id.btn_back_ringkasan);
    }

    private void setupRecyclerView() {
        bahanAdapter = new BahanAdapter(bahanList, this, R.layout.item_bahan_list);
        rvBahanHariIni.setLayoutManager(new LinearLayoutManager(this));
        rvBahanHariIni.setAdapter(bahanAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
        fabAddBahan.setOnClickListener(v -> {
            Intent intent = new Intent(RingkasanBahanActivity.this, TambahEditBahanActivity.class);
            intent.putExtra("mode", "tambah");
            startActivityForResult(intent, 1);
        });
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
        BahanHariIni bahan = bahanList.get(position);
        Intent intent = new Intent(this, TambahEditBahanActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("bahan_id", bahan.getId());
        startActivityForResult(intent, 2);
    }

    @Override
    public void onBahanLongClick(int position) {
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
            if (success) {
                runOnUiThread(this::loadBahanHariIni);
            }
        }).start();
    }
}