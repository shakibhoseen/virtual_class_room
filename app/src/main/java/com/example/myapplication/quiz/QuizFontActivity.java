package com.example.myapplication.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import java.util.Date;
import java.util.HashMap;

public class QuizFontActivity extends AppCompatActivity {
    private TextInputLayout title, describtion, duration, totalQsn, start_time, end_time, correctScore, wrongScore;
    private Button implement;
    private CheckBox numberCheckBox;
    private LinearLayout line1;
    private TextView start_timeTx;
    private String crsId;
    DateUtils dateUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_font);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
/// insert quiz details in here

        dateUtils = new DateUtils();

        title = findViewById(R.id.quiz_title_id);
        describtion = findViewById(R.id.describtion);
        duration = findViewById(R.id.duration);
        totalQsn = findViewById(R.id.total_question);
        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);
        correctScore = findViewById(R.id.correct_score_input);
        wrongScore = findViewById(R.id.wrong_score_input);
        numberCheckBox = findViewById(R.id.number_check_box);
        line1 = findViewById(R.id.line1);

        crsId = getIntent().getStringExtra("crsId");

        implement = findViewById(R.id.implementBtn);

        implement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (numberCheckBox.isChecked()){
                    if (!validCorrectPoint() | !validWrongPoint()){
                        return;
                    }
                }

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

                //
            }
        });

        numberCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberCheckBox.isChecked()){
                    line1.setVisibility(View.VISIBLE);
                }else {
                    line1.setVisibility(View.GONE);
                }
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


    public boolean validCorrectPoint() {
        String s = correctScore.getEditText().getText().toString();
        if (s.isEmpty()) {
            correctScore.setError("please fill the correct point");
            return false;
        } else {
            correctScore.setError(null);
        }


        return true;
    }

    public boolean validWrongPoint() {
        String s = wrongScore.getEditText().getText().toString();
        if (s.isEmpty()) {
            wrongScore.setError("please fill the wrong point");
            return false;
        } else {
            wrongScore.setError(null);
        }


        return true;
    }


    public void upload(String title, String description, long duration, final String totalQsn, long strtTime, long endTime) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("details");
        final String ikey = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("title", title);

        hashMap.put("description", description);
        hashMap.put("duration", duration);         // time for qsn
        hashMap.put("totalQsn", totalQsn);        //no of question
        hashMap.put("prepare", "0");          ///prepare for access to quiz untill the qestion inserted
        hashMap.put("endTime", endTime);          ///invalid time for access to quiz its over
        hashMap.put("strtTime", strtTime);
        if (numberCheckBox.isChecked()){
            String correctTxt = correctScore.getEditText().getText().toString();
            String wrongTxt = wrongScore.getEditText().getText().toString();
            float corectFloat = Float.parseFloat(correctTxt);
            float wrongFloat = Float.parseFloat(wrongTxt);

            hashMap.put("increase", corectFloat);
            hashMap.put("decrease", wrongFloat);
        }

        hashMap.put("dataKey", ikey);
        hashMap.put("publish", new Date().getTime());// publish date
        reference.child(ikey).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(QuizFontActivity.this, QuizInsertActivity.class);
                intent.putExtra("total", totalQsn);
                intent.putExtra("crsId", crsId);
                intent.putExtra("ikey", ikey);
                startActivity(intent);
                finish();
            }
        });
    }


}
