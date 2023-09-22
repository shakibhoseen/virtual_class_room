package com.example.myapplication.quiz.temporaryquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.quiz.QuizModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuizStartActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private List<QuizModel> quizModelList;

    private static final long START_TIME_MILIS = 30000;
    private String  ikey, crsId, userId;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_start);

        quizModelList = new ArrayList<>();

        SharedPreferences preferences = getSharedPreferences("virus_recognition", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("virus", "virus");
        editor.apply();


        userId = FirebaseAuth.getInstance().getUid();

        ikey = getIntent().getStringExtra("iKey");
        crsId = getIntent().getStringExtra("crsId");
        long duration = getIntent().getLongExtra("duration", START_TIME_MILIS);
        double increase = getIntent().getDoubleExtra("increase", 1);
        double decrease = getIntent().getDoubleExtra("decrease", 0);


        pd = new ProgressDialog(this);
        QuizCollectFromServer(duration, increase, decrease);
    }


    public void QuizCollectFromServer(final long duration, final double increase, final double decrease) {

        reference = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("data").child(ikey);

        pd.setMessage("collecting from server");
        pd.setCancelable(false);
        pd.show();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(QuizStartActivity.this, "no quiz found", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    return;
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    QuizModel quizModel = snapshot.getValue(QuizModel.class);
                    quizModel.setQuestionNo(snapshot.getKey());
                    quizModelList.add(quizModel);
                }

                    Intent intent = new Intent(QuizStartActivity.this, QuizShowActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("quizeModeList1", (Serializable) quizModelList);
                    intent.putExtras(bundle);
                    intent.putExtra("duration", duration);
                    intent.putExtra("increase", increase);
                    intent.putExtra("decrease", decrease);
                    intent.putExtra("iKey", ikey);
                    intent.putExtra("crsId", crsId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    pd.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuizStartActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });

    }



}
