package com.example.saveat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.saveat.R;
import com.example.saveat.model.Meal;

import java.util.ArrayList;
import java.util.List;

public class ResepAdapter extends RecyclerView.Adapter<ResepAdapter.ResepViewHolder> {

    public interface OnResepClickListener {
        void onResepClick(Meal meal);
    }

    private List<Meal> resepList;
    private final OnResepClickListener listener;

    public ResepAdapter(List<Meal> resepList, OnResepClickListener listener) {
        this.resepList = resepList != null ? resepList : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resep, parent, false);
        return new ResepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResepViewHolder holder, int position) {
        Meal resep = resepList.get(position);
        holder.bind(resep, listener);
    }

    @Override
    public int getItemCount() {
        return resepList != null ? resepList.size() : 0;
    }

    public void updateList(List<Meal> newList) {
        this.resepList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ResepViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivGambarResep;
        private final TextView tvNamaResep;
        private final TextView tvDaerahResep;

        public ResepViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGambarResep = itemView.findViewById(R.id.ivGambarResep);
            tvNamaResep = itemView.findViewById(R.id.tvNamaResep);
            tvDaerahResep = itemView.findViewById(R.id.tvDaerahResep);
        }

        public void bind(Meal resep, OnResepClickListener listener) {
            if (resep != null) {
                tvNamaResep.setText(resep.getStrMeal() != null ? resep.getStrMeal() : "");
                tvDaerahResep.setText(resep.getStrArea() != null ? resep.getStrArea() : "");

                if (resep.getStrMealThumb() != null && !resep.getStrMealThumb().isEmpty()) {
                    try {
                        Glide.with(itemView.getContext())
                            .load(resep.getStrMealThumb())
                            // Remove placeholder and error resources that don't exist
                            .into(ivGambarResep);
                    } catch (Exception e) {
                        // Handle any Glide loading exceptions
                        ivGambarResep.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                } else {
                    // Set a default Android system image if meal thumbnail is null or empty
                    ivGambarResep.setImageResource(android.R.drawable.ic_menu_gallery);
                }

                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onResepClick(resep);
                    }
                });
            }
        }
    }
}
