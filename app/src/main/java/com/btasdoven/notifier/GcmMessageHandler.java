package com.btasdoven.notifier;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class GcmMessageHandler extends IntentService {

    String mes;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        mes = extras.getString("title");
        String cardid = extras.getString("id");
//        showToast();
        Log.i("GCM", "Received : (" + messageType + ")  " + mes);

        if (mes.endsWith("is done."))
            createNotificationForDone(mes);
        else
            createNotification(mes, cardid);

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }
/*
    public void showToast(){
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_LONG).show();
            }
        });

    }
*/
public void createNotificationForDone(String title) {
    // build notification
    // the addAction re-use the same intent to keep the example short
    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    Notification n  = new Notification.Builder(this)
            .setContentTitle(title)
//                .setContentText("Subject")
            .setSmallIcon(R.mipmap.icon_light)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_dark))
            .setAutoCancel(true)
            .setSound(alarmSound)
            .setVibrate(new long[]{1000, 1000})
            .setTicker(title)
            .build();


    NotificationManager notificationManager =
            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    notificationManager.notify((int)System.currentTimeMillis(), n);
}

    public void createNotification(String title, String cardid) {
        Intent intent = new Intent(this, NotificationReceiverService.class);
        intent.putExtra("id", cardid);

        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getService(this, (int) System.currentTimeMillis(), intent, 0);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(this)
                .setContentTitle(title)
//                .setContentText("Subject")
                .setSmallIcon(R.mipmap.icon_light)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_dark))
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setVibrate(new long[] { 1000, 1000})
                .setTicker(title)
                .addAction(R.mipmap.done, "Done", pIntent)
                .build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(Integer.parseInt(cardid), n);
    }
}
