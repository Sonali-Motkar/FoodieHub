package com.foodiehub.activities;

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
import com.foodiehub.network.requests.RegisterRequest;
import com.foodiehub.network.responses.AuthResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPhone, edtPassword, edtAddress;
    private Button btnRegister;
    private TextView txtBackToLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        edtName = findViewById(R.id.edtRegName);
        edtEmail = findViewById(R.id.edtRegEmail);
        edtPhone = findViewById(R.id.edtRegPhone);
        edtPassword = findViewById(R.id.edtRegPassword);
        edtAddress = findViewById(R.id.edtRegAddress);
        btnRegister = findViewById(R.id.btnRegSubmit);
        txtBackToLogin = findViewById(R.id.txtBackToLogin);

        // Pre-fill user details for smooth experience and project demo
        edtName.setText("Sonali Motkar");
        edtEmail.setText("sonalimotkar001@gmail.com");
        edtPhone.setText("+91 832961****");
        edtPassword.setText("Pass@123");
        edtAddress.setText("Flat 402, Green Palms, Kothrud, Pune - 411038");

        btnRegister.setOnClickListener(v -> performRegistration());
        txtBackToLogin.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your name, email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Registering...");

        RegisterRequest req = new RegisterRequest(name, email, password, phone, address);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.register(req).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Create Account");

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getUser();
                    String token = response.body().getToken();
                    dbHelper.saveUserSession(user, token);

                    Toast.makeText(RegisterActivity.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finishAffinity();
                } else {
                    fallbackLocalRegistration(name, email, phone, address);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Create Account");
                fallbackLocalRegistration(name, email, phone, address);
            }
        });
    }

    private void fallbackLocalRegistration(String name, String email, String phone, String address) {
        User user = new User("USR-101", name, email, phone, address, "customer");
        dbHelper.saveUserSession(user, "local_mock_token");
        Toast.makeText(this, "Account registered locally!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }
}
