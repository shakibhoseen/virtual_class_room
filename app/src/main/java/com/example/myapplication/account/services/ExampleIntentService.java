package com.example.myapplication.account.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.PowerManager;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.example.myapplication.StartActivity;

import timber.log.Timber;

import static com.example.myapplication.account.services.App.CHANNEL_ID;

public class ExampleIntentService extends IntentService {
     /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
     String name;
     private PowerManager.WakeLock wakeLock;

    public ExampleIntentService() {
        super("ExampleIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "ExampleApp:WakeLock");
        wakeLock.acquire();

        Notification notificationFor = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("IntentService")
                .setContentText("Running..")
                .setSmallIcon(R.drawable.ic_baseline_cloud_upload_24)
                .build();
        startForeground(34, notificationFor);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
           setIntentRedelivery(true);
        String input = intent.getStringExtra("inputExtra");


        Intent notificationIntent = new Intent(this, StartActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_cloud_upload_24);
        int progressmax = 100;
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Submitting")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_baseline_cloud_upload_24)
                .setLargeIcon(largeIcon)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true).setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true)
                .setProgress(progressmax, 0, false)
                ;
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);



        notificationManagerCompat.notify(1, notification.build());

        //  notificationManagerCompat.notify(1, notification.build());

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);

                for(int progress = 0; progress<=progressmax; progress+=1){
                    notification.setProgress(progressmax, progress, false);
                    notificationManagerCompat.notify(1, notification.build());
                    SystemClock.sleep(1000);
                }
                notification.setContentText("upload finished")
                        .setOngoing(false)
                        .setProgress(0, 0, false);
                notificationManagerCompat.notify(1, notification.build());
                stopSelf();
            }
        }).start();

       /* new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <10 ; i++) {
                    SystemClock.sleep(1000);
                }
            }
        }).start();*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }
}
