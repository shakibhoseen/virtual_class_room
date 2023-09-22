package com.example.myapplication.account.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.account.StudentModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AssignmentGivenListActivity extends AppCompatActivity implements AssignGivenAdapter.OnDownloadClickListener {
    private RecyclerView recyclerView;
    private TextView empty_list, toolbarTxt;
    private List<AssignmentSubmitModel> modelList;
    private List<AssignmentSubmitModel> modelListAll;
    private List<StudentModel> studentModelList;

    private DatabaseReference reference;
    private AssignGivenAdapter submitAdapter;
    String crsId, ikey, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_given_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTxt = findViewById(R.id.toolbar_txt);

        crsId = getIntent().getStringExtra("crsId");
        ikey = getIntent().getStringExtra("iKey");
        title = getIntent().getStringExtra("title");
        toolbarTxt.setText(title);
        empty_list = findViewById(R.id.empty_list);

        reference = FirebaseDatabase.getInstance().getReference("assignment").child(crsId).child("collection").child(ikey);

        recyclerView = findViewById(R.id.recyclerId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        modelList = new ArrayList<>();
        studentModelList = new ArrayList<>();
        modelListAll = new ArrayList<>();

        submitAdapter = new AssignGivenAdapter(AssignmentGivenListActivity.this, modelList, crsId, ikey);
        recyclerView.setAdapter(submitAdapter);

        readCollection();


        submitAdapter.setOnDownloadClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.assignment_given_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);


        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // searchUsers(newText);
                    searchStuentPer(newText);
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    public void readCollection() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelList.clear();
                modelListAll.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AssignmentSubmitModel user = snapshot.getValue(AssignmentSubmitModel.class);
                    assert firebaseUser != null;
                    assert user != null;
                    user.setSnapKey(snapshot.getKey());
                    modelList.add(user);
                    modelListAll.add(user);

                }
                // submitAdapter.notifyDataSetChanged();
                if (modelList.size() == 0) {
                    empty_list.setVisibility(View.VISIBLE);
                } else {
                    empty_list.setVisibility(View.GONE);
                }

                for (int i = 0; i < modelList.size(); i++) {

                    studentInfom(modelList.get(i).getId(), i);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }


    public void studentInfom(String id, final int position) {

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(id);

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (modelListAll.size()<=position)
                    return;

                if (dataSnapshot.exists()) {
                    StudentModel studentModel = dataSnapshot.getValue(StudentModel.class);
                    modelList.get(position).setImageUrl(studentModel.getImageUrl());
                    modelList.get(position).setRoll(studentModel.getRoll());
                    modelList.get(position).setUsername(studentModel.getUsername());
                    //
                    modelListAll.get(position).setImageUrl(studentModel.getImageUrl());
                    modelListAll.get(position).setRoll(studentModel.getRoll());
                    modelListAll.get(position).setUsername(studentModel.getUsername());
                    //studentModelList.add(studentModel);
                }
                submitAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


   /* private void searchUsers(final String s){
        Query query = FirebaseDatabase.getInstance().getReference("assignment").child(crsId).child("collection").child(ikey).orderByChild("username")
                .startAt(s).endAt(s+"\uff8f");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    AssignmentSubmitModel model = snapshot.getValue(AssignmentSubmitModel.class);
                    modelList.add(model);
                }
                submitAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/


    public void searchStuentPer(String s) {
             modelList.clear();
        if (s.isEmpty()) {
           modelList.addAll(modelListAll);


        } else {
            for (AssignmentSubmitModel model : modelListAll) {
                if (model.getUsername().toLowerCase().contains(s.toLowerCase() )|| model.getRoll().toLowerCase().contains(s.toLowerCase() )) {
                  modelList.add(model);
                }
            }
        }
        submitAdapter.notifyDataSetChanged();
    }




    public void downloadFile(String filename, String distinationDirectory, String url) {
        //create file
        // File file = new File(Environment.getExternalStorageDirectory(),"mypdf.pdf");
        DownloadManager downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(filename);
        //.setDestinationUri(Uri.fromFile(file));
        // request.setDestinationInExternalFilesDir(context,distinationDirectory,filename+fileExtention);

        request.allowScanningByMediaScanner();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        downloadManager.enqueue(request);
    }


    public boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error", "You have permission");
                return true;
            } else {

                Log.e("Permission error", "You have asked for permission");
                ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error", "You already have the permission");
            return true;
        }
    }





    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void OnDownLoadClick(int position) {
        AssignmentSubmitModel model =  modelList.get(position);
        if (haveStoragePermission()) {
            downloadFile(model.getFileName(), "SD card/vartual class/", model.getFileUrl());
        } else {
            Toast.makeText(this, "Allow the permission", Toast.LENGTH_SHORT).show();
        }
    }
}
