package com.cs360.eventtrackeratsushi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * A Worker class that handles sending SMS in the background
 */
public class SmsWorker extends Worker {

    /**
     *  Constructor for SMS worker
     * @param context application context
     * @param params parameters for config
     */
    public SmsWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    /**
     * Method responsible for sending SMS
     * @return Result indicating success/failure of SMS operation
     */
    @NonNull
    @Override
    public Result doWork() {
        // retrieve phone number & message
        String phoneNumber = getInputData().getString("phone");
        String message = getInputData().getString("message");

        // Check for required data
        if (phoneNumber == null || message == null) return Result.failure(); // failure if missing data

        // Check for SMS permission
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            try { // send message through SMS manager
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                return Result.success(); // success if message sent
            } catch (Exception e) {
                return Result.failure(); // failure if error
            }
        } else {
            return Result.failure(); // failure if SMS permissions not granted
        }
    }
}
