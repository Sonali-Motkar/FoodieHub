package com.foodiehub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.foodiehub.R;
import com.foodiehub.database.DatabaseHelper;
import com.foodiehub.models.User;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            User loggedUser = dbHelper.getLoggedInUser();

            if (loggedUser != null) {
                if (loggedUser.isAdmin()) {
                    startActivity(new Intent(SplashActivity.this, AdminActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 2500); // 2.5 seconds allows comfortable time to capture a clean screenshot of the Splash Screen
    }
}
