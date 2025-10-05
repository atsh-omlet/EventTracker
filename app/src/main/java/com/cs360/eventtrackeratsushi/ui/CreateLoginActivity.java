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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createlogin);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);



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

        });
    }
}
