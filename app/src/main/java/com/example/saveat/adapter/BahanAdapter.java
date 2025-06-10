package com.example.saveat.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.saveat.R;
import com.example.saveat.model.BahanHariIni;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BahanAdapter extends RecyclerView.Adapter<BahanAdapter.BahanViewHolder> {

    public interface OnBahanClickListener {
        void onBahanClick(int position);
        void onBahanLongClick(int position);
    }

    private final List<BahanHariIni> bahanList;
    private final OnBahanClickListener listener;
    private final int layoutId;

    public BahanAdapter(List<BahanHariIni> bahanList, OnBahanClickListener listener, int layoutId) {
        this.bahanList = bahanList;
        this.listener = listener;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public BahanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new BahanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BahanViewHolder holder, int position) {
        BahanHariIni bahan = bahanList.get(position);
        holder.bind(bahan, listener);
    }

    @Override
    public int getItemCount() {
        return bahanList.size();
    }

    static class BahanViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNama, tvJumlah, tvKadaluarsa;
        private final ImageView ivBahan;
        private final MaterialCardView cardBahan;
        private final Context context;

        public BahanViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvNama = itemView.findViewById(R.id.tvNamaBahan);
            tvJumlah = itemView.findViewById(R.id.tvJumlahBahan);
            tvKadaluarsa = itemView.findViewById(R.id.tvKadaluarsa);
            ivBahan = itemView.findViewById(R.id.ivBahan);
            cardBahan = itemView.findViewById(R.id.card_bahan);
        }

        public void bind(BahanHariIni bahan, OnBahanClickListener listener) {
            tvNama.setText(bahan.getNama());
            tvJumlah.setText(String.format(Locale.getDefault(), "%d %s", bahan.getJumlah(), bahan.getSatuan()));

            if (bahan.getImagePath() != null && !bahan.getImagePath().isEmpty()) {
                Glide.with(context)
                        .load(Uri.parse(bahan.getImagePath()))
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_error)
                        .into(ivBahan);
            } else {
                ivBahan.setImageResource(R.drawable.ic_placeholder);
            }

            long daysUntilExpiry = getDaysUntilExpiry(bahan.getKadaluarsa());
            updateExpiryInfo(daysUntilExpiry, bahan.getKadaluarsa());

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onBahanClick(getAdapterPosition());
            });
            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onBahanLongClick(getAdapterPosition());
                    return true;
                }
                return false;
            });
        }

        private long getDaysUntilExpiry(Date expiryDate) {
            if (expiryDate == null) return Long.MAX_VALUE;

            Calendar todayCal = Calendar.getInstance();
            todayCal.set(Calendar.HOUR_OF_DAY, 0);
            todayCal.set(Calendar.MINUTE, 0);
            todayCal.set(Calendar.SECOND, 0);
            todayCal.set(Calendar.MILLISECOND, 0);

            Calendar expiryCal = Calendar.getInstance();
            expiryCal.setTime(expiryDate);
            expiryCal.set(Calendar.HOUR_OF_DAY, 0);
            expiryCal.set(Calendar.MINUTE, 0);
            expiryCal.set(Calendar.SECOND, 0);
            expiryCal.set(Calendar.MILLISECOND, 0);

            long diffInMillis = expiryCal.getTimeInMillis() - todayCal.getTimeInMillis();
            return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        }

        private void updateExpiryInfo(long days, Date expiryDate) {
            // Persiapkan format tanggal
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            String formattedDate = sdf.format(expiryDate);

            // Persiapkan warna
            int defaultTextColor = ContextCompat.getColor(context, R.color.black);
            int lightTextColor = ContextCompat.getColor(context, R.color.white);
            int redColor = ContextCompat.getColor(context, R.color.red);

            // Reset tampilan ke default
            cardBahan.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            tvNama.setTextColor(defaultTextColor);
            tvJumlah.setTextColor(ContextCompat.getColor(context, R.color.gray));
            tvKadaluarsa.setTextColor(redColor);
            cardBahan.setStrokeWidth(0);

            // Buat teks dengan HTML untuk format yang lebih baik (opsional tapi disarankan)
            String warningText;

            if (days < 0) {
                warningText = "<b>Sudah Kadaluarsa</b><br><small>" + formattedDate + "</small>";
                setWarningVisuals(lightTextColor);
            } else if (days == 0) {
                warningText = "<b>Hari Ini!</b><br><small>" + formattedDate + "</small>";
                setWarningVisuals(lightTextColor);
            } else if (days <= 7) {
                // Tampilkan "Dalam X hari" beserta tanggalnya
                warningText = String.format(Locale.getDefault(), "<b>Dalam %d hari</b><br><small>(%s)</small>", days + 1, formattedDate);
                setWarningVisuals(lightTextColor);
            } else {
                // Jika masih lama, hanya tampilkan tanggal
                warningText = formattedDate;
                tvKadaluarsa.setTextColor(ContextCompat.getColor(context, R.color.green));
            }

            // Terapkan teks ke TextView
            tvKadaluarsa.setText(Html.fromHtml(warningText, Html.FROM_HTML_MODE_LEGACY));
        }

        private void setWarningVisuals(int textColor) {
            cardBahan.setBackgroundResource(R.drawable.bg_item_kadaluarsa);
            tvNama.setTextColor(textColor);
            tvJumlah.setTextColor(textColor);
            tvKadaluarsa.setTextColor(textColor);
        }
    }
}