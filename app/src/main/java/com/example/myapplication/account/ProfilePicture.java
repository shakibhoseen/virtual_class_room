package com.example.myapplication.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.account.teacher.TeacherModel;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

public class ProfilePicture extends AppCompatActivity implements View.OnClickListener{
 private MaterialEditText profileNameEdt, profilePhoneEdt, profileRollEdt;
 private LinearLayout linearLayout1;
 private CheckBox checkBoxProfile;
 private TextView  profileName,profilePhone, profilRoll, removeText;
 private ImageView profileImage;
 private Button removeProfile, editProfile;
 FirebaseUser mUser;
    Uri imageUri;
    private static final  int REQUEST_IMAGE =99;
    public final static String SHARED_PREFS = "sharedPrefs";
    public final static String STATUS = "status";
    String status_s, status_t;
    String filename;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        profileImage = findViewById(R.id.profile_image);
        profilePhoneEdt = findViewById(R.id.profile_phoneEdt);
        profileNameEdt = findViewById(R.id.profile_name_Edit);
        profileRollEdt = findViewById(R.id.profile_rollEdt);
        profileName = findViewById(R.id.profile_name);
        profilePhone = findViewById(R.id.profile_phone);
        profilRoll = findViewById(R.id.profile_roll);
        removeText = findViewById(R.id.remove_text);

        removeProfile = findViewById(R.id.remove_pic);
        editProfile = findViewById(R.id.profile_edit);
        linearLayout1 = findViewById(R.id.line1);
        checkBoxProfile = findViewById(R.id.checkbox_profile);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
         status= preferences.getString("status","");



        removeProfile.setVisibility(View.GONE);
       // removeText.setVisibility(View.GONE);

        if (status.equals("student")){
            profilePhone.setVisibility(View.GONE);
            readStudentProfile(mUser.getUid());
        }else {
            profilRoll.setVisibility(View.GONE);
            readTeacherProfile(mUser.getUid());
        }

       editProfile.setOnClickListener(this);
        removeProfile.setOnClickListener(this);
        checkBoxProfile.setOnClickListener(this);


    }

    public void readTeacherProfile(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("teacher").child(id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TeacherModel teacherModel = dataSnapshot.getValue(TeacherModel.class);
                status_t=teacherModel.getImageUrl();
                profileName.setText(teacherModel.getName());
                profileNameEdt.setText(teacherModel.getName());
                profilePhone.setText(teacherModel.getPhone());
                profilePhoneEdt.setText(teacherModel.getPhone());

                if (!teacherModel.getImageUrl().equals("") && !teacherModel.getImageUrl().equals("default")){
                    Glide.with(ProfilePicture.this).load(teacherModel.getImageUrl()).into(profileImage);
                    
                }else{
                    removeProfile.setText("choose");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public  void readStudentProfile(String id){
        DatabaseReference  reference= FirebaseDatabase.getInstance().getReference("Users").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               StudentModel studentModel = dataSnapshot.getValue(StudentModel.class);
               status_s=studentModel.getImageUrl();
                  if (!studentModel.getImageUrl().equals("") &&!studentModel.getImageUrl().equals("default")){
                      Picasso.with(ProfilePicture.this).load(studentModel.getImageUrl()).into(profileImage);
                  }else{
                     removeProfile.setText("choose");
                  }
                  profileName.setText(studentModel.getUsername());
                  profileNameEdt.setText(studentModel.getUsername());
                  profilRoll.setText(studentModel.getRoll());
                  profileRollEdt.setText(studentModel.getRoll());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
           case R.id.profile_edit:{
               if (editProfile.getText().equals("Edit profile")){
                   removeProfile.setVisibility(View.VISIBLE);
                   visibleOperation();
                   editProfile.setText("save");
                   return;
                }else if(editProfile.getText().equals("save")){
                   //Toast.makeText(this, "save ", Toast.LENGTH_SHORT).show();
                   if(status_s!=null){
                       //student
                       String username = profileNameEdt.getText().toString();
                       String roll = profileRollEdt.getText().toString();
                       if (!username.isEmpty()&&!roll.isEmpty())
                       upload(username,"Users","",roll,imageUri);
                   }else{
                       //teacher
                       String username = profileNameEdt.getText().toString();

                       String phone = profilePhoneEdt.getText().toString();
                       if (!username.isEmpty()&&!phone.isEmpty())
                          upload(username,"teacher",phone,"",imageUri);
                   }


                }
               return ;

            }
            case  R.id.remove_pic:{
                 if (removeProfile.getText().equals("remove picture")&&status_s!=null){
                     Toast.makeText(this, "chose", Toast.LENGTH_SHORT).show();
                     removeProfile.setText("choose");
                      deletePic(status_s, "Users");
                      return;
                 }else if(removeProfile.getText().equals("remove picture")&&status_t!=null){
                     Toast.makeText(this, "chose", Toast.LENGTH_SHORT).show();
                     removeProfile.setText("choose");
                     deletePic(status_t,"teacher");
                     return;
                 }else{
                     Toast.makeText(this, "choose", Toast.LENGTH_SHORT).show();
                     openImage();
                     return;
                 }
            }
            case R.id.checkbox_profile: {
                if (checkBoxProfile.isChecked()){
                    linearLayout1.setVisibility(View.VISIBLE);
                }else{
                    linearLayout1.setVisibility(View.GONE);
                }
            }

        }

    }


  public void deletePic(String url, final String status){
      StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

      storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {
              DatabaseReference s = FirebaseDatabase.getInstance().getReference(status).child(FirebaseAuth.getInstance().getUid());
              HashMap<String, Object> hasmap = new HashMap<>();

              hasmap.put("imageUrl","default");
              s.updateChildren(hasmap);

          }
      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
              Toast.makeText(ProfilePicture.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
          }
      });
  }





 public void visibleOperation(){

        if(status.equals("student")){
            profilRoll.setVisibility(View.GONE);
            profileName.setVisibility(View.GONE);

            profileNameEdt.setVisibility(View.VISIBLE);
            profileRollEdt.setVisibility(View.VISIBLE);
        }else{
            profilePhone.setVisibility(View.GONE);
            profileName.setVisibility(View.GONE);

            profileNameEdt.setVisibility(View.VISIBLE);
            profilePhoneEdt.setVisibility(View.VISIBLE);
        }
 }



    private void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQUEST_IMAGE);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = ProfilePicture.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode ==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            String uriString = imageUri.toString();
            File myFile = new File(uriString);
            String path= myFile.getAbsolutePath();
            String displayName = null;

            if (uriString.startsWith("content://")){
                Cursor cursor= null;
                try {
                    cursor = getApplication().getContentResolver().query(imageUri, null,null,null,null);
                    if (cursor!=null &&cursor.moveToFirst() ){
                        displayName= cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }finally {
                    cursor.close();
                }
            }else if(uriString.startsWith("file://")){
                displayName =myFile.getName();
            }



            filename = displayName;


        } else Toast.makeText(ProfilePicture.this, "please select a file", Toast.LENGTH_SHORT).show();

        }







    public void upload(final String username,final String status, final String phone, final String roll, Uri uri){
        final ProgressDialog pd = new ProgressDialog(ProfilePicture.this);
        pd.setMessage("updating..");

        pd.show();
        if(uri!=null){
            final StorageReference fileReference = FirebaseStorage.getInstance().getReference().child(status).child("profile").child(filename);
            fileReference.putFile(uri).
            continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ProfilePicture.this, "update successful", Toast.LENGTH_SHORT).show();
                        Uri downloadUri = (Uri) task.getResult();
                        String mUri = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(status);

                        HashMap<String , Object>hashMap = new HashMap<>();
                        if(status_s!=null && checkBoxProfile.isChecked()){
                            hashMap.put("username",username);
                            hashMap.put("roll",roll);
                        }

                        else if (checkBoxProfile.isChecked()){
                            hashMap.put("name",username);
                          hashMap.put("phone",phone);
                        }

                        hashMap.put("imageUrl", mUri);

                        reference.child(FirebaseAuth.getInstance().getUid()).updateChildren(hashMap);

                        pd.dismiss();

                    }else {
                        Toast.makeText(ProfilePicture.this, "failed to success", Toast.LENGTH_SHORT).show();

                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfilePicture.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(status);

            HashMap<String , Object>hashMap = new HashMap<>();
            if(status_s!=null && checkBoxProfile.isChecked()){
                hashMap.put("username",username);
                hashMap.put("roll",roll);
            }

            else if (checkBoxProfile.isChecked()){
                hashMap.put("name",username);
                hashMap.put("phone",phone);
            }

            reference.child(FirebaseAuth.getInstance().getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ProfilePicture.this, "your details are updated now", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            pd.dismiss();

        }


    }


























}
