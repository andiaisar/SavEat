package com.example.saveat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        holder.itemView.setOnClickListener(v -> listener.onBahanClick(position));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onBahanLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return bahanList.size();
    }

    static class BahanViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNama, tvJumlah, tvKadaluarsa;

        public BahanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaBahan);
            tvJumlah = itemView.findViewById(R.id.tvJumlahBahan);
            tvKadaluarsa = itemView.findViewById(R.id.tvKadaluarsa);
        }

        public void bind(BahanHariIni bahan) {
            tvNama.setText(bahan.getNama());
            tvJumlah.setText(String.format("%d %s", bahan.getJumlah(), bahan.getSatuan()));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvKadaluarsa.setText(String.format("Kadaluarsa: %s", sdf.format(bahan.getKadaluarsa())));
        }
    }
}