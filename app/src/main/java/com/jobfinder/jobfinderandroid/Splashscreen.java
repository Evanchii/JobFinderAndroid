package com.jobfinder.jobfinderandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.jobfinder.jobfinderandroid.databinding.ActivitySplashscreenBinding;

public class Splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_splashscreen);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

//        Request Permission
        ActivityCompat.requestPermissions(Splashscreen.this,
                new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE},
                1);

        Handler handler = new Handler();

        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", this.MODE_PRIVATE);

        Intent intent;
        String userType = sharedpreferences.getString("userType", "");
        if(mAuth.getCurrentUser() == null) intent = new Intent(Splashscreen.this, ApplicantSignIn.class);
        else if(userType.equals("applicant")) intent = new Intent(Splashscreen.this, ApplicantDashboard.class);
        else intent = new Intent(Splashscreen.this, EmployerDashboard.class);

        handler.postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 2500);
    }
}