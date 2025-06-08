package com.example.saveat;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DetailResepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_resep);

        String mealName = getIntent().getStringExtra("meal_name");
        String mealThumb = getIntent().getStringExtra("meal_thumb");

        ImageView ivMeal = findViewById(R.id.ivMeal);
        TextView tvMealName = findViewById(R.id.tvMealName);

        tvMealName.setText(mealName);
        Glide.with(this)
                .load(mealThumb)
                .into(ivMeal);

        // You can add more details here from the API
    }
}