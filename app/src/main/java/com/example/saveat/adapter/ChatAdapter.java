package com.example.saveat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saveat.model.ChatMessage;
import com.example.saveat.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_AI = 2;

    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessages.get(position).isUser() ? VIEW_TYPE_USER : VIEW_TYPE_AI;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_ai, parent, false);
            return new AiMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else if (holder instanceof AiMessageViewHolder) {
            ((AiMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void addMessage(ChatMessage message) {
        chatMessages.add(message);
        notifyItemInserted(chatMessages.size() - 1);
    }

    public void removeLastMessage() {
        if (!chatMessages.isEmpty()) {
            int lastIndex = chatMessages.size() - 1;
            chatMessages.remove(lastIndex);
            notifyItemRemoved(lastIndex);
        }
    }

    // ViewHolder untuk pesan user
    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage, tvTime;

        UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tv_time);
        }

        void bind(ChatMessage message) {
            if (tvMessage != null) {
                tvMessage.setText(message.getMessage());
            }
            if (tvTime != null) {
                tvTime.setText(message.getTimestamp());
            }
        }
    }

    // ViewHolder untuk pesan AI
    static class AiMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage, tvTime;

        AiMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tv_time);
        }

        void bind(ChatMessage message) {
            if (tvMessage != null) {
                tvMessage.setText(message.getMessage());
            }
            if (tvTime != null) {
                tvTime.setText(message.getTimestamp());
            }
        }
    }
}