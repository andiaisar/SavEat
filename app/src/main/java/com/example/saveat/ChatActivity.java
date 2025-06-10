package com.example.saveat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saveat.APIService.ApiService;
import com.example.saveat.APIService.RetrofitClient;
import com.example.saveat.adapter.ChatAdapter;
import com.example.saveat.model.ChatMessage;
import com.example.saveat.model.GeminiRequest;
import com.example.saveat.model.GeminiResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends Fragment {
    private RecyclerView recyclerChat;
    private EditText etMessage;
    private ImageView btnSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private ApiService geminiApiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat, container, false);

        recyclerChat = view.findViewById(R.id.recycler_chat);
        etMessage = view.findViewById(R.id.et_message);
        btnSend = view.findViewById(R.id.btn_send);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(chatAdapter);

        geminiApiService = RetrofitClient.getGeminiApiService();

        btnSend.setOnClickListener(v -> sendMessage());

        etMessage.setOnEditorActionListener((v1, actionId, event) -> {
            sendMessage();
            return true;
        });

        addWelcomeMessage();

        return view;
    }

    private void addWelcomeMessage() {
        String welcomeText = "Halo! 👋\n\nSaya adalah asisten AI untuk resep makanan. Apa yang ingin Anda masak hari ini?";
        ChatMessage welcomeMessage = new ChatMessage(welcomeText, false, getCurrentTime());
        chatAdapter.addMessage(welcomeMessage);
        scrollToBottom();
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void scrollToBottom() {
        if (chatAdapter.getItemCount() > 0) {
            recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    private void sendMessage() {
        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            Toast.makeText(getContext(), R.string.no_internet_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        String message = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) return;

        ChatMessage userMessage = new ChatMessage(message, true, getCurrentTime());
        chatAdapter.addMessage(userMessage);
        scrollToBottom();
        etMessage.setText("");

        sendToGeminiAI(message);
    }

    private void sendToGeminiAI(String userMessage) {
        showTypingIndicator();

        String systemPrompt = "Anda adalah asisten AI ahli kuliner yang membantu dengan resep makanan, tips memasak, dan pertanyaan seputar kuliner. Berikan jawaban yang informatif, praktis, dan mudah diikuti dalam bahasa Indonesia. Jika pertanyaan di luar topik kuliner, arahkan kembali ke topik makanan dan masakan.";
        String fullPrompt = systemPrompt + "\n\nPertanyaan: " + userMessage;

        GeminiRequest.Part part = new GeminiRequest.Part(fullPrompt);
        GeminiRequest.Content content = new GeminiRequest.Content(Arrays.asList(part));
        GeminiRequest.GenerationConfig config = new GeminiRequest.GenerationConfig(0.7, 1000, 0.95, 40);

        GeminiRequest request = new GeminiRequest(Arrays.asList(content), config);

        Call<GeminiResponse> call = geminiApiService.sendGeminiChat(request, "AIzaSyAV3NFfUmB-3JnMyu-fhXy84JqdwO20mjA");
        call.enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                hideTypingIndicator();
                if (response.isSuccessful() && response.body() != null) {
                    String aiResponse = extractContentFromGeminiResponse(response.body());
                    if (aiResponse != null && !aiResponse.trim().isEmpty()) {
                        ChatMessage aiMessage = new ChatMessage(aiResponse, false, getCurrentTime());
                        chatAdapter.addMessage(aiMessage);
                        scrollToBottom();
                    } else {
                        showErrorMessage("Maaf, saya tidak bisa memproses pertanyaan Anda saat ini.");
                    }
                } else {
                    if (!NetworkUtils.isNetworkAvailable(getContext())) {
                        showErrorMessage("Koneksi internet tidak tersedia.");
                    } else {
                        showErrorMessage("Gagal terhubung ke AI. Kode: " + response.code());
                    }
                }
            }
            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                hideTypingIndicator();
                if (!NetworkUtils.isNetworkAvailable(getContext())) {
                    showErrorMessage("Koneksi internet tidak tersedia. Silakan coba lagi.");
                } else {
                    showErrorMessage("Koneksi gagal. Silakan coba lagi.");
                }
            }
        });
    }

    private String extractContentFromGeminiResponse(GeminiResponse response) {
        try {
            if (response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                GeminiResponse.Candidate candidate = response.getCandidates().get(0);
                if (candidate.getContent() != null && candidate.getContent().getParts() != null
                        && !candidate.getContent().getParts().isEmpty()) {
                    return candidate.getContent().getParts().get(0).getText().trim();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void showTypingIndicator() {
        ChatMessage typingMessage = new ChatMessage("AI sedang mengetik...", false, getCurrentTime());
        chatAdapter.addMessage(typingMessage);
        scrollToBottom();
    }

    private void hideTypingIndicator() {
        if (!chatMessages.isEmpty()) {
            ChatMessage lastMessage = chatMessages.get(chatMessages.size() - 1);
            if ("AI sedang mengetik...".equals(lastMessage.getMessage())) {
                chatMessages.remove(chatMessages.size() - 1);
                chatAdapter.notifyItemRemoved(chatMessages.size());
            }
        }
    }

    private void showErrorMessage(String message) {
        ChatMessage errorMessage = new ChatMessage(
                "❌ " + message + "\n\nSilakan coba lagi dengan pertanyaan lain.",
                false, getCurrentTime());
        chatAdapter.addMessage(errorMessage);
        scrollToBottom();
    }
}