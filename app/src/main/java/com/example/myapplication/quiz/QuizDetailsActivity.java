package com.example.myapplication.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class QuizDetailsActivity extends AppCompatActivity {
    private TextView title, description, publish, startTime, endTime, increseTxt,  duration, totalQsn;
    private ImageView identifySubmit;
    private String crsId, dataKey;
    private DateUtils dateUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_details);

        title = findViewById(R.id.title);
        description = findViewById(R.id.describtion);
        publish = findViewById(R.id.publish_date);
        startTime = findViewById(R.id.start_time);
        endTime = findViewById(R.id.end_time);
        duration = findViewById(R.id.duration);
        totalQsn = findViewById(R.id.amountQsn);
        increseTxt = findViewById(R.id.increase_decrease_txt);

        identifySubmit = findViewById(R.id.identify_submit_img);


        crsId = getIntent().getStringExtra("crsId");
        dataKey = getIntent().getStringExtra("iKey");



        readQuizDetails();

    }

    public void readQuizDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("details").child(dataKey);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (!dataSnapshot.exists())
                   return;

                    QuizDetailsModel model =dataSnapshot.getValue(QuizDetailsModel.class);
                    title.setText(model.getTitle());
                    description.setText(model.getDescription());
                    publish.setText(dateUtils.dateFromLong(model.getPublish()));
                    startTime.setText(dateUtils.dateFromLong(model.getStrtTime()));
                    endTime.setText(dateUtils.dateFromLong(model.getEndTime()));
                    duration.setText(model.getDuration()+"");
                    totalQsn.setText(model.getTotalQsn());
                    increseTxt.setText("(++" + model.getIncrease() + ") , ( --" + model.getDecrease() + " )");

                    setColor(model.getStrtTime(), model.getEndTime());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    void setColor(long startTime, long endTime){
        final boolean validExamOrNot = validExamck(startTime, endTime);
        if (validExamOrNot) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Drawable col = getResources().getDrawable(R.drawable.custom_button);
                identifySubmit.setBackground(col);

            } else {
                int grn =getResources().getColor(R.color.greenDark);
                identifySubmit.setBackgroundColor(grn);
            }

        } else if (ckState(endTime)) {


            Drawable col = getResources().getDrawable(R.drawable.state_yellow);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
               identifySubmit.setBackground(col);
            } else {
                int grn = getResources().getColor(R.color.orangeDark);
                identifySubmit.setBackgroundColor(grn);
            }
        } else {
            Drawable col = getResources().getDrawable(R.drawable.state_red);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
               identifySubmit.setBackground(col);
            } else {
                int grn =getResources().getColor(R.color.colorAccent);
                identifySubmit.setBackgroundColor(grn);
            }
        }
    }



    public boolean ckState(long times) {
        Calendar calendar = Calendar.getInstance();
        long curentmils = calendar.getTimeInMillis();
        if (curentmils < times) {
            return true;
        } else return false;
    }


    public boolean validExamck(long start, long end) {
        Calendar calender = Calendar.getInstance();
        long curentmilis = calender.getTimeInMillis();
        if (curentmilis > start && curentmilis < end) {
            return true;
        } else return false;
    }
}
