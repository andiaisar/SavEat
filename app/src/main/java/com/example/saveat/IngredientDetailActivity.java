package com.example.saveat;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class IngredientDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_detail);

        String ingredientId = getIntent().getStringExtra("INGREDIENT_ID");

        TextView tvDetail = findViewById(R.id.tvDetail);
        tvDetail.setText("Detail untuk bahan dengan ID: " + ingredientId);

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());
    }
}