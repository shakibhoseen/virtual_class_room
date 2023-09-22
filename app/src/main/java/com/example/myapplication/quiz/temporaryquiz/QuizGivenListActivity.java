package com.example.myapplication.quiz.temporaryquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.account.StudentModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizGivenListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView empty_list, toolbarTxt;
    private List<QuizSubmitModel> modelList;
    private List<QuizSubmitModel> modelListAll;

    QuizGivenAdapter submitAdapter;
    private String totalQsn;
    double markTotal;
    boolean seeReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_given_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTxt = findViewById(R.id.toolbar_txt);
        totalQsn = getIntent().getStringExtra("total");
        String crsId = getIntent().getStringExtra("crsId");
        String ikey = getIntent().getStringExtra("iKey");
        String title = getIntent().getStringExtra("title");
        double increase = getIntent().getDoubleExtra("increase", 1);
        double decrease = getIntent().getDoubleExtra("decrease", 0);
        seeReview = getIntent().getBooleanExtra("seeReview",true);

        toolbarTxt.setText(title);
        double p = Double.parseDouble(totalQsn);
        markTotal = getIntent().getDoubleExtra("mark",p);

        empty_list = findViewById(R.id.empty_list);




        recyclerView = findViewById(R.id.recyclerId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        modelList = new ArrayList<>();
        modelListAll = new ArrayList<>();

        submitAdapter = new QuizGivenAdapter(QuizGivenListActivity.this, modelList, markTotal, crsId
                      , ikey, title, increase, decrease, seeReview);
        recyclerView.setAdapter(submitAdapter);

        readUser(crsId, ikey);
    }


    public void readUser(String crsId, String iKey) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("collection").child(iKey);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelListAll.clear();
                modelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    QuizSubmitModel user = snapshot.getValue(QuizSubmitModel.class);
                    assert firebaseUser != null;
                    assert user != null;

                    modelList.add(user);
                    modelListAll.add(user);

                }
                if (modelList.size() == 0) {
                    empty_list.setVisibility(View.VISIBLE);
                } else {
                    empty_list.setVisibility(View.GONE);
                }


                for (int i=0; i<modelList.size();i++){
                    studentInfom(modelList.get(i).getId(), i);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void studentInfom(String id,final int position) {

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(id);

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    StudentModel studentModel = dataSnapshot.getValue(StudentModel.class);
                    modelList.get(position).setImageUrl(studentModel.getImageUrl());
                    modelList.get(position).setRoll(studentModel.getRoll());
                    modelList.get(position).setUsername(studentModel.getUsername());
                    //
                    modelListAll.get(position).setImageUrl(studentModel.getImageUrl());
                    modelListAll.get(position).setRoll(studentModel.getRoll());
                    modelListAll.get(position).setUsername(studentModel.getUsername());
                }
                submitAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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



    public void searchStuentPer(String s) {
        modelList.clear();
        if (s.isEmpty()) {
            modelList.addAll(modelListAll);


        } else {
            for (QuizSubmitModel model : modelListAll) {
                if (model.getUsername().toLowerCase().contains(s.toLowerCase() )|| model.getRoll().toLowerCase().contains(s.toLowerCase() )) {
                    modelList.add(model);
                }
            }
        }
        submitAdapter.notifyDataSetChanged();
    }
}
