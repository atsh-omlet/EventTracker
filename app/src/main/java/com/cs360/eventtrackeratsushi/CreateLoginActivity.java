package com.cs360.eventtrackeratsushi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity that creates login details for new users
 */
public class CreateLoginActivity extends AppCompatActivity{

    Button btnCreateAccount;
    EditText etUsername;
    EditText etPassword;
    EditText etConfirmPassword;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createlogin);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        dbHelper = new DatabaseHelper(this);

        btnCreateAccount.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String passwordConfirm = etConfirmPassword.getText().toString().trim();
            if (username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()){
                Toast.makeText(CreateLoginActivity.this, "Please enter both username " +
                        "and password", Toast.LENGTH_SHORT).show();
            }
            else if (dbHelper.checkUsernameExists(username)){
                Toast.makeText(CreateLoginActivity.this, "Username taken, " +
                        "please login.", Toast.LENGTH_SHORT).show();
            }
            else if (password.equals(passwordConfirm)){
                dbHelper.addUser(username, password);
                int userId = dbHelper.getUserId(username);
                getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                        .edit()
                        .putBoolean("isLoggedIn", true)
                        .putString("username", username)
                        .putInt("userId",userId)
                        .apply();
                Intent intent = new Intent(CreateLoginActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(CreateLoginActivity.this, "Passwords do not match. " +
                        "Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
