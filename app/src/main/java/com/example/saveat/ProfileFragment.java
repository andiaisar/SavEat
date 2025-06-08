package com.example.saveat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private CircleImageView profileImage;
    private TextView tvUserName, tvUserEmail;
    private LinearLayout layoutEditProfile, layoutRiwayatBahan, layoutLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        profileImage = view.findViewById(R.id.profileImage);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        layoutEditProfile = view.findViewById(R.id.layoutEditProfile);
        layoutRiwayatBahan = view.findViewById(R.id.layoutRiwayatBahan);
        layoutLogout = view.findViewById(R.id.layoutLogout);

        layoutEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        layoutRiwayatBahan.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RiwayatBahanActivity.class);
            startActivity(intent);
        });

        layoutLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logout berhasil", Toast.LENGTH_SHORT).show();
            // Tambahkan logika logout di sini jika perlu
            getActivity().finish();
        });

        updateProfileData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProfileData();
    }

    private void updateProfileData() {
        // Ambil data dari SharedPreferences atau database
        // Contoh, menggunakan data default
        tvUserName.setText("Franz Herman");
        tvUserEmail.setText("rookie25@gmail.com");
    }
}
