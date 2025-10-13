package com.cs360.eventtrackeratsushi.ui;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.cs360.eventtrackeratsushi.R;
import com.cs360.eventtrackeratsushi.viewmodel.DashboardViewModel;
import com.cs360.eventtrackeratsushi.viewmodel.LoginViewModel;


import java.util.Objects;

/**
 * Activity for manging application settings:
 *  SMS notificaton preference
 *  Set phone number
 *  Logout user
 */
public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private static final String LOGOUT_KEY = "logout";
    private static final String CHANGE_PASSWORD_KEY = "change_password";
    private static final String CLEAR_EVENTS_KEY = "clear_events";
    private static final String DELETE_ACCOUNT_KEY = "delete_account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        // Set up toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Account Settings");
    }


    public static class SettingsFragment extends PreferenceFragmentCompat
    implements Preference.OnPreferenceClickListener{

        private LoginViewModel loginViewModel;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

            // Logout preference
            Preference logoutPreference = findPreference(LOGOUT_KEY);
            if (logoutPreference != null) {
                logoutPreference.setOnPreferenceClickListener(this);
            }

            // Change password preference
            Preference changePasswordPreference = findPreference(CHANGE_PASSWORD_KEY);
            if (changePasswordPreference != null) {
                changePasswordPreference.setOnPreferenceClickListener(this);
            }

            // Clear events preference
            Preference clearEventsPreference = findPreference(CLEAR_EVENTS_KEY);
            if (clearEventsPreference != null) {
                clearEventsPreference.setOnPreferenceClickListener(this);
            }

            // Delete account preference
            Preference deleteAccountPreference = findPreference(DELETE_ACCOUNT_KEY);
            if (deleteAccountPreference != null) {
                deleteAccountPreference.setOnPreferenceClickListener(this);
            }

        }


        @Override
        public boolean onPreferenceClick(Preference preference) {
            DashboardViewModel dashboardViewModel =
                    new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
            // handle log out button
            if (preference.getKey().equals(LOGOUT_KEY)) {
                // clear login credentials
                loginViewModel.logout();

                // Clear the events LiveData
                dashboardViewModel.clearEvents();

                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
                return true;
            }
            if (preference.getKey().equals(CHANGE_PASSWORD_KEY)) {
                Intent intent = new Intent(requireContext(), ConfirmPasswordActivity.class);
                intent.putExtra("action_type", CHANGE_PASSWORD_KEY);
                startActivity(intent);
                return true;
            }
            if (preference.getKey().equals(DELETE_ACCOUNT_KEY)) {
                // Show confirmation dialog
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete your account?\nThis action cannot be undone.")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            // Proceed to ConfirmPasswordActivity if user confirms
                            Intent intent = new Intent(requireContext(), ConfirmPasswordActivity.class);
                            intent.putExtra("action_type", DELETE_ACCOUNT_KEY);
                            startActivity(intent);
                        })
                        .setNegativeButton("Go Back", (dialog, which) -> {
                            // Simply dismiss the dialog
                            dialog.dismiss();
                        })
                        .setCancelable(true)
                        .show();
                return true;
            }

            if (preference.getKey().equals(CLEAR_EVENTS_KEY)) {
                // Show confirmation dialog
                new AlertDialog.Builder(requireContext())
                        .setTitle("Clear All Events")
                        .setMessage("Are you sure you want to clear all events?\nThis action cannot be undone.")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            dashboardViewModel.deleteAllEvents();
                            Intent intent = new Intent(requireContext(), DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Toast.makeText(requireContext(), "All events cleared", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Go Back", (dialog, which) -> {
                            // Simply dismiss the dialog
                            dialog.dismiss();
                        })
                        .setCancelable(true)
                        .show();
                return true;
            }


            return false;
        }



    }
}