package com.mining.sms.util;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.widget.RemoteViews;

import com.mining.sms.MainActivity;
import com.mining.sms.R;


public class Notification {

    // 发送简单的通知消息（包括消息标题和消息内容）
    public static void sendSimpleNotify(Service context) {
        String CHANNEL_ONE_ID = "1000085";
        String CHANNEL_ONE_NAME = "100000";
        NotificationChannel notificationChannel;
        //进行8.0的判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setSound(null, null);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        android.app.Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new android.app.Notification.Builder(context, CHANNEL_ONE_ID).setChannelId(CHANNEL_ONE_ID)
                    .setTicker("Nature")
                    .setCustomContentView(remoteViews)
                    .setSmallIcon(R.drawable.t)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true)
                    .build();
        }
        if (notification != null) {
            notification.flags |= android.app.Notification.FLAG_NO_CLEAR;
        }
        context.startForeground(89455, notification);
    }

    public static void sendSimpleNotify(Activity context, String title, String text) {
        String CHANNEL_ONE_ID = "1000322";
        String CHANNEL_ONE_NAME = "111000";
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel;
        //进行8.0的判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setSound(null, null);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        android.app.Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new android.app.Notification.Builder(context, CHANNEL_ONE_ID).setChannelId(CHANNEL_ONE_ID)
                    .setTicker("Nature")
                    .setSmallIcon(R.drawable.dcfbddbbedb)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        if (manager == null) {
            return;
        }
        manager.notify(25500, notification);
    }


    public static void sendSimpleNotify(Activity context, String title) {
        String CHANNEL_ONE_ID = "1000322";
        String CHANNEL_ONE_NAME = "111000";
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel;
        //进行8.0的判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setSound(null, null);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        android.app.Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new android.app.Notification.Builder(context, CHANNEL_ONE_ID).setChannelId(CHANNEL_ONE_ID)
                    .setTicker("Nature")
                    .setSmallIcon(R.drawable.dcfbddbbedb)
                    .setContentTitle(title)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        if (manager == null) {
            return;
        }
        manager.notify(2500, notification);
    }

    public static void sendSimpleNotify(Service context, String title, String text) {
        String CHANNEL_ONE_ID = "1000322";
        String CHANNEL_ONE_NAME = "111000";
        NotificationChannel notificationChannel;
        //进行8.0的判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setSound(null, null);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        android.app.Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new android.app.Notification.Builder(context, CHANNEL_ONE_ID).setChannelId(CHANNEL_ONE_ID)
                    .setTicker("Nature")
                    .setCustomContentView(remoteViews)
                    .setContentTitle(title)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.dcfbddbbedb))   //设置大图标
                    .setContentText(text)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        if (notification == null) {
            return;
        }
        notification.flags |= android.app.Notification.FLAG_NO_CLEAR;
        context.startForeground(8955, notification);
    }


}
