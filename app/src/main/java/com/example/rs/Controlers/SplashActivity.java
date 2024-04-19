package com.example.rs.Controlers;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.example.rs.R;

public class SplashActivity extends AppCompatActivity {

    Animation topAnim, bottomAnim;
    private ImageView image;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        image = findViewById(R.id.imageView);

        image.setAnimation(topAnim);

        Thread thread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }catch (Exception e) {

                }
            }
        };
        thread.start();
    }
}