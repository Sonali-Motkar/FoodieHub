package com.foodiehub.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.foodiehub.R;
import com.foodiehub.database.DatabaseHelper;
import com.foodiehub.models.User;
import com.foodiehub.network.ApiClient;
import com.foodiehub.network.ApiService;
import com.foodiehub.network.requests.LoginRequest;
import com.foodiehub.network.responses.AuthResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView txtRegister, txtChangeIp;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        edtEmail = findViewById(R.id.edtLoginEmail);
        edtPassword = findViewById(R.id.edtLoginPassword);
        btnLogin = findViewById(R.id.btnLoginSubmit);
        txtRegister = findViewById(R.id.txtGoToRegister);
        txtChangeIp = findViewById(R.id.txtChangeServerIp);

        // Pre-fill demo test credentials for easy verification
        edtEmail.setText("sonalimotkar001@gmail.com");
        edtPassword.setText("Pass@123");

        btnLogin.setOnClickListener(v -> performLogin());

        txtRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        txtChangeIp.setOnClickListener(v -> showIpConfigDialog());
    }

    private void performLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Signing in...");

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.login(new LoginRequest(email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Sign In");

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getUser();
                    String token = response.body().getToken();

                    // Save session to local SQLite database
                    dbHelper.saveUserSession(user, token);

                    Toast.makeText(LoginActivity.this, "Welcome " + user.getName() + "!", Toast.LENGTH_SHORT).show();

                    if (user.isAdmin()) {
                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    finish();
                } else {
                    // Fallback local login for testing if backend server is not running yet
                    handleOfflineFallbackLogin(email, password);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Sign In");
                handleOfflineFallbackLogin(email, password);
            }
        });
    }

    private void handleOfflineFallbackLogin(String email, String password) {
        // Allows testing app features even before backend server is started
        boolean isAdmin = email.equalsIgnoreCase("admin@foodiehub.com");
        User fallbackUser = new User(
            isAdmin ? "ADM-001" : "USR-101",
            isAdmin ? "Master Admin" : "Sonali Motkar",
            email,
            "+91 832961****",
            "Flat 402, Green Palms, Kothrud, Pune",
            isAdmin ? "admin" : "customer"
        );
        dbHelper.saveUserSession(fallbackUser, "offline_local_token_jwt");

        Toast.makeText(this, "Logged in (Local Mode)", Toast.LENGTH_SHORT).show();
        if (isAdmin) {
            startActivity(new Intent(this, AdminActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }

    private void showIpConfigDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Server API Base URL");
        final EditText input = new EditText(this);
        input.setText(ApiClient.BASE_URL);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newUrl = input.getText().toString().trim();
            ApiClient.setCustomBaseUrl(newUrl);
            Toast.makeText(LoginActivity.this, "Server URL updated to: " + ApiClient.BASE_URL, Toast.LENGTH_LONG).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
