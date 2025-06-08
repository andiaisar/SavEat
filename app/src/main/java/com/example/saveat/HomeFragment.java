package com.example.saveat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.saveat.APIService.ApiService;
import com.example.saveat.APIService.RetrofitClient;
import com.example.saveat.database.DatabaseHelper;
import com.example.saveat.model.MealResponse;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private List<MealResponse.Meal> popularRecipesList = new ArrayList<>();
    private DatabaseHelper dbHelper;

    // Views
    private TextView tvNamaUser;
    private MaterialCardView cardRingkasanBahan;
    private LinearLayout tvLihatDetail; // Diubah ke LinearLayout agar sesuai dengan ID baru

    // ===== AWAL PERUBAHAN =====
    private MaterialCardView loadingPlaceholder; // Wadah untuk progress bar dan teks
    private LinearLayout hsvPopularRecipes;
    // ===== AKHIR PERUBAHAN =====

    // Recipe cards
    private MaterialCardView cardRecipe1, cardRecipe2, cardRecipe3, cardRecipe4, cardRecipe5, cardRecipe6;
    private TextView tvRecipe1Title, tvRecipe2Title, tvRecipe3Title, tvRecipe4Title, tvRecipe5Title, tvRecipe6Title;
    private ImageView ivRecipe1, ivRecipe2, ivRecipe3, ivRecipe4, ivRecipe5, ivRecipe6;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        dbHelper = new DatabaseHelper(getActivity());
        initViews(view);

        // Langsung panggil loadPopularRecipes
        loadPopularRecipes();

        return view;
    }

    private void initViews(View view) {
        tvNamaUser = view.findViewById(R.id.tvNamaUser);
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", getActivity().MODE_PRIVATE);
        String userName = prefs.getString("user_name", "User");
        tvNamaUser.setText("Hello, " + userName + "!");

        cardRingkasanBahan = view.findViewById(R.id.cardRingkasanBahan);
        tvLihatDetail = view.findViewById(R.id.tvLihatDetail);

        View.OnClickListener ringkasanListener = v -> {
            Intent intent = new Intent(getActivity(), RingkasanBahanActivity.class);
            startActivity(intent);
        };
        if (cardRingkasanBahan != null) cardRingkasanBahan.setOnClickListener(ringkasanListener);
        if (tvLihatDetail != null) tvLihatDetail.setOnClickListener(ringkasanListener);

        // ===== AWAL PERUBAHAN =====
        loadingPlaceholder = view.findViewById(R.id.loadingPlaceholder);
        hsvPopularRecipes = view.findViewById(R.id.hsvPopularRecipes);
        // ===== AKHIR PERUBAHAN =====

        // Find recipe card containers
        cardRecipe1 = view.findViewById(R.id.cardRecipe1);
        cardRecipe2 = view.findViewById(R.id.cardRecipe2);
        cardRecipe3 = view.findViewById(R.id.cardRecipe3);
        cardRecipe4 = view.findViewById(R.id.cardRecipe4);
        cardRecipe5 = view.findViewById(R.id.cardRecipe5);
        cardRecipe6 = view.findViewById(R.id.cardRecipe6);

        // Find ImageViews and TextViews inside each card
        if (cardRecipe1 != null) {
            ivRecipe1 = cardRecipe1.findViewById(R.id.ivRecipe);
            tvRecipe1Title = cardRecipe1.findViewById(R.id.tvRecipeTitle);
        }
        if (cardRecipe2 != null) {
            ivRecipe2 = cardRecipe2.findViewById(R.id.ivRecipe);
            tvRecipe2Title = cardRecipe2.findViewById(R.id.tvRecipeTitle);
        }
        if (cardRecipe3 != null) {
            ivRecipe3 = cardRecipe3.findViewById(R.id.ivRecipe);
            tvRecipe3Title = cardRecipe3.findViewById(R.id.tvRecipeTitle);
        }
        if (cardRecipe4 != null) {
            ivRecipe4 = cardRecipe4.findViewById(R.id.ivRecipe);
            tvRecipe4Title = cardRecipe4.findViewById(R.id.tvRecipeTitle);
        }
        if (cardRecipe5 != null) {
            ivRecipe5 = cardRecipe5.findViewById(R.id.ivRecipe);
            tvRecipe5Title = cardRecipe5.findViewById(R.id.tvRecipeTitle);
        }
        if (cardRecipe6 != null) {
            ivRecipe6 = cardRecipe6.findViewById(R.id.ivRecipe);
            tvRecipe6Title = cardRecipe6.findViewById(R.id.tvRecipeTitle);
        }
    }

    private void loadPopularRecipes() {
        // PERBAIKAN: Tampilkan placeholder loading dan sembunyikan daftar resep
        loadingPlaceholder.setVisibility(View.VISIBLE);
        hsvPopularRecipes.setVisibility(View.GONE);

        setAllCardPlaceholders();

        ApiService apiService = RetrofitClient.getMealApiService();
        if (apiService == null) {
            // PERBAIKAN: Sembunyikan placeholder jika API service tidak tersedia
            loadingPlaceholder.setVisibility(View.GONE);
            return;
        }

        final int totalRequests = 6;
        AtomicInteger requestsCompleted = new AtomicInteger(0);

        for (int i = 0; i < totalRequests; i++) {
            final int position = i + 1;
            Call<MealResponse> call = apiService.getRandomMeal();

            Callback<MealResponse> callback = new Callback<MealResponse>() {
                @Override
                public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                    if (isAdded() && response.isSuccessful() && response.body() != null) {
                        List<MealResponse.Meal> meals = response.body().getMeals();
                        if (meals != null && !meals.isEmpty()) {
                            MealResponse.Meal meal = meals.get(0);
                            if (meal != null) {
                                if(popularRecipesList.size() < totalRequests) {
                                    popularRecipesList.add(meal);
                                }
                                updateRecipeCards(meal, position);
                            } else {
                                handleRecipeLoadError(position);
                            }
                        } else {
                            handleRecipeLoadError(position);
                        }
                    } else {
                        handleRecipeLoadError(position);
                    }
                    checkIfAllRequestsDone();
                }

                @Override
                public void onFailure(Call<MealResponse> call, Throwable t) {
                    if (isAdded()) {
                        handleRecipeLoadError(position);
                    }
                    checkIfAllRequestsDone();
                }

                private void checkIfAllRequestsDone() {
                    if (requestsCompleted.incrementAndGet() == totalRequests) {
                        // PERBAIKAN: Jalankan di UI thread untuk menyembunyikan placeholder dan menampilkan resep
                        new Handler(Looper.getMainLooper()).post(() -> {
                            loadingPlaceholder.setVisibility(View.GONE);
                            hsvPopularRecipes.setVisibility(View.VISIBLE);
                        });
                    }
                }
            };

            call.enqueue(callback);
        }
    }

    private void setAllCardPlaceholders() {
        if (tvRecipe1Title != null) tvRecipe1Title.setText("Memuat...");
        if (tvRecipe2Title != null) tvRecipe2Title.setText("Memuat...");
        if (tvRecipe3Title != null) tvRecipe3Title.setText("Memuat...");
        if (tvRecipe4Title != null) tvRecipe4Title.setText("Memuat...");
        if (tvRecipe5Title != null) tvRecipe5Title.setText("Memuat...");
        if (tvRecipe6Title != null) tvRecipe6Title.setText("Memuat...");
    }

    private void handleRecipeLoadError(int position) {
        TextView titleView = getRecipeTitleView(position);
        if (titleView != null) {
            titleView.setText("Gagal Memuat");
        }
    }

    private TextView getRecipeTitleView(int position) {
        switch (position) {
            case 1: return tvRecipe1Title;
            case 2: return tvRecipe2Title;
            case 3: return tvRecipe3Title;
            case 4: return tvRecipe4Title;
            case 5: return tvRecipe5Title;
            case 6: return tvRecipe6Title;
            default: return null;
        }
    }

    private ImageView getRecipeImageView(int position) {
        switch (position) {
            case 1: return ivRecipe1;
            case 2: return ivRecipe2;
            case 3: return ivRecipe3;
            case 4: return ivRecipe4;
            case 5: return ivRecipe5;
            case 6: return ivRecipe6;
            default: return null;
        }
    }

    private MaterialCardView getRecipeCardView(int position) {
        switch (position) {
            case 1: return cardRecipe1;
            case 2: return cardRecipe2;
            case 3: return cardRecipe3;
            case 4: return cardRecipe4;
            case 5: return cardRecipe5;
            case 6: return cardRecipe6;
            default: return null;
        }
    }

    private void updateRecipeCards(MealResponse.Meal meal, int position) {
        TextView titleView = getRecipeTitleView(position);
        MaterialCardView cardView = getRecipeCardView(position);
        ImageView imageView = getRecipeImageView(position);

        if (titleView != null && meal.getStrMeal() != null) {
            titleView.setText(meal.getStrMeal());
        }

        if (cardView != null) {
            cardView.setOnClickListener(v -> navigateToRecipeDetail(meal));
        }

        if (imageView != null && meal.getStrMealThumb() != null && isAdded()) {
            Glide.with(this)
                    .load(meal.getStrMealThumb())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .centerCrop()
                    .into(imageView);
        }
    }

    private void navigateToRecipeDetail(MealResponse.Meal meal) {
        Intent intent = new Intent(getActivity(), DetailResepActivity.class);
        intent.putExtra("meal_id", meal.getIdMeal());
        intent.putExtra("meal_name", meal.getStrMeal());
        intent.putExtra("meal_thumb", meal.getStrMealThumb());
        intent.putExtra("meal_instructions", meal.getStrInstructions());
        intent.putExtra("meal_category", meal.getStrCategory());
        intent.putExtra("meal_area", meal.getStrArea());
        intent.putExtra("meal_youtube", meal.getStrYoutube());
        startActivity(intent);
    }
}