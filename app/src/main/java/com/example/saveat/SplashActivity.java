package com.example.saveat;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    private RelativeLayout potContainer;
    private TextView appName;
    private ImageView pot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.d(TAG, "SplashActivity created");

        // Initialize views
        initViews();

        // FORCE is_first_run to be true for testing
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        prefs.edit().putBoolean("is_first_run", true).apply();
        Log.d(TAG, "FORCED is_first_run to true for testing");

        // Start animation sequence
        startSplashAnimation();
    }

    private void initViews() {
        potContainer = findViewById(R.id.potContainer);
        appName = findViewById(R.id.appName);
        pot = findViewById(R.id.pot);
    }

    private void startSplashAnimation() {
        // Phase 1: Show pot in center for 1 second
        new Handler(Looper.getMainLooper()).postDelayed(this::animatePotAndText, 1000);

        // Phase 2: Navigate to next screen after animation completes
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToNextScreen, 4000);
    }

    private void animatePotAndText() {
        // Smooth pot movement to the left
        ObjectAnimator potMoveX = ObjectAnimator.ofFloat(potContainer, "translationX", 0f, -180f);
        potMoveX.setDuration(1800);
        potMoveX.setInterpolator(new DecelerateInterpolator(2f));

        // Smooth text appearance
        appName.setVisibility(View.VISIBLE);
        ObjectAnimator textAlpha = ObjectAnimator.ofFloat(appName, "alpha", 0f, 1f);
        textAlpha.setDuration(1200);
        textAlpha.setStartDelay(800);
        textAlpha.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator textScaleX = ObjectAnimator.ofFloat(appName, "scaleX", 0.3f, 1f);
        textScaleX.setDuration(1200);
        textScaleX.setStartDelay(800);
        textScaleX.setInterpolator(new DecelerateInterpolator(1.5f));

        ObjectAnimator textScaleY = ObjectAnimator.ofFloat(appName, "scaleY", 0.3f, 1f);
        textScaleY.setDuration(1200);
        textScaleY.setStartDelay(800);
        textScaleY.setInterpolator(new DecelerateInterpolator(1.5f));

        // Smooth text slide in from right
        ObjectAnimator textTranslateX = ObjectAnimator.ofFloat(appName, "translationX", 200f, 0f);
        textTranslateX.setDuration(1200);
        textTranslateX.setStartDelay(800);
        textTranslateX.setInterpolator(new DecelerateInterpolator(2f));

        // Position text correctly initially
        appName.setTranslationX(200f);

        // Start animations
        potMoveX.start();

        AnimatorSet textSet = new AnimatorSet();
        textSet.play(textAlpha)
                .with(textScaleX)
                .with(textScaleY)
                .with(textTranslateX);
        textSet.start();
    }

    private void navigateToNextScreen() {
        // Check if this is first run
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("is_first_run", true);

        // Also check if user is logged in
        SharedPreferences userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = userPrefs.getBoolean("is_logged_in", false);

        Intent intent;

        if (isFirstRun) {
            // First time - go to intro
            intent = new Intent(SplashActivity.this, IntroActivity.class);
        } else if (isLoggedIn) {
            // User is logged in - go directly to MainActivity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // Not first time but not logged in - go to sign in
            intent = new Intent(SplashActivity.this, SignInActivity.class);
        }

        // Clear any previous activities in the stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}