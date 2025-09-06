package com.cs360.eventtrackeratsushi;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity handles user login, navigates to dashboard or account creation
 */
public class MainActivity extends AppCompatActivity {

    // UI elements
    Button btnLogin;
    Button btnCreateAccount;
    EditText etUsername;
    EditText etPassword;
    DatabaseHelper dbHelper; // helper object of CRUD operations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check if user is aleady logged in
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        // skip login screen if user is logged in already
        if (isLoggedIn) {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish(); // remove login activity from back stack
            return;
        }

        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        dbHelper = new DatabaseHelper(this);

        // listener for login button
        btnLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter both username and " +
                        "password", Toast.LENGTH_SHORT).show();
            }
            // navigate to DashboardActivity, close login screen
            else if (dbHelper.checkUser(username, password)) {
                dbHelper.addUser(username, password);
                int userId = dbHelper.getUserId(username);
                getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                        .edit()
                        .putBoolean("isLoggedIn", true)
                        .putString("username", username)
                        .putInt("userId",userId)
                        .apply();
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(MainActivity.this, "Invalid username or password.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreateLoginActivity.class);
            startActivity(intent);
        });
    }

}