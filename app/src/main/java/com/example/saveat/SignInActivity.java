package com.example.saveat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saveat.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class SignInActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private TextInputEditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        dbHelper = new DatabaseHelper(this);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnLogin).setOnClickListener(v -> sinInUser());
        findViewById(R.id.tvRegister).setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            finish();
        });

        findViewById(R.id.tvForgotPassword).setOnClickListener(v -> {
            Toast.makeText(SignInActivity.this, "Fitur lupa password belum tersedia", Toast.LENGTH_SHORT).show();
        });
    }

    private void sinInUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email tidak boleh kosong");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Kata sandi tidak boleh kosong");
            return;
        }

        if (dbHelper.checkUser(email, password)) {
            String name = dbHelper.getUserName(email);
            Toast.makeText(SignInActivity.this, "Selamat datang, " + name, Toast.LENGTH_SHORT).show();

            // Save user name in SharedPreferences
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_name", name);
            editor.apply();

            // Navigate to MainActivity, clear back stack
            navigateToMain();
        } else {
            Toast.makeText(SignInActivity.this, "Email atau kata sandi salah", Toast.LENGTH_SHORT).show();
        }
    }

    // For SignInActivity.java - after successful login
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