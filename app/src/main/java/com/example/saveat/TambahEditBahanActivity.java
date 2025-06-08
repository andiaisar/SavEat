package com.example.saveat;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saveat.database.DatabaseHelper;
import com.example.saveat.model.BahanHariIni;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TambahEditBahanActivity extends AppCompatActivity {
    private EditText etNama, etJumlah;
    private Spinner spSatuan;
    private Button btnPilihTanggal, btnSimpan;
    private Calendar calendar;
    private DatabaseHelper dbHelper;
    private String mode;
    private long bahanId;

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
        btnPilihTanggal = findViewById(R.id.btn_pilih_tanggal);
        btnSimpan = findViewById(R.id.btn_simpan);

        // Setup spinner satuan
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.satuan_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSatuan.setAdapter(adapter);

        // Setup calendar
        calendar = Calendar.getInstance();

        // Jika mode edit, load data bahan
        if (mode.equals("edit")) {
            loadBahanData();
        }

        btnPilihTanggal.setOnClickListener(v -> showDatePicker());
        btnSimpan.setOnClickListener(v -> simpanBahan());
    }

    private void loadBahanData() {
        new Thread(() -> {
            BahanHariIni bahan = dbHelper.getBahanById(bahanId);
            runOnUiThread(() -> {
                if (bahan != null) {
                    etNama.setText(bahan.getNama());
                    etJumlah.setText(String.valueOf(bahan.getJumlah()));

                    // Set spinner satuan
                    for (int i = 0; i < spSatuan.getCount(); i++) {
                        if (spSatuan.getItemAtPosition(i).toString().equals(bahan.getSatuan())) {
                            spSatuan.setSelection(i);
                            break;
                        }
                    }

                    // Set tanggal
                    calendar.setTime(bahan.getKadaluarsa());
                    updateTanggalButton();
                }
            });
        }).start();
    }

    private void showDatePicker() {
        new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show(); // Corrected missing closing parenthesis
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTanggalButton();
        }
    };

    private void updateTanggalButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        btnPilihTanggal.setText(sdf.format(calendar.getTime()));
    }

    private void simpanBahan() {
        String nama = etNama.getText().toString().trim();
        String jumlahStr = etJumlah.getText().toString().trim();
        String satuan = spSatuan.getSelectedItem().toString();

        if (nama.isEmpty() || jumlahStr.isEmpty()) {
            Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        int jumlah = Integer.parseInt(jumlahStr);
        long userId = getCurrentUserId();
        long kadaluarsa = calendar.getTimeInMillis();

        new Thread(() -> {
            boolean success;
            if (mode.equals("tambah")) {
                success = dbHelper.tambahBahan(nama, jumlah, satuan, kadaluarsa, userId) != -1;
            } else {
                success = dbHelper.updateBahan(bahanId, nama, jumlah, satuan, kadaluarsa);
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