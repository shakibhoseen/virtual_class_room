package com.example.myapplication.account.services;

import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.example.myapplication.StartActivity;

import static com.example.myapplication.account.services.App.CHANNEL_ID;

public class ExampleJobService extends JobService {
    public static final String TAG = "ExampleJobService";
    private boolean jobCancelled = false;
     
    @Override
    public boolean onStartJob(JobParameters params) {
        doBackgroundWork(params);
        return true;
    }
    
    private void doBackgroundWork(JobParameters params){
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <30 ; i++) {
                    Log.d(TAG, "run: +i");

                    if (jobCancelled){
                        return;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "run: job finished");
                jobFinished(params, false);
            }
        }).start();*/

        createNotification();
       // createOwnNotification();
        jobFinished(params, false);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob: Job cancelled before completion");
        jobCancelled = true;
        return true;
    }


    private void createNotification(){
        Intent notificationIntent = new Intent(this, StartActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_cloud_upload_24);
        int progressmax = 100;
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                /*.setContentTitle("Submitting")
                .setContentText("")*/
                .setSmallIcon(R.drawable.ic_baseline_cloud_upload_24)
              //  .setLargeIcon(largeIcon)
               // .setContentIntent(pendingIntent)
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


                for(int progress = 0; progress<=progressmax; progress+=1){
                    notification.setProgress(progressmax, progress, false);
                    notificationManagerCompat.notify(1, notification.build());
                    SystemClock.sleep(1000);
                    Log.d(TAG, "job scheduled "+ progress);
                    if (jobCancelled){
                        return;
                    }
                }
                notification.setContentText("upload finished")
                        .setOngoing(false)
                        .setProgress(0, 0, false);
                notificationManagerCompat.notify(1, notification.build());
                stopSelf();
            }
        }).start();
    }



    private void createOwnNotification(){
        Intent notificationIntent = new Intent(this, StartActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        RemoteViews collapsed = new RemoteViews(getPackageName(), R.layout.submit_custom_notification);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_cloud_upload_24);
        int progressmax = 100;
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                /*.setContentTitle("Submitting")
                .setContentText("")*/
                .setSmallIcon(R.drawable.ic_baseline_cloud_upload_24)
                //  .setLargeIcon(largeIcon)
                // .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true).setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true)
                .setCustomContentView(collapsed)

                ;
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);



        notificationManagerCompat.notify(1, notification.build());

        //  notificationManagerCompat.notify(1, notification.build());

        new Thread(new Runnable() {
            @Override
            public void run() {


                for(int progress = 0; progress<=progressmax; progress+=1){
                    collapsed.setProgressBar(R.id.progress_num_id, progressmax, progress, false);
                   // notification.setProgress(progressmax, progress, false);
                    notificationManagerCompat.notify(1, notification.build());
                    SystemClock.sleep(1000);
                    Log.d(TAG, "job scheduled "+ progress);
                    if (jobCancelled){
                        return;
                    }
                }
                notification.setContentText("upload finished")
                        .setOngoing(false)
                        .setProgress(0, 0, false);
                notificationManagerCompat.notify(1, notification.build());
                stopSelf();
            }
        }).start();
    }
}
