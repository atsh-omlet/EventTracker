package com.atsushi.event_tracker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.atsushi.event_tracker.R;
import com.atsushi.event_tracker.viewmodel.SettingsViewModel;
import java.util.Objects;

/**
 * Activity for confirming password
 */
public class ConfirmPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ConfirmPasswordActivity";
    private static final String CHANGE_PASSWORD = "change_password";
    private static final String DELETE_ACCOUNT = "delete_account";
    private EditText etPassword;
    private Button btnConfirm;
    private SettingsViewModel settingsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etPassword = findViewById(R.id.etPassword);
        btnConfirm = findViewById(R.id.btnConfirm);

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);


        // Set the title based on the action type
        String action_type = getIntent().getStringExtra("action_type");
        assert action_type != null;
        if (action_type.equals(CHANGE_PASSWORD)) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Confirm Password");
        } else if (action_type.equals(DELETE_ACCOUNT)) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Delete Account");
        }

        /**
         * Handle password check result
         * if action_type is CHANGE_PASSWORD, check password
         * if action_type is DELETE_ACCOUNT, delete account
         */
        settingsViewModel.getPasswordCheck().observe(this, success -> {
            switch (action_type) {
                case CHANGE_PASSWORD:
                    if (success != null && success) {
                        Intent intent = new Intent(ConfirmPasswordActivity.this, NewPasswordActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case DELETE_ACCOUNT:
                    if (success != null && success) {
                        Intent intent = new Intent(ConfirmPasswordActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }


            }
        });
        settingsViewModel.getMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Handle button click
         * if action_type is CHANGE_PASSWORD, check password
         * if action_type is DELETE_ACCOUNT, delete account
         */
        btnConfirm.setOnClickListener(view -> {
            String password = etPassword.getText().toString().trim();
            switch (action_type) {
                case CHANGE_PASSWORD:
                    settingsViewModel.checkPassword(password);
                    break;
                case DELETE_ACCOUNT:
                    settingsViewModel.deleteAccount(password);
                    break;
            }


        });

    }
}
