package com.example.saveat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButton;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {
    private static final String TAG = "IntroActivity";
    private ViewPager2 viewPager;
    private MaterialButton buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "IntroActivity onCreate started");
        setContentView(R.layout.activity_intro);

        viewPager = findViewById(R.id.viewPagerIntro);
        buttonNext = findViewById(R.id.buttonNext);

        if (viewPager == null) {
            Log.e(TAG, "viewPagerIntro not found in layout!");
            return;
        }

        if (buttonNext == null) {
            Log.e(TAG, "buttonNext not found in layout!");
            return;
        }

        // Setup intro items
        List<IntroItem> introItems = new ArrayList<>();
        introItems.add(new IntroItem(
                "Selamat Datang di SavEat",
                "Siap kelola bahan makanan\nMakan lebih efisien",
                ".",
                R.drawable.intro1));

        introItems.add(new IntroItem(
                "Pantau Kedaluwarsa",
                "Dapatkan notifikasi sebelum\nbahan makanan kadaluarsa",
                ".",
                R.drawable.intro2));

        introItems.add(new IntroItem(
                "Resep Pintar",
                "Rekomendasi resep berdasarkan\nbahan yang tersedia",
                ".",
                R.drawable.intro3));

        introItems.add(new IntroItem(
                "Pengingat Masak",
                "Notifikasi untuk bahan yang\nperlu segera diolah",
                ".",
                R.drawable.intro4));

        IntroAdapter adapter = new IntroAdapter(introItems);
        viewPager.setAdapter(adapter);

        try {
            // Indikator dots (jika pakai library eksternal seperti DotsIndicator)
            DotsIndicator dotsIndicator = findViewById(R.id.dotsIndicator);
            if (dotsIndicator != null) {
                dotsIndicator.setViewPager2(viewPager);
            } else {
                Log.e(TAG, "dots_indicator not found in layout!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up dots indicator: " + e.getMessage());
        }

        // Tombol "Lanjut" atau "Mulai"
        buttonNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < introItems.size() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                // Tandai bahwa intro sudah dilihat
                SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("is_first_run", false);
                editor.apply();
                Log.d(TAG, "is_first_run set to false, navigating to SignInActivity");

                // Pindah ke SignInActivity
                startActivity(new Intent(IntroActivity.this, SignInActivity.class));
                finish();
            }
        });

        // Add this after initializing your other views
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (viewPager.getCurrentItem() > 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                } else {
                    finish(); // Exit intro if on first page
                }
            });
        }

        // Perbarui teks tombol berdasarkan halaman
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == introItems.size() - 1) {
                    buttonNext.setText("Mulai");
                } else {
                    buttonNext.setText("Lanjut");
                }
            }
        });

        Log.d(TAG, "IntroActivity onCreate completed successfully");
    }
}