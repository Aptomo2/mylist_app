package com.example.mylist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {

    ImageView satu, dua;
    Animation one, two;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        one = AnimationUtils.loadAnimation(this,R.anim.one_anim);
        two = AnimationUtils.loadAnimation(this,R.anim.two_anim);

        satu = findViewById(R.id.logo);
        dua = findViewById(R.id.mc);

        satu.setAnimation(one);
        dua.setAnimation(two);

        int splash_sc = 2000;
        new Handler() .postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, splash_sc);
    }
}