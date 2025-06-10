package com.example.saveat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.card.MaterialCardView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView tvUserName, tvUserEmail;
    private LinearLayout layoutEditProfile, layoutRiwayatBahan, layoutBahasaAplikasi, layoutTentangKami, layoutLogout;

    // Bottom Navigation
    private LinearLayout menuHome, menuIngredients, menuRecipe, menuProfile;
    private MaterialCardView circleHome, circleIngredients, circleRecipe, circleProfile;
    private ImageView iconHome, iconIngredients, iconRecipe, iconProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupClickListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        profileImage = findViewById(R.id.profileImage);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        layoutEditProfile = findViewById(R.id.layoutEditProfile);
        layoutRiwayatBahan = findViewById(R.id.layoutRiwayatBahan);
        layoutLogout = findViewById(R.id.layoutLogout);

        // Bottom Navigation
        menuHome = findViewById(R.id.menuHome);
        menuIngredients = findViewById(R.id.menuIngredients);
        menuRecipe = findViewById(R.id.menuRecipe);
        menuProfile = findViewById(R.id.menuProfile);

        circleHome = findViewById(R.id.circleHome);
        circleIngredients = findViewById(R.id.circleIngredients);
        circleRecipe = findViewById(R.id.circleRecipe);
        circleProfile = findViewById(R.id.circleProfile);

        iconHome = findViewById(R.id.iconHome);
        iconIngredients = findViewById(R.id.iconIngredients);
        iconRecipe = findViewById(R.id.iconRecipe);
        iconProfile = findViewById(R.id.iconProfile);
    }

    private void setupClickListeners() {
        layoutEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        layoutRiwayatBahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, RiwayatBahanActivity.class);
                startActivity(intent);
            }
        });

        layoutBahasaAplikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Bahasa Aplikasi diklik", Toast.LENGTH_SHORT).show();
            }
        });

        layoutTentangKami.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Tentang Kami diklik", Toast.LENGTH_SHORT).show();
            }
        });

        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementasi logout
                Toast.makeText(ProfileActivity.this, "Logout berhasil", Toast.LENGTH_SHORT).show();
                // Bisa tambahkan logika logout di sini
                finish();
            }
        });
    }

    private void setupBottomNavigation() {
        // Set profile tab as active
        circleHome.setVisibility(View.GONE);
        iconHome.setVisibility(View.VISIBLE);

        circleIngredients.setVisibility(View.GONE);
        iconIngredients.setVisibility(View.VISIBLE);

        circleRecipe.setVisibility(View.GONE);
        iconRecipe.setVisibility(View.VISIBLE);

        circleProfile.setVisibility(View.VISIBLE);
        iconProfile.setVisibility(View.GONE);

        // Home
        menuHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeFragment.class);
            startActivity(intent);
            finish();
        });

        // Ingredients
        menuIngredients.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, StockFragment.class);
            startActivity(intent);
            finish();
        });

        // Recipe/AI
        menuRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        });

        // Profile
        menuProfile.setOnClickListener(v -> {
            // Update visual indicators for profile
            circleHome.setVisibility(View.GONE);
            iconHome.setVisibility(View.VISIBLE);

            circleIngredients.setVisibility(View.GONE);
            iconIngredients.setVisibility(View.VISIBLE);

            circleRecipe.setVisibility(View.GONE);
            iconRecipe.setVisibility(View.VISIBLE);

            circleProfile.setVisibility(View.VISIBLE);
            iconProfile.setVisibility(View.GONE);

            // Navigate to ProfileActivity
            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish(); // Close current activity to prevent stacking
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update data profil ketika kembali dari edit profile
        updateProfileData();
    }

    private void updateProfileData() {
        // Ambil data dari SharedPreferences atau database
        // Untuk contoh, menggunakan data default
        tvUserName.setText("Franz Herman");
        tvUserEmail.setText("rookie25@gmail.com");
    }
}