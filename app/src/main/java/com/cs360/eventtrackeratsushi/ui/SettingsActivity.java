package com.cs360.eventtrackeratsushi.ui;

import android.Manifest;
import android.content.Context;
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
import com.cs360.eventtrackeratsushi.viewmodel.LoginViewModel;

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
        getSupportActionBar().setTitle("Account Settings");
    }


    public static class SettingsFragment extends PreferenceFragmentCompat
    implements Preference.OnPreferenceClickListener,
    Preference.OnPreferenceChangeListener {

        private SwitchPreferenceCompat smsPreference;
        private EditTextPreference phoneNumberPreference;
        private final int SMS_PERMISSION_CODE = 0; // Request code for SMS permission
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

            // Configure phone number
            phoneNumberPreference = findPreference("phone_number");
            if (phoneNumberPreference != null) {
                phoneNumberPreference.setOnPreferenceChangeListener(this);
                updatePhoneNumberSummary();
            }

            // SMS preference toggle
            smsPreference = findPreference("enable_sms_notifications");
            if (smsPreference != null) {
                smsPreference.setOnPreferenceChangeListener(this);
                SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
                // load sms preference state
                boolean smsEnabled = prefs.getBoolean("enable_sms_notifications", false);
                smsPreference.setChecked(smsEnabled);
            }


        }

        /**
         * Update phone number summary display
         */
        private void updatePhoneNumberSummary() {
            String phoneNumber = phoneNumberPreference.getText();
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                phoneNumberPreference.setSummary("No phone number set");
            } else {
                phoneNumberPreference.setSummary("Set to: " + phoneNumber);
            }
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            // handle log out button
            if (preference.getKey().equals("logout")) {
                // clear login credentials
                loginViewModel.logout();
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
                return true;
            }
            return false;
        }


        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            // handle sms notification toggle change
            if (preference.getKey().equals("enable_sms_notifications")) {
                boolean enableSms = (Boolean) newValue;

                // check permission for sms
                if (enableSms) {
                    if (ContextCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                        if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
                            showPermissionRationale();
                            return false; // wait for permission result
                        }
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                                SMS_PERMISSION_CODE);
                        return false;
                    }
                }
                return true; // allow the change
            }
            else if (preference.getKey().equals("phone_number")) {
                // Save the new phone number
                String newPhoneNumber = (String) newValue;
                phoneNumberPreference.setText(newPhoneNumber);
                updatePhoneNumberSummary();
                return true;
            }
            return false;
        }

        /**
         * show rationale dialog for sms permission
         */
        private void showPermissionRationale() {
            new AlertDialog.Builder(requireContext())
                    .setTitle("SMS Permission Needed")
                    .setMessage("This allows sending event reminders via SMS")
                    .setPositiveButton("Continue", (d, w) ->
                            requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                                    SMS_PERMISSION_CODE))
                    .setNegativeButton("Cancel", null)
                    .show();
        }



        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            // handle sms permission request result
            if (requestCode == SMS_PERMISSION_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    smsPreference.setChecked(true); // permission granted, enable sms
                    SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
                    editor.putBoolean("enable_sms_notifications", true);
                    editor.apply();
                } else { // permission denied, disable sms
                    smsPreference.setChecked(false);
                    SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
                    editor.putBoolean("enable_sms_notifications", false);
                    editor.apply();
                    Toast.makeText(requireContext(),
                            "SMS permission denied. Notifications disabled.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}