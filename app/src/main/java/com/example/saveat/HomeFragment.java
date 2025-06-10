package com.example.saveat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.saveat.APIService.ApiService;
import com.example.saveat.APIService.RetrofitClient;
import com.example.saveat.database.DatabaseHelper;
import com.example.saveat.model.BahanHariIni;
import com.example.saveat.model.MealResponse;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private DatabaseHelper dbHelper;
    private ImageView ivProfileHome;
    private TextView tvNamaUser;
    private RelativeLayout cardNotification;
    private TextView tvNotificationCount;
    private MaterialCardView cardRingkasanBahan;
    private LinearLayout tvLihatDetail;
    private MaterialCardView loadingPlaceholder;
    private LinearLayout hsvPopularRecipes;
    private MaterialCardView cardRecipe1, cardRecipe2, cardRecipe3, cardRecipe4, cardRecipe5, cardRecipe6;
    private TextView tvRecipe1Title, tvRecipe2Title, tvRecipe3Title, tvRecipe4Title, tvRecipe5Title, tvRecipe6Title;
    private ImageView ivRecipe1, ivRecipe2, ivRecipe3, ivRecipe4, ivRecipe5, ivRecipe6;
    private LinearLayout noInternetLayout;
    private Button btnRefresh;

    // Flag untuk melacak jika ada kegagalan pada pemanggilan API
    private final AtomicBoolean isAnyApiCallFailed = new AtomicBoolean(false);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);
        dbHelper = new DatabaseHelper(getActivity());
        initViews(view);
        setupListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileImage();
        checkExpiryWarnings();
        loadPopularRecipes();
    }

    private void initViews(View view) {
        ivProfileHome = view.findViewById(R.id.ivProfileHome);
        tvNamaUser = view.findViewById(R.id.tvNamaUser);
        cardNotification = view.findViewById(R.id.cardNotification);
        tvNotificationCount = view.findViewById(R.id.tvNotificationCount);
        cardRingkasanBahan = view.findViewById(R.id.cardRingkasanBahan);
        tvLihatDetail = view.findViewById(R.id.tvLihatDetail);
        loadingPlaceholder = view.findViewById(R.id.loadingPlaceholder);
        hsvPopularRecipes = view.findViewById(R.id.hsvPopularRecipes);
        noInternetLayout = view.findViewById(R.id.no_internet_layout);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        initRecipeCards(view);
    }

    private void setupListeners() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userName = prefs.getString("user_name", "User");
        tvNamaUser.setText("Hello, " + userName + "!");

        View.OnClickListener ringkasanListener = v -> {
            Intent intent = new Intent(getActivity(), RingkasanBahanActivity.class);
            startActivity(intent);
        };
        cardRingkasanBahan.setOnClickListener(ringkasanListener);
        tvLihatDetail.setOnClickListener(ringkasanListener);
        cardNotification.setOnClickListener(ringkasanListener);

        btnRefresh.setOnClickListener(v -> {
            Log.d(TAG, "Tombol Refresh diklik.");
            loadPopularRecipes();
        });
    }

    private void loadPopularRecipes() {
        Log.d(TAG, "Memulai proses loadPopularRecipes...");

        // Langsung tampilkan progress bar dan sembunyikan yang lain
        loadingPlaceholder.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);
        hsvPopularRecipes.setVisibility(View.GONE);

        // Cek koneksi internet
        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            Log.w(TAG, "Tidak ada koneksi internet terdeteksi.");
            showNoInternetView();
            return;
        }

        // 3. Jika ada koneksi, lanjutkan memanggil API
        Log.d(TAG, "Koneksi internet tersedia, melanjutkan ke panggilan API.");
        setAllCardPlaceholders();
        executeRecipeApiCall();
    }

    //Menjalankan pemanggilan ke TheMealDB API.
    private void executeRecipeApiCall() {
        // Reset status kegagalan setiap kali mencoba lagi
        isAnyApiCallFailed.set(false);

        ApiService apiService = RetrofitClient.getMealApiService();
        if (apiService == null) {
            Log.e(TAG, "ApiService tidak tersedia.");
            showNoInternetView();
            return;
        }

        final int totalRequests = 6;
        AtomicInteger requestsCompleted = new AtomicInteger(0);

        for (int i = 0; i < totalRequests; i++) {
            final int position = i + 1;
            apiService.getRandomMeal().enqueue(new Callback<MealResponse>() {
                @Override
                public void onResponse(@NonNull Call<MealResponse> call, @NonNull Response<MealResponse> response) {
                    if (!isAdded()) return;

                    if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null && !response.body().getMeals().isEmpty()) {
                        updateRecipeCards(response.body().getMeals().get(0), position);
                    } else {
                        Log.e(TAG, "Response tidak berhasil atau body kosong untuk posisi: " + position);
                        isAnyApiCallFailed.set(true);
                        handleRecipeLoadError(position);
                    }
                    checkIfAllRequestsDone(requestsCompleted.incrementAndGet(), totalRequests);
                }

                @Override
                public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    Log.e(TAG, "Gagal memuat resep untuk posisi " + position, t);
                    isAnyApiCallFailed.set(true);
                    handleRecipeLoadError(position);
                    checkIfAllRequestsDone(requestsCompleted.incrementAndGet(), totalRequests);
                }
            });
        }
    }

    private void checkIfAllRequestsDone(int completed, int total) {
        if (completed == total && getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Log.d(TAG, "Semua " + total + " panggilan API selesai.");
                loadingPlaceholder.setVisibility(View.GONE);

                if (isAnyApiCallFailed.get()) {
                    Log.w(TAG, "Setidaknya satu panggilan API gagal, menampilkan 'No Internet' view.");
                    showNoInternetView();
                } else {
                    Log.d(TAG, "Semua panggilan API berhasil, menampilkan kartu resep.");
                    hsvPopularRecipes.setVisibility(View.VISIBLE);
                    noInternetLayout.setVisibility(View.GONE);
                }
            });
        }
    }

    private void showNoInternetView() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                loadingPlaceholder.setVisibility(View.GONE);
                hsvPopularRecipes.setVisibility(View.GONE);
                noInternetLayout.setVisibility(View.VISIBLE);
            });
        }
    }


    private void checkExpiryWarnings() {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);

        if (userId != -1) {
            new Thread(() -> {
                List<BahanHariIni> expiringItems = dbHelper.getBahanMendekatiKadaluarsa(userId);
                final int expiringCount = expiringItems.size();

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (expiringCount > 0) {
                            tvNotificationCount.setText(String.valueOf(expiringCount));
                            tvNotificationCount.setVisibility(View.VISIBLE);
                        } else {
                            tvNotificationCount.setVisibility(View.GONE);
                        }
                    });
                }
            }).start();
        }
    }

    private void loadProfileImage() {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);

        if (userId != -1) {
            new Thread(() -> {
                Cursor cursor = dbHelper.getUserDetails(userId);
                if (cursor != null && cursor.moveToFirst()) {
                    final String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("profile_image_path"));
                    cursor.close();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (imagePath != null && !imagePath.isEmpty() && isAdded()) {
                                Glide.with(HomeFragment.this)
                                        .load(Uri.parse(imagePath))
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .placeholder(R.drawable.profileuser)
                                        .error(R.drawable.profileuser)
                                        .into(ivProfileHome);
                            } else {
                                ivProfileHome.setImageResource(R.drawable.profileuser);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private void initRecipeCards(View view) {
        cardRecipe1 = view.findViewById(R.id.cardRecipe1);
        cardRecipe2 = view.findViewById(R.id.cardRecipe2);
        cardRecipe3 = view.findViewById(R.id.cardRecipe3);
        cardRecipe4 = view.findViewById(R.id.cardRecipe4);
        cardRecipe5 = view.findViewById(R.id.cardRecipe5);
        cardRecipe6 = view.findViewById(R.id.cardRecipe6);

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
}