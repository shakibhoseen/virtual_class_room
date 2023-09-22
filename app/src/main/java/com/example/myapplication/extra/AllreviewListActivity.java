package com.example.myapplication.extra;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import com.example.myapplication.account.StudentModel;
import com.example.myapplication.quiz.review.ReviewStartActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllreviewListActivity extends AppCompatActivity {
    private CircleImageView profileImg;
    private Button viewBtn;
    private Toolbar toolbar;
    private TextView userName, toolbarTxt, userRoll;
    private String crsId, ikey, uRoll;
    private double increase, decrease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allreview_list);

        toolbar = findViewById(R.id.toolbar_id);
        setSupportActionBar(toolbar);
        SharedPreferences preferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final String  status = preferences.getString("status", "");
        toolbarTxt = findViewById(R.id.toolbar_txt);
        userName = findViewById(R.id.username);
        userRoll = findViewById(R.id.rollnoId);
        profileImg = findViewById(R.id.profile_image);
        viewBtn = findViewById(R.id.view_btn);

        crsId = getIntent().getStringExtra("crsId");
        ikey = getIntent().getStringExtra("dataKey");
        final String userId = getIntent().getStringExtra("userId");
        String title = getIntent().getStringExtra("title");
        increase = getIntent().getDoubleExtra("increase", 1);
        decrease = getIntent().getDoubleExtra("decrease", 0);
        final boolean seeReview = getIntent().getBooleanExtra("seeReview",true);

        toolbarTxt.setText("Review of " + title);


        readStudentProfile(userId);


        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!status.equals("teacher") && seeReview){
                    Toast.makeText(AllreviewListActivity.this, "Its not available before exam end date expired", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(AllreviewListActivity.this, ReviewStartActivity.class);
                intent.putExtra("review", "yes");
                intent.putExtra("crsId", crsId);
                intent.putExtra("iKey", ikey);
                intent.putExtra("userId", userId);
                intent.putExtra("increase", increase);
                intent.putExtra("decrease", decrease);


                if (uRoll != null)
                    intent.putExtra("uRoll", uRoll);
                startActivity(intent);


            }
        });

    }


    public void readStudentProfile(String uId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StudentModel studentModel = dataSnapshot.getValue(StudentModel.class);

                if (!studentModel.getImageUrl().equals("") && !studentModel.getImageUrl().equals("default")) {
                    Picasso.with(AllreviewListActivity.this).load(studentModel.getImageUrl()).into(profileImg);
                } else {
                    profileImg.setImageResource(R.drawable.meena);
                }
                userName.setText(studentModel.getUsername());
                userRoll.setText(studentModel.getRoll());
                uRoll = studentModel.getRoll();

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
