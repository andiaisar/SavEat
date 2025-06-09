package com.example.saveat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class DetailResepActivity extends AppCompatActivity {

    private ImageView ivMeal;
    private TextView tvInstructions, tvCategory, tvArea;
    private Button btnYoutube;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_resep);

        // Initialize views
        initViews();

        // Setup Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get data from intent
        Intent intent = getIntent();
        String mealName = intent.getStringExtra("meal_name");
        String mealThumb = intent.getStringExtra("meal_thumb");
        String mealInstructions = intent.getStringExtra("meal_instructions");
        String mealCategory = intent.getStringExtra("meal_category");
        String mealArea = intent.getStringExtra("meal_area");
        String youtubeUrl = intent.getStringExtra("meal_youtube");

        // Populate views with data
        if (mealName != null) {
            collapsingToolbar.setTitle(mealName);
            // We also set the title in a separate TextView for better control
            TextView tvMealName = findViewById(R.id.tvMealName);
            tvMealName.setText(mealName);
        }

        if (mealThumb != null) {
            Glide.with(this)
                    .load(mealThumb)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(ivMeal);
        }

        if (mealInstructions != null) {
            tvInstructions.setText(mealInstructions);
        }

        if (mealCategory != null) {
            tvCategory.setText(mealCategory);
        }

        if (mealArea != null) {
            tvArea.setText(mealArea);
        }

        // Handle YouTube button visibility and click
        if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
            btnYoutube.setVisibility(View.VISIBLE);
            btnYoutube.setOnClickListener(v -> {
                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(youtubeIntent);
            });
        }
    }

    private void initViews() {
        ivMeal = findViewById(R.id.ivMeal);
        tvInstructions = findViewById(R.id.tvInstructions);
        tvCategory = findViewById(R.id.tvCategory);
        tvArea = findViewById(R.id.tvArea);
        btnYoutube = findViewById(R.id.btnYoutube);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}