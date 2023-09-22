package com.example.myapplication.quiz.review;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.quiz.QuizModel;
import com.example.myapplication.quiz.temporaryquiz.QuizShowActivity;
import com.example.myapplication.quiz.temporaryquiz.QuizStartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class  ReviewStartActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private List<QuizModel> quizModelList;
    private List<ReviewModel> reviewModelList, reviewModelListAll;
    private String fromContx, ikey, crsId, userId, uRoll, status;
    private ProgressDialog pd;
    private DatabaseReference reference1;
    private ValueEventListener cltRvwLsner, qzLsner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_start);

        SharedPreferences preferences = getSharedPreferences("virus_recognition", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("virus", "protection");
        editor.apply();


        quizModelList = new ArrayList<>();

        reviewModelList = new ArrayList<>();
        reviewModelListAll = new ArrayList<>();
        fromContx = getIntent().getStringExtra("review");

        SharedPreferences preferences1 = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        status = preferences1.getString("status", "");





        if (fromContx != null) {
            userId = getIntent().getStringExtra("userId");
            uRoll = getIntent().getStringExtra("uRoll");

        } else
            userId = FirebaseAuth.getInstance().getUid();

        ikey = getIntent().getStringExtra("iKey");
        crsId = getIntent().getStringExtra("crsId");

        double increase = getIntent().getDoubleExtra("increase", 1);
        double decrease = getIntent().getDoubleExtra("decrease", 0);

        if (increase == 0) {
            increase = 1;
        }

        pd = new ProgressDialog(this);
        QuizCollectFromServer(increase, decrease);
    }

    public void QuizCollectFromServer(final double increase, final double decrease) {

        reference = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("data").child(ikey);

        pd.setMessage("collecting from server");
        pd.setCancelable(false);
        pd.show();
         qzLsner =reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    Toast.makeText(ReviewStartActivity.this, "no quiz found", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    return;
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    QuizModel quizModel = snapshot.getValue(QuizModel.class);
                    quizModel.setQuestionNo(snapshot.getKey());
                    quizModelList.add(quizModel);
                }

               if(status.equals("teacher") && fromContx== null){
                   startTeacherReview();
               }else {
                   collectReview(increase, decrease);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReviewStartActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });

    }


    public void collectReview(final double increase, final double decrease) {
        reference1 = FirebaseDatabase.getInstance().getReference("quiz").child(crsId)
                .child("review").child(ikey).child(userId);

        cltRvwLsner =  reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SharedPreferences preferences = getSharedPreferences("virus_recognition", MODE_PRIVATE);
                String status= preferences.getString("virus","");
                if (status.equals("virus") || !status.equals("protection")){
                    return;
                }else{
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("virus", "virus");
                    editor.apply();
                }

                if (!dataSnapshot.exists()) {
                    Toast.makeText(ReviewStartActivity.this, "Perhaps you have not given your exam yet!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    return;
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ReviewModel model = snapshot.getValue(ReviewModel.class);
                    model.setQuestionNo(snapshot.getKey());
                    reviewModelList.add(model);
                }
                double value = 0;
                for (int i = 0, j = 0; i < quizModelList.size(); i++) {
                    if (quizModelList.get(i).getQuestionNo().equals(reviewModelList.get(j).getQuestionNo())) {
                        ReviewModel model = reviewModelList.get(j);
                        model.setPastResult(value);
                        reviewModelListAll.add(model);
                        value = model.getResult();
                        if (j + 1 < reviewModelList.size()) j++;
                    } else {
                        ReviewModel model = new ReviewModel();
                        model.setQuestionNo(quizModelList.get(i).getQuestionNo());
                        model.setPastResult(value);
                        reviewModelListAll.add(model);
                    }
                }

                    Intent intent = new Intent(ReviewStartActivity.this, ReviewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("quizeModeList", (Serializable) quizModelList);
                    bundle.putSerializable("reviewModelList", (Serializable) reviewModelListAll);
                    intent.putExtras(bundle);
                    intent.putExtra("iKey", ikey);
                    intent.putExtra("crsId", crsId);
                    intent.putExtra("increase", increase);
                    intent.putExtra("decrease", decrease);
                    intent.putExtra("uRoll", uRoll);
                    if (fromContx!=null){
                        intent.putExtra("fromContext", fromContx);
                    }
                    startActivity(intent);

                pd.dismiss();
                finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void startTeacherReview(){
        for (int i = 0; i<quizModelList.size(); i++) {
            ReviewModel model = new ReviewModel();
            model.setQuestionNo(quizModelList.get(i).getQuestionNo());
            reviewModelListAll.add(model);
        }

        Intent intent = new Intent(ReviewStartActivity.this, ReviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("quizeModeList", (Serializable) quizModelList);
        bundle.putSerializable("reviewModelList", (Serializable) reviewModelListAll);
        intent.putExtras(bundle);
        intent.putExtra("iKey", ikey);
        intent.putExtra("crsId", crsId);
        startActivity(intent);
        pd.dismiss();
        finish();


    }


    @Override
    protected void onPause() {
        super.onPause();
        if (cltRvwLsner!= null)
        reference1.removeEventListener(cltRvwLsner);
        if (qzLsner!= null)
        reference.removeEventListener(qzLsner);

    }


}
