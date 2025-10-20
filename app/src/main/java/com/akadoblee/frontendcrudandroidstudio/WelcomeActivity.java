package com.akadoblee.frontendcrudandroidstudio;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class WelcomeActivity extends AppCompatActivity {

    private CardView buttonCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        buttonCard = findViewById(R.id.buttonCard);

        new Handler().postDelayed(() -> {
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            buttonCard.startAnimation(fadeIn);
            buttonCard.setVisibility(View.VISIBLE);
        }, 500);

        buttonCard.setOnClickListener(v -> {
            Animation scaleDown = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            buttonCard.startAnimation(scaleDown);

            new Handler().postDelayed(() -> {
                Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }, 200);
        });
    }
}