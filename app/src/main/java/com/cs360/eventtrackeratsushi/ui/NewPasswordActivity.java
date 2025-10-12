package com.cs360.eventtrackeratsushi.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.cs360.eventtrackeratsushi.R;
import com.cs360.eventtrackeratsushi.viewmodel.LoginViewModel;

import java.util.Objects;

public class NewPasswordActivity extends AppCompatActivity {
    private static final String TAG = "NewPasswordActivity";
    private EditText etNewPassword, etConfirmPassword;
    private Button btnConfirm;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etNewPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnConfirm = findViewById(R.id.btnConfirm);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Set New Password");

        loginViewModel.getPasswordCheck().observe(this, success -> {
            if (success != null && success) {
                finish();
            }
        });

        loginViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirm.setOnClickListener(view -> {
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            loginViewModel.updatePassword(newPassword, confirmPassword);
        });
    }
}
