package com.example.saveat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IntroAdapter extends RecyclerView.Adapter<IntroAdapter.IntroViewHolder> {

    private final List<IntroItem> introItems;

    public IntroAdapter(List<IntroItem> introItems) {
        this.introItems = introItems;
    }

    @NonNull
    @Override
    public IntroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_intro, parent, false);
        return new IntroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IntroViewHolder holder, int position) {
        holder.bind(introItems.get(position));
    }

    @Override
    public int getItemCount() {
        return introItems.size();
    }

    static class IntroViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageIntro;
        private TextView textTitle, textDescription, textLevel;

        public IntroViewHolder(@NonNull View itemView) {
            super(itemView);
            imageIntro = itemView.findViewById(R.id.imageIntro);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
            textLevel = itemView.findViewById(R.id.textLevel);
        }

        public void bind(IntroItem introItem) {
            imageIntro.setImageResource(introItem.getImage());
            textTitle.setText(introItem.getTitle());
            textDescription.setText(introItem.getDescription());
            textLevel.setText(introItem.getLevel());
        }
    }
}
