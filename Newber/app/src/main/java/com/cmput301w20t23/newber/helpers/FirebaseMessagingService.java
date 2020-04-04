package com.cmput301w20t23.newber.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.database.DatabaseAdapter;
import com.cmput301w20t23.newber.views.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Firebase Messaging Service class that handles Firebase push notifications, and creates a Notification on the device
 *
 * @author Ayushi Patel
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    /**
     * Handler when a message is received from Firebase
     * @param remoteMessage Message received
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //Set the title of the notification to Ride Offer
        String title = "Ride Offer";
        String message = remoteMessage.getData().get("message");

        //Create a new Pending Intent
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        String channelId = getString(R.string.default_notification_channel_id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId,
                    "Ride Updates",
                    NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Provides ride offer updates");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        int notificationId = (int) System.currentTimeMillis();

        notificationManager.notify(notificationId, builder.build());
    }

    /**
     * When a new token is received, set the current user token in Firestore to it
     * @param token
     */
    @Override
    public void onNewToken(String token) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseAdapter.getInstance().setUserToken(uid, token);
    }
}
