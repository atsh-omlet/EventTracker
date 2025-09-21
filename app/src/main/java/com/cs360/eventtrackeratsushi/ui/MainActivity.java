package com.cs360.eventtrackeratsushi.ui;


import android.content.Intent;
import android.content.SharedPreferences;
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
 * MainActivity handles user login, navigates to dashboard or account creation
 */
public class MainActivity extends AppCompatActivity {

    // UI elements
    private Button btnLogin, btnCreateAccount;
    private EditText etUsername, etPassword;
    private LoginViewModel loginViewModel;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // skip login screen if user is logged in already

        loginViewModel.getLoginStatus().observe(this, status ->{
            if (Boolean.TRUE.equals(status)){
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }

        });

        loginViewModel.getLoginSuccess().observe(this, success ->{
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();

        });


        loginViewModel.getErrorMessage().observe(this, msg ->
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show()
        );

        btnLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            loginViewModel.login(username,password);
        });

       btnCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreateLoginActivity.class);
            startActivity(intent);
        });
    }

}