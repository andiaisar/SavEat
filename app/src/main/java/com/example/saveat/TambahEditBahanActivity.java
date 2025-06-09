// File: app/src/main/java/com/example/saveat/TambahEditBahanActivity.java
package com.example.saveat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class TambahEditBahanActivity extends AppCompatActivity {
    private EditText etNama, etJumlah;
    private Spinner spSatuan, spKategori;
    private Button btnPilihTanggal, btnSimpan, btnPilihGambar;
    private ImageView ivBahanImage;
    private Calendar calendar;
    private DatabaseHelper dbHelper;
    private String mode;
    private long bahanId;
    private Uri imageUri;
    private String currentImagePath; // Untuk menyimpan path gambar yang sudah ada

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_edit_bahan);
        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");
        bahanId = intent.getLongExtra("bahan_id", -1);

        initViews();
        setupSpinners();
        calendar = Calendar.getInstance();

        if ("edit".equals(mode)) {
            setTitle("Edit Bahan");
            loadBahanData();
        } else {
            setTitle("Tambah Bahan");
            updateTanggalButton(); // Set tanggal hari ini sebagai default
        }

        btnPilihTanggal.setOnClickListener(v -> showDatePicker());
        btnSimpan.setOnClickListener(v -> simpanBahan());
        btnPilihGambar.setOnClickListener(v -> openFileChooser());
    }

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
        if (bahanId != -1) {
            new Thread(() -> {
                BahanHariIni bahan = dbHelper.getBahanById(bahanId);
                if (bahan != null) {
                    runOnUiThread(() -> {
                        etNama.setText(bahan.getNama());
                        etJumlah.setText(String.valueOf(bahan.getJumlah()));

                        // Set spinner satuan
                        ArrayAdapter<String> satuanAdapter = (ArrayAdapter<String>) spSatuan.getAdapter();
                        int satuanPosition = satuanAdapter.getPosition(bahan.getSatuan());
                        spSatuan.setSelection(satuanPosition);

                        // Set spinner kategori
                        ArrayAdapter<String> kategoriAdapter = (ArrayAdapter<String>) spKategori.getAdapter();
                        int kategoriPosition = kategoriAdapter.getPosition(bahan.getCategory());
                        spKategori.setSelection(kategoriPosition);

                        // Set tanggal
                        calendar.setTime(bahan.getKadaluarsa());
                        updateTanggalButton();

                        // Load gambar
                        currentImagePath = bahan.getImagePath();
                        if (currentImagePath != null && !currentImagePath.isEmpty()) {
                            imageUri = Uri.parse(currentImagePath);
                            Glide.with(this).load(imageUri).into(ivBahanImage);
                        }
                    });
                }
            }).start();
        }
    }

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTanggalButton();
        };

        new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateTanggalButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        btnPilihTanggal.setText(sdf.format(calendar.getTime()));
    }

    private void simpanBahan() {
        String nama = etNama.getText().toString().trim();
        String jumlahStr = etJumlah.getText().toString().trim();
        String satuan = spSatuan.getSelectedItem().toString();
        String kategori = spKategori.getSelectedItem().toString();

        if (nama.isEmpty() || jumlahStr.isEmpty() || "Pilih Kategori".equals(kategori)) {
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
            return;
        }

        int jumlah = Integer.parseInt(jumlahStr);
        long kadaluarsaTimestamp = calendar.getTimeInMillis();
        long userId = getCurrentUserId();
        String finalImagePath = (imageUri != null) ? imageUri.toString() : currentImagePath;

        if ("edit".equals(mode)) {
            dbHelper.updateBahan(bahanId, nama, jumlah, satuan, kadaluarsaTimestamp, finalImagePath, kategori);
            Toast.makeText(this, "Bahan berhasil diperbarui", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.tambahBahan(nama, jumlah, satuan, kadaluarsaTimestamp, userId, finalImagePath, kategori);
            Toast.makeText(this, "Bahan berhasil ditambahkan", Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }

    private void openFileChooser() {
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
            try {
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                Glide.with(this).load(imageUri).into(ivBahanImage);
                currentImagePath = imageUri.toString(); // Update path saat gambar baru dipilih
            } catch (SecurityException e) {
                Log.e("TambahEditBahan", "Gagal mengambil izin URI persisten", e);
            }
        }
    }

    private long getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getLong("user_id", -1);
    }
}