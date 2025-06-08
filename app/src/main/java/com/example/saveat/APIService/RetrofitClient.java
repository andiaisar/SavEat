package com.example.saveat.APIService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String MEAL_BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/";

    private static Retrofit mealRetrofit = null;
    private static Retrofit geminiRetrofit = null;

    // Existing meal API client
    public static ApiService getMealApiService() {
        if (mealRetrofit == null) {
            mealRetrofit = new Retrofit.Builder()
                    .baseUrl(MEAL_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mealRetrofit.create(ApiService.class);
    }

    // New Gemini AI API client
    public static ApiService getGeminiApiService() {
        if (geminiRetrofit == null) {
            geminiRetrofit = new Retrofit.Builder()
                    .baseUrl(GEMINI_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return geminiRetrofit.create(ApiService.class);
    }
}