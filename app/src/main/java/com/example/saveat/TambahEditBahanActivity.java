// File: app/src/main/java/com/example/saveat/TambahEditBahanActivity.java
package com.example.saveat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.saveat.database.DatabaseHelper;
import com.example.saveat.model.BahanHariIni;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TambahEditBahanActivity extends AppCompatActivity {
    // ... (deklarasi variabel tetap sama)
    private EditText etNama, etJumlah;
    private Spinner spSatuan, spKategori;
    private Button btnPilihTanggal, btnSimpan, btnPilihGambar;
    private ImageView ivBahanImage;
    private Calendar calendar;
    private DatabaseHelper dbHelper;
    private String mode;
    private long bahanId;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_edit_bahan);
        dbHelper = new DatabaseHelper(this);
        mode = getIntent().getStringExtra("mode");
        bahanId = getIntent().getLongExtra("bahan_id", -1);
        initViews();
        setupSpinners();
        calendar = Calendar.getInstance();
        if (mode.equals("edit")) {
            loadBahanData();
        }
        btnPilihTanggal.setOnClickListener(v -> showDatePicker());
        btnSimpan.setOnClickListener(v -> simpanBahan());
        btnPilihGambar.setOnClickListener(v -> openFileChooser());
    }

    // --- PERUBAHAN UTAMA DI SINI ---
    private void openFileChooser() {
        // Menggunakan ACTION_OPEN_DOCUMENT untuk akses file yang andal dan persisten.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            try {
                // Dengan ACTION_OPEN_DOCUMENT, panggilan ini sekarang akan berhasil.
                getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            Glide.with(this).load(imageUri).into(ivBahanImage);
        }
    }

    // Sisa kode tidak ada perubahan
    private void initViews() {
        etNama = findViewById(R.id.et_nama_bahan);
        etJumlah = findViewById(R.id.et_jumlah);
        spSatuan = findViewById(R.id.sp_satuan);
        spKategori = findViewById(R.id.sp_kategori);
        btnPilihTanggal = findViewById(R.id.btn_pilih_tanggal);
        btnSimpan = findViewById(R.id.btn_simpan);
        btnPilihGambar = findViewById(R.id.btn_pilih_gambar);
        ivBahanImage = findViewById(R.id.iv_bahan_image);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> satuanAdapter = ArrayAdapter.createFromResource(this,
                R.array.satuan_array, android.R.layout.simple_spinner_item);
        satuanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSatuan.setAdapter(satuanAdapter);

        ArrayAdapter<CharSequence> kategoriAdapter = ArrayAdapter.createFromResource(this,
                R.array.kategori_array, android.R.layout.simple_spinner_item);
        kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKategori.setAdapter(kategoriAdapter);
    }

    private void loadBahanData() {
        // (kode loadBahanData tetap sama)
    }

    private void showDatePicker() {
        // (kode showDatePicker tetap sama)
    }

    private void updateTanggalButton() {
        // (kode updateTanggalButton tetap sama)
    }

    private void simpanBahan() {
        // (kode simpanBahan tetap sama)
    }

    private long getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getLong("user_id", -1);
    }
}