package com.example.saveat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout menuHome, menuIngredients, menuRecipe, menuProfile;
    private MaterialCardView circleHome, circleIngredients, circleRecipe, circleProfile;
    private ImageView iconHome, iconIngredients, iconRecipe, iconProfile;
    private StockFragment stockFragment; // For Ingredients
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private ChatActivity chatFragment; // For AI Chat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupNavigationListeners();

        // Set home as default selection
        navigateTo("home");
    }

    private void initViews() {
        // Initialize menu items
        menuHome = findViewById(R.id.menuHome);
        menuIngredients = findViewById(R.id.menuIngredients);
        menuRecipe = findViewById(R.id.menuRecipe);
        menuProfile = findViewById(R.id.menuProfile);

        // Initialize selected circles
        circleHome = findViewById(R.id.circleHome);
        circleIngredients = findViewById(R.id.circleIngredients);
        circleRecipe = findViewById(R.id.circleRecipe);
        circleProfile = findViewById(R.id.circleProfile);

        // Initialize icons
        iconHome = findViewById(R.id.iconHome);
        iconIngredients = findViewById(R.id.iconIngredients);
        iconRecipe = findViewById(R.id.iconRecipe);
        iconProfile = findViewById(R.id.iconProfile);
    }

    private void setupNavigationListeners() {
        menuHome.setOnClickListener(v -> navigateTo("home"));
        menuIngredients.setOnClickListener(v -> navigateTo("ingredients"));
        menuRecipe.setOnClickListener(v -> navigateTo("recipe"));
        menuProfile.setOnClickListener(v -> navigateTo("profile"));
    }

    private void navigateTo(String destination) {
        // Reset all navigation items first
        resetAllNavItems();

        switch (destination) {
            case "home":
                circleHome.setVisibility(View.VISIBLE);
                iconHome.setVisibility(View.GONE);
                loadHomeFragment();
                break;

            case "ingredients":
                circleIngredients.setVisibility(View.VISIBLE);
                iconIngredients.setVisibility(View.GONE);
                loadStockFragment(); // <-- Perubahan di sini
                break;

            case "recipe":
                circleRecipe.setVisibility(View.VISIBLE);
                iconRecipe.setVisibility(View.GONE);
                loadChatFragment();
                break;

            case "profile":
                circleProfile.setVisibility(View.VISIBLE);
                iconProfile.setVisibility(View.GONE);
                loadProfileFragment();
                break;
        }
    }

    private void resetAllNavItems() {
        // Reset visibility for all navigation items - with null checks
        if (circleHome != null) circleHome.setVisibility(View.GONE);
        if (circleIngredients != null) circleIngredients.setVisibility(View.GONE);
        if (circleRecipe != null) circleRecipe.setVisibility(View.GONE);
        if (circleProfile != null) circleProfile.setVisibility(View.GONE);

        if (iconHome != null) iconHome.setVisibility(View.VISIBLE);
        if (iconIngredients != null) iconIngredients.setVisibility(View.VISIBLE);
        if (iconRecipe != null) iconRecipe.setVisibility(View.VISIBLE);
        if (iconProfile != null) iconProfile.setVisibility(View.VISIBLE);
    }

    private void loadHomeFragment() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        loadFragment(homeFragment);
    }

    // <-- Tambahkan metode ini
    private void loadStockFragment() {
        if (stockFragment == null) {
            stockFragment = new StockFragment();
        }
        loadFragment(stockFragment);
    }

    private void loadChatFragment() {
        if (chatFragment == null) {
            chatFragment = new ChatActivity();
        }
        loadFragment(chatFragment);
    }

    private void loadProfileFragment() {
        if (profileFragment == null) {
            profileFragment = new ProfileFragment();
        }
        loadFragment(profileFragment);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}