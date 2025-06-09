// File: app/src/main/java/com/example/saveat/EditProfileActivity.java
package com.example.saveat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.saveat.database.DatabaseHelper;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private CircleImageView profileImage;
    private ImageView btnBack, btnEditImage;
    private EditText etNama, etEmail;
    private Spinner spinnerGender;
    private Button btnSaveChanges;

    private DatabaseHelper dbHelper;
    private long userId;
    private Uri imageUri;
    private String currentImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);

        initViews();
        setupSpinner();
        setupClickListeners();

        if (userId != -1) {
            loadCurrentData();
        } else {
            Toast.makeText(this, "Gagal memuat data pengguna.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showImagePickerDialog() {
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
                Log.e("EditProfileActivity", "Gagal mengambil izin persisten", e);
            }
            Glide.with(this).load(imageUri).into(profileImage);
        }
    }

    private void initViews() {
        profileImage = findViewById(R.id.profileImage);
        btnBack = findViewById(R.id.btnBack);
        btnEditImage = findViewById(R.id.btnEditImage);
        etNama = findViewById(R.id.etNama);
        etEmail = findViewById(R.id.etEmail);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
    }

    private void setupSpinner() {
        String[] genderOptions = {"Laki-Laki", "Perempuan"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnEditImage.setOnClickListener(v -> {
            if (checkPermissions()) {
                showImagePickerDialog();
            } else {
                requestPermissions();
            }
        });
        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void loadCurrentData() {
        Cursor cursor = dbHelper.getUserDetails(userId);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
            currentImagePath = cursor.getString(cursor.getColumnIndexOrThrow("profile_image_path"));

            etNama.setText(name);
            etEmail.setText(email);

            if (gender != null) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerGender.getAdapter();
                spinnerGender.setSelection(adapter.getPosition(gender));
            }

            if (currentImagePath != null && !currentImagePath.isEmpty()) {
                Glide.with(this).load(Uri.parse(currentImagePath)).into(profileImage);
            }
            cursor.close();
        }
    }

    private void saveChanges() {
        String nama = etNama.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String finalImagePath = (imageUri != null) ? imageUri.toString() : currentImagePath;

        if (nama.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Nama dan Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isUpdated = dbHelper.updateUserProfile(userId, nama, email, gender, finalImagePath);

        if (isUpdated) {
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_name", nama);
            editor.putString("user_email", email);
            editor.apply();

            Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermissions() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImagePickerDialog();
            } else {
                Toast.makeText(this, "Izin diperlukan untuk mengakses galeri", Toast.LENGTH_SHORT).show();
            }
        }
    }
}