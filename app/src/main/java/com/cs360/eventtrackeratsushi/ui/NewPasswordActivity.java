package com.cs360.eventtrackeratsushi.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.cs360.eventtrackeratsushi.R;
import com.cs360.eventtrackeratsushi.viewmodel.SettingsViewModel;

import java.util.Objects;

/**
 * Activity for setting a new password
 */
public class NewPasswordActivity extends AppCompatActivity {
    private static final String TAG = "NewPasswordActivity";
    private EditText etNewPassword, etConfirmPassword;
    private Button btnConfirm;
    private SettingsViewModel settingsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etNewPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnConfirm = findViewById(R.id.btnConfirm);

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Set New Password");

        settingsViewModel.getPasswordCheck().observe(this, success -> {
            if (success != null && success) {
                finish();
            }
        });

        settingsViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirm.setOnClickListener(view -> {
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            settingsViewModel.updatePassword(newPassword, confirmPassword);
        });
    }
}
