package com.example.saveat.APIService;

import com.example.saveat.model.GeminiRequest;
import com.example.saveat.model.GeminiResponse;
import com.example.saveat.model.MealResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    // API untuk meal (existing)
    @GET("random.php")
    Call<MealResponse> getRandomMeal();

    // API untuk Gemini AI
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-1.5-flash-latest:generateContent")
    Call<GeminiResponse> sendGeminiChat(@Body GeminiRequest request, @Query("key") String apiKey);
}