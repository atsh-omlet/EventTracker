package com.cs360.eventtrackeratsushi.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

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
            Preference logoutPreference = findPreference("logout");
            if (logoutPreference != null) {
                logoutPreference.setOnPreferenceClickListener(this);
            }

        }


        @Override
        public boolean onPreferenceClick(Preference preference) {
            // handle log out button
            if (preference.getKey().equals("logout")) {
                // clear login credentials
                loginViewModel.logout();

                // Clear the events LiveData
                DashboardViewModel dashboardViewModel =
                        new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
                dashboardViewModel.clearEvents();

                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
                return true;
            }
            return false;
        }



    }
}