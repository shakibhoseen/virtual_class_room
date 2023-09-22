package com.example.myapplication.account.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.example.myapplication.StartActivity;

import timber.log.Timber;

import static com.example.myapplication.account.services.App.CHANNEL_ID;

public class ExamJobIntentService extends JobIntentService {
    public static final String TAG = "ExamJobIntentService";

   public static void enqueueWork(Context context, Intent work){
        enqueueWork(context,
                ExamJobIntentService.class,
                124,
                work );
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.tag(TAG).d("onCreate");

    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Timber.tag(TAG).d("onHandleWork: ");

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





    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.tag(TAG).d("onDestroy: ");
    }

    @Override
    public boolean onStopCurrentWork() {
        Timber.tag(TAG).d("onStopCurrentWork: ");

        return super.onStopCurrentWork();
    }
}
