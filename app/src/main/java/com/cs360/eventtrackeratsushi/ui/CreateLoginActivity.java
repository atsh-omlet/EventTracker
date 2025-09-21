package com.cs360.eventtrackeratsushi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.cs360.eventtrackeratsushi.R;
import com.cs360.eventtrackeratsushi.database.DatabaseHelper;
import com.cs360.eventtrackeratsushi.viewmodel.LoginViewModel;

/**
 * Activity that creates login details for new users
 */
public class CreateLoginActivity extends AppCompatActivity{

    private Button btnCreateAccount;
    private EditText etUsername, etPassword, etConfirmPassword;
    private LoginViewModel loginViewModel;
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

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginViewModel.getLoginSuccess().observe(this, success ->{
            Intent intent = new Intent(CreateLoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();

        });

        loginViewModel.getErrorMessage().observe(this, msg ->
            Toast.makeText(CreateLoginActivity.this, msg, Toast.LENGTH_SHORT).show()
        );

        btnCreateAccount.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String passwordConfirm = etConfirmPassword.getText().toString().trim();
            loginViewModel.createUser(username, password, passwordConfirm);
            /*
            if (username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()){
                Toast.makeText(CreateLoginActivity.this, "Please enter both username " +
                        "and password", Toast.LENGTH_SHORT).show();
            }
            else if (dbHelper.checkUsernameExists(username)){
                Toast.makeText(CreateLoginActivity.this, "Username taken, " +
                        "please login.", Toast.LENGTH_SHORT).show();
            }
            else if (password.equals(passwordConfirm)){
                dbHelper.createUser(username, password);
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

             */
        });
    }
}
