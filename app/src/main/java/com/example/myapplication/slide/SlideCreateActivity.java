package com.example.myapplication.slide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

public class SlideCreateActivity extends AppCompatActivity {

    private TextInputLayout description;
    private Button upload;
    private ImageView attachment;
    private TextView showfile;
    String filename,crsNm, crsId,  describtion ;

    Uri pdfUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_create);


        description = findViewById(R.id.describtion);
        upload = findViewById(R.id.upload);
        attachment = findViewById(R.id.attachment);
       showfile = findViewById(R.id.filename);
       crsNm = getIntent().getStringExtra("crsNm");
       crsId = getIntent().getStringExtra("crsId");



       attachment.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (ContextCompat.checkSelfPermission(SlideCreateActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                   selectPdf();
               } else {
                   Toast.makeText(SlideCreateActivity.this, "no permission", Toast.LENGTH_SHORT).show();
                   ActivityCompat.requestPermissions(SlideCreateActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                  selectPdf();

               }
           }
       });


       upload.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

              if (!validDescrb() | !validFile()){
                  return;
              }else {
                 upload();
              }
           }
       });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else
            Toast.makeText(this, "please provide permission", Toast.LENGTH_SHORT).show();
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
                    cursor = (SlideCreateActivity.this).getContentResolver().query(pdfUri, null, null, null, null);
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
            showfile.setText(displayName);
            // Toast.makeText(this, "hmm", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "please select a file", Toast.LENGTH_SHORT).show();
    }

    public boolean validDescrb() {
        String s = description.getEditText().getText().toString();
        if (s.isEmpty()) {
            description.setError("please fill the batch name");
            return false;
        } else {
            description.setError(null);
        }


        return true;
    }

    public boolean validFile(){
        if (pdfUri!=null){
            return true;
        }else{
            Toast.makeText(this, "please select file", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    public void upload(){

        final ProgressDialog pd = new ProgressDialog(SlideCreateActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Uploading");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setProgress(0);
        describtion = description.getEditText().getText().toString();

        pd.show();
        if(pdfUri!=null) {
            final String url;
            StorageReference storageReference= FirebaseStorage.getInstance().getReference();
            storageReference.child("uploads").child("slide").child(crsNm).child(filename).putFile(pdfUri)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String url= uri.toString();
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("slide").child(crsId).child("collection");
                                    HashMap<String, Object> hashMap= new HashMap<>();


                                    hashMap.put("fileName",filename);
                                    hashMap.put("fileUrl",url);
                                    hashMap.put("description",describtion);
                                    hashMap.put("publish", new Date().getTime());

                                    reference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(SlideCreateActivity.this, "successfully uploaded", Toast.LENGTH_SHORT).show();
                                            }else Toast.makeText(SlideCreateActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                                        }

                                    });
                                }

                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SlideCreateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    int currentProgress= (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    pd.setProgress(currentProgress);
                    if(currentProgress==100){
                        pd.dismiss();
                    }
                }
            });


        }
    }

}
