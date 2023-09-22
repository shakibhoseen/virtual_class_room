package com.example.myapplication.quiz.temporaryquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.example.myapplication.R;

import com.example.myapplication.quiz.QuizModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class QuizShowActivity extends AppCompatActivity implements View.OnClickListener {
    private RadioButton radioBtnA, radioBtnB, radioBtnC, radioBtnD, radioBtn, radioDeflt;
    private RadioGroup radioGroup;
    private TextView questionTxt, coundownTxt, toolbarTimerTxt, toolbarTxt;
    private Button submit, nextBtn;
    private static final long START_TIME_MILIS = 30000;
    private long duration;
    private long mTieLeftMillis;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning = false;
    private int position = 0;
    private double result = 0;
    private double increase, decrease;



    List<QuizModel> quizModelList;
    String dataKey, crsId, userid;



    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_show);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        duration = getIntent().getLongExtra("duration", START_TIME_MILIS);
        increase = getIntent().getDoubleExtra("increase", 1);
        decrease = getIntent().getDoubleExtra("decrease", 0);

        if (increase==0){
            increase =1;
        }

        mTieLeftMillis = duration;

        radioBtnA = findViewById(R.id.radioBtnAId);
        radioBtnB = findViewById(R.id.radioBtnBId);
        radioBtnC = findViewById(R.id.radioBtnCId);
        radioBtnD = findViewById(R.id.radioBtnDId);
        radioGroup = findViewById(R.id.radioGroupId);
        radioDeflt = findViewById(R.id.radioBtnDefltId);
        questionTxt = findViewById(R.id.questionTxId);
        submit = findViewById(R.id.submitId);
        nextBtn = findViewById(R.id.nextBtnId);
        coundownTxt = findViewById(R.id.coundownId);
        toolbarTimerTxt = findViewById(R.id.toolbar_time_txt);
        toolbarTxt = findViewById(R.id.toolbar_txt);

        Bundle bundle1 = getIntent().getExtras();
        // quizModelList = new ArrayList<>();
        quizModelList = (List<QuizModel>) bundle1.getSerializable("quizeModeList1");

        crsId = getIntent().getStringExtra("crsId");
        dataKey = getIntent().getStringExtra("iKey");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null)
        userid = firebaseUser.getUid();
        else finish();


        if (quizModelList.size() != 0)
            quizDistriBution();
        if (quizModelList.size()==1){
            nextBtn.setVisibility(View.GONE);
        }
        nextBtn.setOnClickListener(this);

        startTimer();
        submit.setOnClickListener(this);

    }


    public void quizDistriBution() {
        int p = position + 1;
        toolbarTxt.setText("Question " + p + " of " + quizModelList.size());
        questionTxt.setText(quizModelList.get(position).getQuestion());
        radioBtnA.setText(quizModelList.get(position).getOptionA());
        radioBtnB.setText(quizModelList.get(position).getOptionB());
        radioBtnC.setText(quizModelList.get(position).getOptionC());
        radioBtnD.setText(quizModelList.get(position).getOptionD());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submitId) {
            submit.setEnabled(false);
            int radioi = radioGroup.getCheckedRadioButtonId();
            radioBtn = findViewById(radioi);
            String ans = quizModelList.get(position).getAns();

            if (ckUserAns(radioBtn.getText().toString(), ans) ) {
                result += increase;
            }else {
                result -= decrease;
            }

            storeReview(radioBtn.getText().toString(), ans, quizModelList.get(position).getQuestionNo());


            if (position + 1 == quizModelList.size()) {
                mCountDownTimer.cancel();
                finish();
            }
        }
        if (v.getId() == R.id.nextBtnId && nextBtn.getText().equals("next")) {
            // Toast.makeText(this, "" + quizModelList.size(), Toast.LENGTH_SHORT).show();
            position++;
            resetTimer();
            if (quizModelList.size() > position) {
                quizDistriBution();

            }
            if (position + 1 == quizModelList.size()) {

                nextBtn.setVisibility(View.GONE);
            }
        }
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTieLeftMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTieLeftMillis = millisUntilFinished;
                updateCountDownText();
                mTimerRunning = true;
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                if (position + 1 < quizModelList.size()) {
                    position++;
                    quizDistriBution();
                    resetTimer();

                } else {
                    finish();
                }

                if (position + 1 == quizModelList.size()) {

                    nextBtn.setVisibility(View.GONE);
                }

            }
        }.start();
    }

    private void resetTimer() {
        radioDeflt.setChecked(true);
        submit.setEnabled(true);
        if (mTimerRunning) {
            mCountDownTimer.cancel();
        }
        mTieLeftMillis = duration;
        updateCountDownText();
        startTimer();

    }




    private void updateCountDownText() {
        int minutes = (int) (mTieLeftMillis / 1000) / 60;
        int seconds = (int) (mTieLeftMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        toolbarTimerTxt.setText(timeLeftFormatted);
    }


    public boolean ckUserAns(String userAns, String correctAns) {

        if (userAns.equals(correctAns)) {
            return true;
        } else {
            return false;
        }
    }






    public void storeReview(String userAns, String correctAns, String question) {

        ExampleAsyncTask task = new ExampleAsyncTask(this, question,correctAns,userAns, new Date().getTime(),
                result, crsId, dataKey, userid);
        task.execute("hi");


    }



    private static class ExampleAsyncTask extends AsyncTask<String, String, String>{
        private WeakReference<QuizShowActivity> activityWeakReference;
        private String question, correctAns, userAns,crsId, dataKey, userid ;
        long publish;
        double  result;

        public ExampleAsyncTask(QuizShowActivity activity, String question,
                                String correctAns, String userAns, long publish, double result,String crsId,String dataKey,String userid) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.question = question;
            this.correctAns = correctAns;
            this.userAns = userAns;
            this.publish = publish;
            this.result = result;
            this.crsId = crsId;
            this.dataKey = dataKey;
            this.userid = userid;

        }

        @Override
        protected String doInBackground(String... strings) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("review").child(dataKey);

            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put("questionNo", question);
            hashMap.put("correctAns", correctAns);
            hashMap.put("userAns", userAns);
            hashMap.put("publish", new Date().getTime());
            hashMap.put("result", result);
            reference.child(userid).child(question).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    storeData(result);

                }
            });
            return null;
        }



        public void storeData(double result) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("collection").child(dataKey);

            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put("id", userid);
            hashMap.put("publish", new Date().getTime());
            hashMap.put("result", result);


            reference.child(userid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });

        }


    }



  /*  @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putString("theWord", theWord); // Saving the Variable theWord
        outState.putStringArrayList("fiveDefns", fiveDefns); // Saving the ArrayList fiveDefns
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

        theWord = savedInstanceState.getString("theWord"); // Restoring theWord
        fiveDefns = savedInstanceState.getStringArrayList("fiveDefns"); //Restoring fiveDefns
    }*/



}
