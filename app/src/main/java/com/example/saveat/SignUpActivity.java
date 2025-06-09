// andiaisar/saveat/SavEat-0aebf4ca14c8e3c387ff043acb369607ffc30613/app/src/main/java/com/example/saveat/SignUpActivity.java
package com.example.saveat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.saveat.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextInputEditText etName, etPassword, etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        dbHelper = new DatabaseHelper(this);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);

        findViewById(R.id.btnRegister).setOnClickListener(v -> registerUser());
        findViewById(R.id.tvLogin).setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dbHelper.cekEmail(email)) {
            etEmail.setError("Email sudah terdaftar");
            return;
        }

        long userId = dbHelper.adduser(name, password, email);
        if (userId > 0) {
            Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();

            // Simpan data penting pengguna di SharedPreferences
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("user_id", userId);
            editor.putString("user_name", name);
            editor.putString("user_email", email);
            editor.putBoolean("is_logged_in", true);
            editor.apply();

            navigateToMain();
        } else {
            Toast.makeText(this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}