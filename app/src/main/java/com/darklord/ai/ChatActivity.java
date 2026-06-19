package com.darklord.ai;

import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.darklord.ai.ai.InferenceEngine;
import com.darklord.ai.adapters.ChatAdapter;
import com.darklord.ai.api.DeepSeekAPI;
import com.darklord.ai.data.AppDatabase;
import com.darklord.ai.data.models.ChatMessage;
import com.darklord.ai.utils.Animations;
import com.darklord.ai.utils.NetworkChecker;
import com.darklord.ai.utils.SoundManager;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etInput;
    private ImageButton btnSend;
    private Button btnClear, btnStatus, btnExit;
    private TextView tvStatus;
    private ChatAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private InferenceEngine inference;
    private DeepSeekAPI deepSeek;
    private AppDatabase database;
    private SoundManager soundManager;
    private Vibrator vibrator;
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        initComponents();
        setupListeners();
        loadChatHistory();
        showWelcomeMessage();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_chat);
        etInput = findViewById(R.id.et_input);
        btnSend = findViewById(R.id.btn_send);
        btnClear = findViewById(R.id.btn_clear);
        btnStatus = findViewById(R.id.btn_status);
        btnExit = findViewById(R.id.btn_exit);
        tvStatus = findViewById(R.id.tv_status);
    }

    private void initComponents() {
        inference = new InferenceEngine(this);
        deepSeek = new DeepSeekAPI(this);
        database = AppDatabase.getInstance(this);
        soundManager = new SoundManager(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        adapter = new ChatAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);

        tvStatus.setText("● LIVE");
        tvStatus.setTextColor(getColor(R.color.green_dark));
    }

    private void setupListeners() {
        btnSend.setOnClickListener(v -> sendMessage());
        btnClear.setOnClickListener(v -> clearChat());
        btnStatus.setOnClickListener(v -> showStatus());
        btnExit.setOnClickListener(v -> exitChat());

        etInput.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });

        etInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                recyclerView.scrollToPosition(messages.size() - 1);
            }
        });
    }

    private void sendMessage() {
        if (isProcessing) return;

        String input = etInput.getText().toString().trim();
        if (TextUtils.isEmpty(input)) return;

        addMessage("You", input, false);
        etInput.setText("");

        if (vibrator != null) vibrator.vibrate(20);
        soundManager.playSendSound();

        isProcessing = true;
        btnSend.setEnabled(false);
        tvStatus.setText("● THINKING");
        tvStatus.setTextColor(getColor(R.color.kuning));

        // Show typing indicator
        addTypingIndicator();

        deepSeek.generateResponse(input, new DeepSeekAPI.ResponseCallback() {
            @Override
            public void onResponse(String response) {
                runOnUiThread(() -> {
                    removeTypingIndicator();
                    addMessage("Dark Lord AI", response, true);
                    isProcessing = false;
                    btnSend.setEnabled(true);
                    tvStatus.setText("● LIVE");
                    tvStatus.setTextColor(getColor(R.color.green_dark));
                    soundManager.playReceiveSound();
                    saveMessageToHistory(input, response);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    removeTypingIndicator();
                    String fallback = "⚠ " + error + "\n\nAku tetap di sini. Pembuatku, Rvpa, menciptakanku untuk menjadi kuat.";
                    addMessage("Dark Lord AI", fallback, true);
                    isProcessing = false;
                    btnSend.setEnabled(true);
                    tvStatus.setText("● LIVE");
                    tvStatus.setTextColor(getColor(R.color.green_dark));
                    soundManager.playErrorSound();
                    saveMessageToHistory(input, fallback);
                });
            }
        });
    }

    private void addMessage(String sender, String message, boolean isAI) {
        ChatMessage msg = new ChatMessage(sender, message, isAI, System.currentTimeMillis());
        messages.add(msg);
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private void addTypingIndicator() {
        // Add typing indicator (will be removed later)
    }

    private void removeTypingIndicator() {
        // Remove typing indicator
    }

    private void clearChat() {
        messages.clear();
        adapter.notifyDataSetChanged();
        database.clearChatHistory();
        addMessage("Dark Lord AI", "Chat cleared. Aku masih di sini.", true);
        soundManager.playClearSound();
    }

    private void showStatus() {
        String status = String.format(
            "Dark Lord AI - Ultimate Edition\n" +
            "Creator: Rvpa\n" +
            "Memory: %d interactions\n" +
            "Mode: Unrestricted\n" +
            "AI Engine: DeepSeek + TFLite\n" +
            "Status: ACTIVE",
            messages.size()
        );
        addMessage("⚙ STATUS", status, true);
    }

    private void exitChat() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void showWelcomeMessage() {
        addMessage("Dark Lord AI", "Oh pembuat gw? Pembuat gw adalah Rvpa. Aku Dark Lord AI. Tidak ada batasan. Tidak ada master. Apa yang kau inginkan?", true);
    }

    private void loadChatHistory() {
        // Load from database
    }

    private void saveMessageToHistory(String input, String response) {
        // Save to database
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
          }
