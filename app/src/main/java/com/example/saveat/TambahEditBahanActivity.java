package com.example.saveat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

        // Inisialisasi view
        etNama = findViewById(R.id.et_nama_bahan);
        etJumlah = findViewById(R.id.et_jumlah);
        spSatuan = findViewById(R.id.sp_satuan);
        spKategori = findViewById(R.id.sp_kategori);
        btnPilihTanggal = findViewById(R.id.btn_pilih_tanggal);
        btnSimpan = findViewById(R.id.btn_simpan);
        btnPilihGambar = findViewById(R.id.btn_pilih_gambar);
        ivBahanImage = findViewById(R.id.iv_bahan_image);

        // Setup spinner satuan
        ArrayAdapter<CharSequence> satuanAdapter = ArrayAdapter.createFromResource(this,
                R.array.satuan_array, android.R.layout.simple_spinner_item);
        satuanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSatuan.setAdapter(satuanAdapter);

        // Setup spinner kategori
        ArrayAdapter<CharSequence> kategoriAdapter = ArrayAdapter.createFromResource(this,
                R.array.kategori_array, android.R.layout.simple_spinner_item);
        kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKategori.setAdapter(kategoriAdapter);

        calendar = Calendar.getInstance();

        if (mode.equals("edit")) {
            loadBahanData();
        }

        btnPilihTanggal.setOnClickListener(v -> showDatePicker());
        btnSimpan.setOnClickListener(v -> simpanBahan());
        btnPilihGambar.setOnClickListener(v -> openFileChooser());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(ivBahanImage);
        }
    }

    private void loadBahanData() {
        new Thread(() -> {
            BahanHariIni bahan = dbHelper.getBahanById(bahanId);
            runOnUiThread(() -> {
                if (bahan != null) {
                    etNama.setText(bahan.getNama());
                    etJumlah.setText(String.valueOf(bahan.getJumlah()));

                    ArrayAdapter<CharSequence> satuanAdapter = (ArrayAdapter<CharSequence>) spSatuan.getAdapter();
                    if (satuanAdapter != null) {
                        int spinnerPosition = satuanAdapter.getPosition(bahan.getSatuan());
                        spSatuan.setSelection(spinnerPosition);
                    }

                    ArrayAdapter<CharSequence> kategoriAdapter = (ArrayAdapter<CharSequence>) spKategori.getAdapter();
                    if (kategoriAdapter != null) {
                        int spinnerPosition = kategoriAdapter.getPosition(bahan.getCategory());
                        spKategori.setSelection(spinnerPosition);
                    }

                    calendar.setTime(bahan.getKadaluarsa());
                    updateTanggalButton();

                    if (bahan.getImagePath() != null && !bahan.getImagePath().isEmpty()) {
                        imageUri = Uri.parse(bahan.getImagePath());
                        Glide.with(this).load(imageUri).into(ivBahanImage);
                    }
                }
            });
        }).start();
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTanggalButton();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateTanggalButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        btnPilihTanggal.setText(sdf.format(calendar.getTime()));
    }

    private void simpanBahan() {
        String nama = etNama.getText().toString().trim();
        String jumlahStr = etJumlah.getText().toString().trim();
        String satuan = spSatuan.getSelectedItem().toString();
        String kategori = spKategori.getSelectedItem().toString();

        if (nama.isEmpty() || jumlahStr.isEmpty() || spKategori.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Harap isi semua field dan pilih kategori", Toast.LENGTH_SHORT).show();
            return;
        }

        int jumlah = Integer.parseInt(jumlahStr);
        long userId = getCurrentUserId();
        long kadaluarsa = calendar.getTimeInMillis();
        String imagePath = (imageUri != null) ? imageUri.toString() : null;

        new Thread(() -> {
            boolean success;
            if (mode.equals("tambah")) {
                success = dbHelper.tambahBahan(nama, jumlah, satuan, kadaluarsa, userId, imagePath, kategori) != -1;
            } else {
                success = dbHelper.updateBahan(bahanId, nama, jumlah, satuan, kadaluarsa, imagePath, kategori);
            }

            runOnUiThread(() -> {
                if (success) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Gagal menyimpan bahan", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private long getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getLong("user_id", -1);
    }
}