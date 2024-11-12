package com.example.planetze35;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

public class EmailUtils {

    /**
     * Send a verification email to the user
     * @param context the context
     * @param user the user
     */
    public static void sendVerificationEmail(Context context, FirebaseUser user) {
        if (user == null || context == null) {
            return;
        }
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Verification email sent", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(context, "Failed to send verification email, please try again", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }
}
