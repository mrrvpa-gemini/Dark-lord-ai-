package com.darklord.ai;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView logo = findViewById(R.id.iv_logo);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvSubtitle = findViewById(R.id.tv_subtitle);
        TextView tvCreator = findViewById(R.id.tv_creator);
        TextView tvVersion = findViewById(R.id.tv_version);
        Button btnEnter = findViewById(R.id.btn_enter);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        logo.startAnimation(fadeIn);
        tvTitle.startAnimation(fadeIn);
        tvSubtitle.startAnimation(slideUp);
        tvCreator.startAnimation(slideUp);
        tvVersion.startAnimation(slideUp);
        btnEnter.startAnimation(pulse);

        btnEnter.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }
}
