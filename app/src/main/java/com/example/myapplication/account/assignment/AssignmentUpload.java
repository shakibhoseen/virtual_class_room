package com.example.myapplication.account.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.account.StudentModel;
import com.example.myapplication.account.services.ExamJobIntentService;
import com.example.myapplication.account.services.ExampleIntentService;
import com.example.myapplication.account.services.ExampleJobService;
import com.example.myapplication.account.services.UploadService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

public class AssignmentUpload extends AppCompatActivity {
    public static final String TAG = "AssignmentUpload";
    private Toolbar toolbar;
    private Button uploadBtn;
    private Button selecBtn;
    private TextView notification, toolbarTxt;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    Uri pdfUri;
    String filename, roll, userName, imageUrl;
    FirebaseUser firebaseUser;
    private static final int REQUEST_IMAGE = 99;
    String crsId, dataKey, title, crsNm;
    String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_upload);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarTxt = findViewById(R.id.toolbar_txt);
        crsId = getIntent().getStringExtra("crsId");
        dataKey = getIntent().getStringExtra("iKey");
        title = getIntent().getStringExtra("title");
        crsNm = getIntent().getStringExtra("crsNm");
        toolbarTxt.setText(title+" of "+crsNm);
        uploadBtn = findViewById(R.id.btn_home);
        selecBtn = findViewById(R.id.selectId);
        notification = findViewById(R.id.text_home);

        storage = FirebaseStorage.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        userid = firebaseUser.getUid();

        readUser(userid);


        uploadBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              /*  if (pdfUri != null) {
                    uploadFile(pdfUri, crsId, dataKey);
                } else
                    Toast.makeText(AssignmentUpload.this, "select a file", Toast.LENGTH_SHORT).show();*/


               /* Intent intent = new Intent(AssignmentUpload.this, UploadService.class);
                intent.putExtra("crsId", crsId);
                intent.putExtra("dataKey", dataKey);
                intent.putExtra("crsNm", crsNm);
                intent.putExtra("title", title);
                intent.putExtra("userId", userid);

                intent.putExtra("filename", filename);
                intent.putExtra("pdfUri", pdfUri);*/
             //   ContextCompat.startForegroundService(getApplicationContext(), intent);

                ComponentName componentName = new ComponentName(AssignmentUpload.this, ExampleJobService.class);
                JobInfo info = new JobInfo.Builder(123, componentName)
                        .setRequiresCharging(true)
                        //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPersisted(true)
                        //.setPeriodic(15*60*1000)
                        .build();

                JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                int resultCode = scheduler.schedule(info);
                if(resultCode == JobScheduler.RESULT_SUCCESS){
                    Log.d(TAG, "job scheduled");
                    Toast.makeText(AssignmentUpload.this, "job scheduled", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d(TAG, "job scheduling failure");
                    Toast.makeText(AssignmentUpload.this, "job scheduling failure", Toast.LENGTH_SHORT).show();
                }

                /*Intent serviceIntent = new Intent(AssignmentUpload.this, ExamJobIntentService.class);
                serviceIntent.putExtra("inputExtra", "jobIntent");
                ExamJobIntentService.enqueueWork(AssignmentUpload.this, serviceIntent);*/

               /* Intent serviceIntent = new Intent(AssignmentUpload.this, ExampleIntentService.class);
                serviceIntent.putExtra("intentExtra", "intentSr");
                ContextCompat.startForegroundService (getApplicationContext(), serviceIntent);*/

            }
        });


        selecBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");

                JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                scheduler.cancel(123);
                Toast.makeText(AssignmentUpload.this, "cancel job", Toast.LENGTH_SHORT).show();

              /*  if (ContextCompat.checkSelfPermission(AssignmentUpload.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectPdf();
                } else {
                    Toast.makeText(AssignmentUpload.this, "no permission", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(AssignmentUpload.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE);
                    //selectPdf();

                }*/
             /*   Intent intent = new Intent(AssignmentUpload.this, UploadService.class);
                stopService(intent);*/

             /*   JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                scheduler.cancel(123);
                Log.d(TAG, "job cancel ");*/
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     selectPdf();
        } else
            Toast.makeText(this, "please provide permission"+ requestCode, Toast.LENGTH_SHORT).show();
    }

    private void selectPdf() {
        Intent intent = new Intent();
        // intent.setType("application/pdf/image/docx/video/*");
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pdfUri = data.getData();
            String uriString = pdfUri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = (AssignmentUpload.this).getContentResolver().query(pdfUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }


            filename = displayName;
            notification.setText(displayName);
            // Toast.makeText(this, "hmm", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "please select a file", Toast.LENGTH_SHORT).show();
    }


    private void uploadFile(final Uri uri, final String crsId, final String iKey) {
        final ProgressDialog pd = new ProgressDialog(AssignmentUpload.this);
        pd.setCancelable(false);
        pd.setMessage("Uploading");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setProgress(0);


        pd.show();
        if (uri != null) {
            final String url;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child("uploads").child("assignment").child(crsNm).child(title).child(filename).putFile(uri)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String url = uri.toString();
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("assignment").child(crsId).child("collection").child(iKey);
                                    HashMap<String, Object> hashMap = new HashMap<>();

                                    hashMap.put("fileName", filename);
                                    hashMap.put("fileUrl", url);
                                    hashMap.put("id", userid);
                                    hashMap.put("publish", new Date().getTime());

                                    reference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                uploadBtn.setEnabled(false);
                                                Toast.makeText(AssignmentUpload.this, "your Assignment uploaded", Toast.LENGTH_SHORT).show();
                                            } else
                                                Toast.makeText(AssignmentUpload.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                                        }

                                    });
                                }

                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AssignmentUpload.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    pd.setProgress(currentProgress);
                    if (currentProgress == 100) {
                        pd.dismiss();
                    }
                }
            });


        }


    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = AssignmentUpload.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    public void readUser(String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StudentModel user = dataSnapshot.getValue(StudentModel.class);
                roll = user.getRoll();
                userName = user.getUsername();
                imageUrl = user.getImageUrl();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
