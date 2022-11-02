package com.capstone.waterdrinking.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.waterdrinking.R;

public class SplashActivity extends AppCompatActivity {

    private TextView startTextView;
    private ImageView startWater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startTextView = (TextView) findViewById(R.id.startTextView);
        startWater = (ImageView) findViewById(R.id.startWater);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        startTextView.startAnimation(animation);
        startWater.startAnimation(animation);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000); // 2초 뒤 메인화면 이동
    }
}