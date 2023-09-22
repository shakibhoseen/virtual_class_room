package com.example.myapplication.account.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.example.myapplication.StartActivity;
import com.example.myapplication.account.assignment.AssignmentUpload;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;

import static com.example.myapplication.account.services.App.CHANNEL_ID;

public class UploadService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Uri pdfUri = intent.getParcelableExtra("pdfUri");

      String crsId =  intent.getStringExtra("crsId");
        String dataKey =  intent.getStringExtra("dataKey");
        String crsNm =  intent.getStringExtra("crsNm");
        String title =  intent.getStringExtra("title");
        String userId =  intent.getStringExtra("userId");

        String filename =  intent.getStringExtra("filename");



        Intent notificationIntent = new Intent(this, StartActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_cloud_upload_24);



        Notification notificationEx = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Virtual Class Room Running..")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_baseline_cloud_upload_24)
                .setLargeIcon(largeIcon)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
                ;

        startForeground(12, notificationEx);



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

       /* new Thread(new Runnable() {
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
        }).start();*/
      //shafirulislam94075948@gmail.com
        if (pdfUri != null) {
            final String url;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child("uploads").child("assignment").child(crsNm).child(title).child(filename).putFile(pdfUri)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String url = uri.toString();
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("assignment").child(crsId).child("collection").child(dataKey);
                                    HashMap<String, Object> hashMap = new HashMap<>();

                                    hashMap.put("fileName", filename);
                                    hashMap.put("fileUrl", url);
                                    hashMap.put("id", userId);
                                    hashMap.put("publish", new Date().getTime());

                                    reference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                // Toast.makeText(AssignmentUpload.this, "your Assignment uploaded", Toast.LENGTH_SHORT).show();
                                            } else{
                                                //  Toast.makeText(AssignmentUpload.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                    });
                                }

                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadService.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int totalByte = (int) taskSnapshot.getTotalByteCount()/1000;
                    int transferByte = (int) taskSnapshot.getBytesTransferred()/1000;
                    //int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    int currentProgress =  (100 * transferByte / totalByte);



                     if(currentProgress==100){
                         notification.setContentText("Submit Finish")
                         .setProgress(0, 0, false)
                         .setOngoing(false);
                     }else{
                         notification.setProgress(progressmax, currentProgress, false)
                                 .setContentText(transferByte +"kb / "+ totalByte+" kb");
                     }

                    notificationManagerCompat.notify(1, notification.build());
                }
            });


        }









        return START_NOT_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }











}
