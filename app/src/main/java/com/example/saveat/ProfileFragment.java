package com.example.saveat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.saveat.database.DatabaseHelper;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private CircleImageView profileImage;
    private TextView tvUserName, tvUserEmail;
    private LinearLayout layoutEditProfile, layoutRiwayatBahan;
    private View layoutLogout; // PERBAIKAN: Tipe diubah dari LinearLayout menjadi View
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        dbHelper = new DatabaseHelper(getContext());
        initViews(view);
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        profileImage = view.findViewById(R.id.profileImage);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        layoutEditProfile = view.findViewById(R.id.layoutEditProfile);
        layoutRiwayatBahan = view.findViewById(R.id.layoutRiwayatBahan);
        layoutLogout = view.findViewById(R.id.layoutLogout); // Baris ini sekarang aman
    }

    private void setupListeners() {
        layoutEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), EditProfileActivity.class));
        });

        layoutRiwayatBahan.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RiwayatBahanActivity.class));
        });

        layoutLogout.setOnClickListener(v -> {
            if (getActivity() == null) return;
            // Hapus data dari SharedPreferences
            SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();

            Toast.makeText(getContext(), "Logout berhasil", Toast.LENGTH_SHORT).show();

            // Arahkan ke halaman login
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });
    }

    // Muat data setiap kali fragmen ini aktif
    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
    }

    private void loadProfileData() {
        if (getActivity() == null) return;

        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);

        if (userId != -1) {
            Cursor cursor = dbHelper.getUserDetails(userId);
            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("profile_image_path"));

                tvUserName.setText(name);
                tvUserEmail.setText(email);

                if (imagePath != null && !imagePath.isEmpty()) {
                    Glide.with(this)
                            .load(Uri.parse(imagePath))
                            .placeholder(R.drawable.usercircle)
                            .error(R.drawable.usercircle)
                            .into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.usercircle);
                }
                cursor.close();
            }
        }
    }
}