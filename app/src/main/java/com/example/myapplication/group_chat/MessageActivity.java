package com.example.myapplication.group_chat;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import android.os.Bundle;


import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.account.ProfilePicture;
import com.example.myapplication.account.StudentModel;
import com.example.myapplication.account.teacher.MainTeacherActivity;
import com.example.myapplication.account.teacher.TeacherModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 512;
    private static final String TAG = "Message";
    private CircleImageView profile_image;
    private ImageView select_imageview, holderImage;
    private static final int REQUEST_IMAGE = 99;
    private TextInputEditText text_sent;
    private TextView username;
    private ImageButton btn_sent, image_choser;
    private RecyclerView recyclerView;

    public final static String SHARED_PREFS = "sharedPrefs";
    public final static String STATUS = "status";
    private FirebaseUser firebaseUser;
    private DatabaseReference readRef, seenRef, profileRef;
    private List<Chat> mchat;
    MessageAdapter messageAdapter;
    Uri imageUri;
    StorageReference storageReference;
    StorageTask uploadtask;
    //  APIService apiService;
    String userid, userNameSt, ownImg, status;
    boolean notify = false, isSending = false;

    Intent intent;
    ValueEventListener seenListener, readListener;
    final List<SeenList> list = new ArrayList<>();

    private RequestQueue mRequestQue;
    private String URL = "https://fcm.googleapis.com/fcm/send";
    private ValueEventListener studntLsner, teachrLsner;
    private DatabaseReference studntRef, teachrRef;
    private StoreMsgHelper storeMsgHelper;
    private StoreSeenHelper storeSeenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mchat = new ArrayList<>();
        mRequestQue = Volley.newRequestQueue(this);
        storeMsgHelper = new StoreMsgHelper(this);
        storeSeenHelper = new StoreSeenHelper(this);

        FirebaseMessaging.getInstance().subscribeToTopic("news");

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        status = preferences.getString("status", "");

        setSupportActionBar(toolbar);

       /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("teacher")) {
                    startActivity(new Intent(getApplicationContext(), MainTeacherActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        });*/

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        text_sent = findViewById(R.id.text_sent);
        btn_sent = findViewById(R.id.btn_sent);
        image_choser = findViewById(R.id.btn_camera);
        holderImage = findViewById(R.id.image_holder_id);
        /*select_imageview =findViewById(R.id.select_Imageview);*/

        intent = getIntent();

        // final String userid= intent.getStringExtra("userid");


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("chatimages");

        userid = firebaseUser.getUid();


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        messageAdapter = new MessageAdapter(MessageActivity.this, mchat, "url");
        recyclerView.setAdapter(messageAdapter);


        if (status.equals("teacher")) {
            readTeacherProfile(userid);
        } else {
            readStudentProfile(userid);
        }


       /*reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               User user =dataSnapshot.getValue(User.class);
               username.setText(user.getUsername());
               if(user.getImageUrl().equals("default")){
                   profile_image.setImageResource(R.mipmap.ic_launcher);
               }else Picasso.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);
                   //Glide.with(MessageActivity.this).load(user.getImageUrl()).into(profile_image);

               readMessage(firebaseUser.getUid(),userid,user.getImageUrl());
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });*/


        image_choser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // openImage();

               pickFromGallery();
            }
        });


        btn_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                isSending = true;

                repeatAnim();
                String message = text_sent.getText().toString();
                if (!message.equals("") || imageUri != null) {
                    sendMessage(firebaseUser.getUid(), userid, message, imageUri);

                } else
                    Toast.makeText(MessageActivity.this, "you cant sent empty message", Toast.LENGTH_SHORT).show();
            }
        });
        readMessage(firebaseUser.getUid(), userid, "fhh");

        readOffline();

    }


    public void readMessage(final String sender, final String receiver, final String imgUrl) {

        readRef = FirebaseDatabase.getInstance().getReference("Chats").child("content");


        readListener = readRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


              /*  for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    chat.setContentId(snapshot.getKey().toString());
                    chat.setSeenlist(list);
                    chat.setClickOne(false);
                    mchat.add(chat);
                }

                messageAdapter.notifyDataSetChanged();
                int n = mchat.size();
                for (int i = n - 1; i >= 0; i--) {
                    seenMessage(i);
                }
                smoothScroll();*/

               /* Cursor cursor = storeMsgHelper.getAlldata();

                String lastItem = null;
                        if(cursor.moveToLast())
                           lastItem = cursor.getColumnName(4);*/
                storeMsgHelper.deleteAll();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    // if (lastItem!=null)
                    storeMsgHelper.insertData(chat.getSender(), chat.getMessage(), "0", snapshot.getKey().toString(), chat.getPublish());
                }

                readOffline();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void status(String status) {
        profileRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> map = new HashMap<>();

        map.put("status", status);
        profileRef.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        readRef.addValueEventListener(readListener);
        // status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (studntRef != null)
            studntRef.removeEventListener(studntLsner);
        if (teachrLsner != null)
            teachrRef.removeEventListener(teachrLsner);
        if (readListener != null)
            readRef.removeEventListener(readListener);
        //seenRef.removeEventListener(seenListener);


        // status("offline");
    }

    //final String userid, final String userNameSt, final String ownImg

    private void seenMessage(final int index) {


        final Chat chat = mchat.get(index);
        String conteId = chat.getContentId();

        if (conteId == null) {

            return;
        }


        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chats")
                .child("seenlist").child(conteId);


        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<SeenList> seenLists = new ArrayList<>();
                boolean isSeen = false;
                boolean isUpdate = false;
                boolean oneInsert = true;   // on time insert
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    SeenList list = snapshot.getValue(SeenList.class);
                    seenLists.add(list);
                    if (snapshot.getKey().equals(userid)) {
                        isSeen = true;
                        if (ownImg != null && userNameSt != null)
                            if (!list.getOwnImg().equals(ownImg) || !list.getName().equals(userNameSt)) {
                                isUpdate = true;
                            }
                    }

                    if (oneInsert && snapshot.getKey().equals(chat.getSender())) {
                        oneInsert = false;
                        mchat.get(index).setSenderImg(list.getOwnImg());
                        mchat.get(index).setSenderNm(list.getName());
                    }
                }
                mchat.get(index).setSeenlist(seenLists);

                if (!isSeen || seenLists.size() == 0 || isUpdate) {
                    HashMap<String, Object> map = new HashMap<>();

                    map.put("name", userNameSt);
                    map.put("ownImg", ownImg);
                    reference1.child(userid).updateChildren(map);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void onilneSeenMsg(final int index) {
        {

            final Chat chat = mchat.get(index);
            String conteId = chat.getContentId();
            final boolean[] insertOneTimeOff = {true};

            if (conteId == null) {

                return;
            }


            final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chats")
                    .child("seenlist").child(conteId);


            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    List<SeenList> seenLists = new ArrayList<>();
                    boolean isSeen = false;
                    boolean isUpdate = false;
                    boolean oneInsert = true;   // on time insert
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                        SeenList list = snapshot.getValue(SeenList.class);
                        list.setUserId(snapshot.getKey().toString());
                        seenLists.add(list);
                        if (snapshot.getKey().equals(userid)) {
                            isSeen = true;
                            if (ownImg != null && userNameSt != null)
                                if (!list.getOwnImg().equals(ownImg) || !list.getName().equals(userNameSt)) {
                                    isUpdate = true;
                                }
                        }

                    }
                    if (insertOneTimeOff[0]) {
                        insertOneTimeOff[0] = false;
                        // mchat.get(index).setSeenlist(seenLists);
                        String contentId = mchat.get(index).getContentId();
                        for (SeenList list1 : seenLists)
                            storeSeenHelper.insertData(list1.getName(), list1.getOwnImg(), list1.getUserId(), contentId);
                    }


                    if (!isSeen || seenLists.size() == 0 || isUpdate) {
                        HashMap<String, Object> map = new HashMap<>();

                        map.put("name", userNameSt);
                        map.put("ownImg", ownImg);
                        reference1.child(userid).updateChildren(map);
                        // insertOneTimeOff[0] = true;
                    }


                    messageAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }


    private void offlineSeenMsg() {
        Cursor cursor = storeSeenHelper.getAscSeenList();
        while (cursor.moveToNext()) {

        }

    }


    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = MessageActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       /* if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadtask != null) {

            } else {
                select_imageview.setImageURI(imageUri);
                select_imageview.setVisibility(View.VISIBLE);
                // uploadFile();
                // Toast.makeText(getContext(), "unable to open file", Toast.LENGTH_SHORT).show();
            }
        }*/
        switch (requestCode) {
            case GALLERY_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getData() != null) {

                        launchImageCrop(data.getData());
                    }
                } else {
                    Log.e(TAG, "Image selection error: Couldn't select that image from memory.");
                }
            }

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE : {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == Activity.RESULT_OK && result!= null) {
                    holderImage.setVisibility(View.VISIBLE);
                    holderImage.setImageURI(result.getUri());

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.e(TAG, "Crop error: ${result.getError()}");
                }
            }
        }


    }


    public void sendMessage(final String sender, final String receiver, final String message, Uri uri) {
        btn_sent.setEnabled(false);
        /*if(uri!=null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(uri));
            uploadtask = fileReference.putFile(uri);
            uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        Uri downloadUri = (Uri) task.getResult();
                        String mUri = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                        HashMap<String , Object>hashMap = new HashMap<>();
                        hashMap.put("sender",sender);
                        hashMap.put("receiver",receiver);
                        hashMap.put("message",message);
                        hashMap.put("imageUrl", mUri);
                        hashMap.put("isseen",false);
                        reference.child("Chats").push().setValue(hashMap);
                        text_sent.getEditText().setText("");
                        btn_sent.setClickable(true);
                        imageUri= null;
                        select_imageview.setVisibility(View.GONE);
                        select_imageview.setImageURI(imageUri);
                        pd.dismiss();

                    }else {
                        Toast.makeText(MessageActivity.this, "failed to success", Toast.LENGTH_SHORT).show();
                        btn_sent.setClickable(true);
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else { */
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("message", message);
        hashMap.put("publish", new Date().getTime());
        hashMap.put("totalSeen", "0");
        reference.child("Chats").child("content").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                btn_sent.setEnabled(true);
            }
        });
        text_sent.setText("");


        // }

       /* final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =  dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotification(receiver, user.getUsername(), msg);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        sendNotification(userNameSt, message);
    }


    private void sendNotification(final String senderNm, final String message) {
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", "/topics/" + "news");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", senderNm);
            notificationObj.put("body", message);

            JSONObject extraData = new JSONObject();
            extraData.put("SenderUrl", ownImg);
            extraData.put("senderId", userid);

            mainObj.put("notification", notificationObj);
            mainObj.put("data", extraData);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    mainObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MessageActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAA4SN3wuM:APA91bGNFIHeBqY5B7CifXCmbk8dyfjocYJRkmMRagoq6nQmJh03kSYwl3sdI93evpf49E-VnrH486S_mjS-axq8synXNCOf0rYnVbtdgdbAvqHYumlVRqvsemrxFvU9szRYOOMz3E2A");
                    return header;
                }
            };

            mRequestQue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void readStudentProfile(String id) {
        studntRef = FirebaseDatabase.getInstance().getReference("Users").child(id);
        final SeenList seenList = new SeenList();
        studntLsner = studntRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StudentModel studentModel = dataSnapshot.getValue(StudentModel.class);
                assert studentModel != null;
                userNameSt = studentModel.getUsername();
                ownImg = studentModel.getImageUrl();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void readTeacherProfile(String id) {
        teachrRef = FirebaseDatabase.getInstance().getReference("teacher").child(id);

        teachrLsner = teachrRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TeacherModel teacherModel = dataSnapshot.getValue(TeacherModel.class);
                userNameSt = teacherModel.getName();
                ownImg = teacherModel.getImageUrl();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void smoothScroll() {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (mchat.size() != 0)
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (status.equals("teacher")) {
            startActivity(new Intent(getApplicationContext(), MainTeacherActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        super.onBackPressed();
    }


    private void readOffline() {

        Cursor cursor = storeMsgHelper.getAlldata();
        mchat.clear();
        while (cursor.moveToNext()) {

                   /* Chat chat = new Chat(cursor.getColumnName(1),"",cursor.getColumnName(2),
                            "",list, cursor.getColumnName(3));*/
            Chat chat = new Chat();
            chat.setSender(cursor.getString(1));
            chat.setMessage(cursor.getString(2));

            chat.setTotalSeen(cursor.getString(3));
            chat.setPublish(cursor.getLong(6));
            chat.setContentId(cursor.getString(4));
            chat.setSeenlist(list);
            chat.setClickOne(false);
            mchat.add(chat);
        }

        messageAdapter.notifyDataSetChanged();
        int n = mchat.size();
        for (int i = n - 1; i >= 0; i--) {
            seenMessage(i);
        }
        smoothScroll();
    }


    private void repeatAnim() {
        if (!isSending)
            return;

        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.left_right_sending_animation);


        topAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                repeatAnim();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        btn_sent.startAnimation(topAnim);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    private void launchImageCrop(Uri uri) {
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                //.setAspectRatio(1920, 1080)
                .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
                .start(this);
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intent.setType("image/*");
        String[] mimeTypes = new String[]{"image/jpeg", "image/png", "image/jpg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }


}
