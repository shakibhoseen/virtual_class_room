package com.example.myapplication.quiz.temporaryquiz;

import androidx.annotation.NonNull;
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
import com.example.myapplication.quiz.QuizDetailsModel;
import com.example.myapplication.quiz.QuizFontActivity;
import com.example.myapplication.quiz.QuizInsertActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class QuizModificationActivity extends AppCompatActivity {

    private TextInputLayout title, describtion, duration, totalQsn, start_time, end_time;
    private Button implement, saveReturn;
    private boolean ckBtnprssed;
    private TextView start_timeTx;
    private String crsId;
    private String iKey;
    DateUtils dateUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_modification);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        title = findViewById(R.id.quiz_title_id);
        describtion = findViewById(R.id.describtion);
        duration = findViewById(R.id.duration);
        totalQsn = findViewById(R.id.total_question);
        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);

        dateUtils = new DateUtils();


        crsId = getIntent().getStringExtra("crsId");
        iKey = getIntent().getStringExtra("iKey");

        implement = findViewById(R.id.implementBtn);
        saveReturn = findViewById(R.id.saveBtn);

        implement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ckBtnprssed = true;
               beforeUpdate();

                //
            }
        });

        saveReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ckBtnprssed =false;
                beforeUpdate();
            }
        });

        readQuizTitle();

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

    public boolean validDuration() {
        String s = duration.getEditText().getText().toString();
        if (s.isEmpty()) {
            duration.setError("please fill the batch name");
            return false;
        } else {
            duration.setError(null);
        }


        return true;
    }

    public boolean validTotalQ() {
        String s = totalQsn.getEditText().getText().toString();
        if (s.isEmpty()) {
            totalQsn.setError("please fill the batch name");
            return false;
        } else {
            totalQsn.setError(null);
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

    public void upload(String title, String description, long duration, final String totalQsn, long strtTime, long endTime) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("details");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("title", title);

        hashMap.put("description", description);
        hashMap.put("duration", duration);         // time for qsn
        hashMap.put("totalQsn", totalQsn);        //no of question
        hashMap.put("prepare", "0");          ///prepare for access to quiz untill the qestion inserted
        hashMap.put("endTime", endTime);          ///invalid time for access to quiz its over
        hashMap.put("strtTime", strtTime);
       // hashMap.put("publish", new Date().getTime());// publish date
        reference.child(iKey).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (ckBtnprssed){
                    Intent intent = new Intent(QuizModificationActivity.this, QuizInsertActivity.class);
                    intent.putExtra("total", totalQsn);
                    intent.putExtra("crsId", crsId);
                    intent.putExtra("ikey", iKey);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(QuizModificationActivity.this, "successfully updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }



    public void beforeUpdate(){
        if (!validtitle() | !validDescrb() | !validDuration() | !validTotalQ() | !validSEndTimeQ() | !validStartTimeQ()) {
            return;
        }
        String title_s = title.getEditText().getText().toString();
        String description_s = describtion.getEditText().getText().toString();
        String duration_s = duration.getEditText().getText().toString();
        long duration = Long.parseLong(duration_s);
        String totalQsn_s = totalQsn.getEditText().getText().toString();
        long startTime = dateUtils.transform(start_time.getEditText().getText().toString());
        long endTime = dateUtils.transform(end_time.getEditText().getText().toString());
        if (endTime<startTime){
            end_time.setError("last date not smaller than start time");
            return;
        }
        upload(title_s, description_s, duration, totalQsn_s, startTime, endTime);
    }


    public  void readQuizTitle(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("details").child(iKey);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                QuizDetailsModel model = dataSnapshot.getValue(QuizDetailsModel.class);
                title.getEditText().setText(model.getTitle());
                describtion.getEditText().setText(model.getDescription());
                totalQsn.getEditText().setText(model.getTotalQsn());
                String s = String.valueOf(model.getDuration());
                duration.getEditText().setText(s);
                end_time.getEditText().setText(dateUtils.feedback(model.getEndTime()));
                start_time.getEditText().setText(dateUtils.feedback(model.getStrtTime()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
