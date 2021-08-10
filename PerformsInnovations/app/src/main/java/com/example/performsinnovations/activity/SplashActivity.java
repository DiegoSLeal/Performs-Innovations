package com.example.performsinnovations.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.performsinnovations.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                abrirAnuncios();
            }
        }, 3000);

    }
    private void abrirAnuncios(){
        Intent i = new Intent(SplashActivity.this, AnunciosActivity.class);
        startActivity(i);
        finish();
    }
}