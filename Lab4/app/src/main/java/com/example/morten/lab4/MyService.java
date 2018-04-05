package com.example.morten.lab4;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyService extends Service {
    String savedUsername;

    DatabaseReference mDatabase;

    NotificationManager nManager = null;
    NotificationCompat.Builder nBuilder = null;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "MessageApp";

    public MyService() {}

    /**
     * onCreate automatic function that runs when service is created
     * **/
    @Override
    public void onCreate() {
        mDatabase = FirebaseDatabase.getInstance().getReference("messages");
        addListenerOnDatabase();

        getUserPreference();

        // Variables for notification
        nBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // sets notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "MessageApp", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.argb(100,255, 105, 180));
            notificationChannel.setVibrationPattern(new long[]{0, 100, 20, 100, 20, 100, 20, 100});
            notificationChannel.enableVibration(true);
            nManager.createNotificationChannel(notificationChannel);
        }
    }

    /**
     * onStartCommand default function
     * **/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * onDestroy default function that runs when service stops
     * **/
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * onBind default function that runs when service is binded
     * **/
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    public void addListenerOnDatabase() {
        // Read from the database
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                HashMap<String, String> m = (HashMap<String, String>) map.get("message");

                String msgText = m.get("message");
                String author = m.get("author");

                if (validate(msgText) && validate(author) && validate(savedUsername) && !isForeground("com.example.morten.lab4")) {
                    if (!savedUsername.equals(author)) {
                        notifyUser(msgText, author);
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Database", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public boolean validate(String string) {
        boolean valid;
        switch (string) {
            case "": valid = false;
                break;
            default: valid = true;
            break;
        }
        return valid;
    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }

    public void notifyUser(String msg, String author) {
        // Create and send a notification to user
        nBuilder.setSmallIcon(R.drawable.ic_launcher_background);
        nBuilder.setContentTitle("MessageApp");
        nBuilder.setContentText("New message from " + author + " - " + msg);
        nBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        nBuilder.setAutoCancel(true);
        nBuilder.setContentIntent(PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));


        nManager.notify(NOTIFICATION_ID, nBuilder.build());
    }

    private void getUserPreference() {
        // Get shared prefs
        SharedPreferences sharedPref = getSharedPreferences("FileName",MODE_PRIVATE);

        // Get values from shared prefs
        savedUsername = sharedPref.getString("username", "");
    }


}
