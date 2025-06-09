// andiaisar/saveat/SavEat-0aebf4ca14c8e3c387ff043acb369607ffc30613/app/src/main/java/com/example/saveat/SignInActivity.java
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

        findViewById(R.id.btnLogin).setOnClickListener(v -> signInUser());
        findViewById(R.id.tvRegister).setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });
        findViewById(R.id.tvForgotPassword).setOnClickListener(v -> {
            Toast.makeText(SignInActivity.this, "Fitur lupa password belum tersedia", Toast.LENGTH_SHORT).show();
        });
    }

    private void signInUser() {
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
            long userId = dbHelper.getUserId(email); // Ambil ID pengguna
            Toast.makeText(SignInActivity.this, "Selamat datang, " + name, Toast.LENGTH_SHORT).show();

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
            Toast.makeText(SignInActivity.this, "Email atau kata sandi salah", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}