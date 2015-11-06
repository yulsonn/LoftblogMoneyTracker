package ru.loftschool.loftblogmoneytracker.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.ui.activities.MainActivity_;

public class NotificationUtil {

    private static final int NOTIFICATION_ID = 12345;

    public static void UpdateNotifications(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        String vibroNotificationsKey = context.getString(R.string.pref_enable_vibro_key);
        String ledNotificationsKey = context.getString(R.string.pref_enable_led_key);
        String soundNotificationsKey = context.getString(R.string.pref_enable_sound_key);

        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey, Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));
        boolean ledNotifications = prefs.getBoolean(ledNotificationsKey, Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));
        boolean vibroNotifications = prefs.getBoolean(vibroNotificationsKey, Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));
        boolean soundNotifications = prefs.getBoolean(soundNotificationsKey, Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));


        if (displayNotifications) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            //Create Intent to launch this Activity again if the notification is clicked.
            Intent i = new Intent(context, MainActivity_.class);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(intent);

            builder.setSmallIcon(R.mipmap.ic_launcher);

            if (ledNotifications) {
                Log.d("Noteification", "led == " + ledNotifications);
                builder.setLights(Color.CYAN, 300, 1500);
            }
            if (vibroNotifications) {
                Log.d("Noteification", "vibro == " + ledNotifications);
                builder.setVibrate(new long[]{500, 500});
            }
            if (soundNotifications) {
                Log.d("Noteification", "sound == " + ledNotifications);
                builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            }

            // Cancel the notification when clicked
            builder.setAutoCancel(true);

            String title = context.getString(R.string.app_name);
            String contentText = context.getResources().getString(R.string.notification_message);
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            builder.setLargeIcon(largeIcon);
            builder.setContentTitle(title);
            builder.setContentText(contentText);

            Notification notification = builder.build();
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(NOTIFICATION_ID, notification);

        }
    }
}
