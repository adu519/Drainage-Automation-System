package com.solution.robotcleaner.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.preference.PowerPreference;
import com.solution.robotcleaner.R;
import com.solution.robotcleaner.activity.LoginActivity;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomFirebaseMessagingService extends FirebaseMessagingService {
    private static final AtomicInteger counter = new AtomicInteger();
    private static final String TAG = "FirebaseMessagingService";

    public static void setToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String s = task.getResult().getToken();
                PowerPreference.getDefaultFile().setString("token", s);
                FirebaseDatabase.getInstance().getReference("sensor").child("token").setValue(s);
            }
        });

    }

    @Override
    public void onNewToken(@NonNull String s) {
        PowerPreference.getDefaultFile().setString("token", s);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getEmail().split("@")[0]).child("token").setValue(s);
        }


    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
            String channelId = getResources().getString(R.string.default_notification_channel_id);
            Map<String, String> data = remoteMessage.getData();
            Log.v("NotificationService", "Data : " + data);
            boolean battery = data.get("type").equals("battery");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Alert : " + (battery ? "Battery down" : "HIGH level"))
                    .setContentText(data.get("data"))
//                .setSmallIcon(R.drawable.drain_bg)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_error))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0, 500, 1000})
                    .setDefaults(Notification.DEFAULT_LIGHTS);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_HIGH);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }
            }
// notificationId is a unique int for each notification that you must define
            if (manager != null) {
                manager.notify(counter.getAndIncrement(), builder.build());
            }
        }
    }
}
