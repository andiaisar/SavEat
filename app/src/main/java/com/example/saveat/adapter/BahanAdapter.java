// File: app/src/main/java/com/example/saveat/adapter/BahanAdapter.java
package com.example.saveat.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.saveat.R;
import com.example.saveat.model.BahanHariIni;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BahanAdapter extends RecyclerView.Adapter<BahanAdapter.BahanViewHolder> {

    public interface OnBahanClickListener {
        void onBahanClick(int position);
        void onBahanLongClick(int position);
    }

    private final List<BahanHariIni> bahanList;
    private final OnBahanClickListener listener;

    public BahanAdapter(List<BahanHariIni> bahanList, OnBahanClickListener listener) {
        this.bahanList = bahanList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BahanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bahan, parent, false);
        return new BahanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BahanViewHolder holder, int position) {
        BahanHariIni bahan = bahanList.get(position);
        holder.bind(bahan);

        // Menetapkan listener ke itemView
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBahanClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onBahanLongClick(holder.getAdapterPosition());
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return bahanList.size();
    }

    static class BahanViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNama, tvJumlah, tvKadaluarsa;
        private final ImageView ivBahan;

        public BahanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaBahan);
            tvJumlah = itemView.findViewById(R.id.tvJumlahBahan);
            tvKadaluarsa = itemView.findViewById(R.id.tvKadaluarsa);
            ivBahan = itemView.findViewById(R.id.ivBahan);
        }

        public void bind(BahanHariIni bahan) {
            tvNama.setText(bahan.getNama());
            tvJumlah.setText(String.format(Locale.getDefault(), "%d %s", bahan.getJumlah(), bahan.getSatuan()));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvKadaluarsa.setText(String.format("Kadaluarsa: %s", sdf.format(bahan.getKadaluarsa())));

            if (bahan.getImagePath() != null && !bahan.getImagePath().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(Uri.parse(bahan.getImagePath()))
                        .placeholder(R.drawable.ic_placeholder) // Placeholder yang bagus
                        .error(R.drawable.ic_error) // Gambar jika terjadi error
                        .into(ivBahan);
            } else {
                // Set gambar default jika tidak ada path gambar
                ivBahan.setImageResource(R.drawable.ic_placeholder);
            }
        }
    }
}