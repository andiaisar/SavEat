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
            // Navigate to SignInActivity
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (name.isEmpty()){
            etName.setError("Nama tidak boleh kosong");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Kata sandi tidak boleh kosong");
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email tidak boleh kosong");
            return;
        }

        if (dbHelper.cekEmail(email)){
            etEmail.setError("Email sudah terdaftar");
            return;
        }

        long id = dbHelper.adduser(name, password, email);
        if (id > 0 ) {
            Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();

            // Save user name in SharedPreferences
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_name", name);
            editor.apply();

            // Navigate to MainActivity, clear back stack
            navigateToMain();
        }else {
            Toast.makeText(this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
        }
    }

    // For SignUpActivity.java - after successful registration
    private void navigateToMain() {
        // Set logged in flag
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit().putBoolean("is_logged_in", true).apply();

        Intent intent = new Intent(this, MainActivity.class);
        // Clear back stack so user can't go back to login screen
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Close the current activity
    }
}