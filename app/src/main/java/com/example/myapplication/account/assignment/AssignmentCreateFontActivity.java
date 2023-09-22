package com.example.myapplication.account.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.quiz.DateUtils;
import com.example.myapplication.quiz.QuizFontActivity;
import com.example.myapplication.quiz.QuizInsertActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class AssignmentCreateFontActivity extends AppCompatActivity {
    private TextInputLayout title, describtion,  start_time, end_time;
    private Button createAssignment;
    DateUtils dateUtils;

    private String crsId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_create_font);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dateUtils = new DateUtils();

        title = findViewById(R.id.quiz_title_id);
        describtion = findViewById(R.id.describtion);

        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);


        crsId = getIntent().getStringExtra("crsId");

        createAssignment = findViewById(R.id.implementBtn);


        createAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validtitle() | !validDescrb() |!validStartTimeQ() | !validSEndTimeQ()){
                    return;
                }
                String title_s = title.getEditText().getText().toString();
                String description_s = describtion.getEditText().getText().toString();

                long startTime = dateUtils.transform(start_time.getEditText().getText().toString());
                long endTime = dateUtils.transform(end_time.getEditText().getText().toString());
                if (endTime<startTime){
                    end_time.setError("last date not smaller than start time");
                    return;
                }

                upload(title_s, description_s,  startTime, endTime);
            }
        });
    }


    public boolean validtitle() {
        String s = title.getEditText().getText().toString();
        if (s.isEmpty()) {
            title.setError("please fill the batch name");
            return false;
        } else {
            title.setError(null);
        }


        return true;
    }


    public boolean validDescrb() {
        String s = describtion.getEditText().getText().toString();
        if (s.isEmpty()) {
            describtion.setError("please fill the batch name");
            return false;
        } else {
            describtion.setError(null);
        }


        return true;
    }

    public boolean validStartTimeQ() {
        String s = start_time.getEditText().getText().toString();
        if (s.isEmpty()) {
            start_time.setError("correct the formate");
            return false;
        } else {
            start_time.setError(null);
        }


        return true;
    }


    public boolean validSEndTimeQ() {
        String s = end_time.getEditText().getText().toString();
        if (s.isEmpty()) {
            end_time.setError("correct the formate");
            return false;
        } else {
            end_time.setError(null);
        }


        return true;
    }


    public void upload(String title, String description, long strtTime, long endTime) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("assignment").child(crsId).child("details");
        final String ikey = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("title", title);
        hashMap.put("description", description);

        hashMap.put("prepare", "0");          ///prepare for access to quiz untill the qestion inserted
        hashMap.put("endTime", endTime);          ///invalid time for access to quiz its over
        hashMap.put("strtTime", strtTime);
        hashMap.put("dataKey", ikey);
        hashMap.put("publish", new Date().getTime());// publish date
        reference.child(ikey).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AssignmentCreateFontActivity.this, "Assignment create successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }



}
