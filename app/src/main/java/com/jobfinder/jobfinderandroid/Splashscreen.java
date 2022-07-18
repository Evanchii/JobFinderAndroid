package com.jobfinder.jobfinderandroid;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.jobfinder.jobfinderandroid.databinding.ActivitySplashscreenBinding;

public class Splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_splashscreen);

        Handler handler = new Handler();
//        Intent intent = (mAuth.getCurrentUser() != null) ? new Intent(Splashscreen.this, Dashboard.class) : new Intent(Splashscreen.this, Login.class);
        Intent intent = new Intent(Splashscreen.this, ApplicantSignUp.class);
        handler.postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 2500);
    }
}