package com.example.myapplication.quiz.review;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.quiz.QuizModel;

import java.util.List;

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener {
    private RadioButton radioBtnA, radioBtnB, radioBtnC, radioBtnD, radioBtn, radioDeflt;
    private TextView questionTxt, decionCorrectTxt, decisionWrongTxt, decisionForget, toolbarTxt, scoreRviewTxt, rollTxt;
    private Button previousBtn, nextBtn;
    private List<QuizModel> quizModelList;
    private List<ReviewModel> reviewModelList;
    private int position = 0;
    private boolean isTeacher;

    private ColorStateList correctNotation, dfault, wrong;
    private double increase, decrease;
    String fromAllReview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        SharedPreferences preferences = getSharedPreferences("virus_recognition", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("virus", "virus");
        editor.apply();
        SharedPreferences preferences1 = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String status = preferences1.getString("status", "");
        fromAllReview = getIntent().getStringExtra("fromContext");

        if (status.equals("teacher")){
            isTeacher = true;
        }else {
            isTeacher = false;
        }





        radioBtnA = findViewById(R.id.radioBtnAId);
        radioBtnB = findViewById(R.id.radioBtnBId);
        radioBtnC = findViewById(R.id.radioBtnCId);
        radioBtnD = findViewById(R.id.radioBtnDId);
        radioDeflt = findViewById(R.id.radioBtnDefltId);
        questionTxt = findViewById(R.id.questionTxId);
        previousBtn = findViewById(R.id.previous_review);
        nextBtn = findViewById(R.id.nextBtnId);
        decionCorrectTxt = findViewById(R.id.toolbar_decision_correct_txt);
        decisionWrongTxt = findViewById(R.id.toolbar_decision_wrong_txt);
        decisionForget = findViewById(R.id.toolbar_decision_forget_txt);
        toolbarTxt = findViewById(R.id.toolbar_txt);
        scoreRviewTxt = findViewById(R.id.score_review_id);
        rollTxt = findViewById(R.id.roll_view_id);

        Bundle bundle = getIntent().getExtras();
        // quizModelList = new ArrayList<>();
        quizModelList = (List<QuizModel>) bundle.getSerializable("quizeModeList");
        reviewModelList = (List<ReviewModel>) bundle.getSerializable("reviewModelList");
        increase = getIntent().getDoubleExtra("increase", 1);
        decrease = getIntent().getDoubleExtra("decrease", 0);

        if (quizModelList.size()<=1){
            nextBtn.setVisibility(View.GONE);
        }

        String uRoll = getIntent().getStringExtra("uRoll");
        if (uRoll != null){
            rollTxt.setVisibility(View.VISIBLE);
            rollTxt.setText(uRoll);
        }


        nextBtn.setOnClickListener(this);
        previousBtn.setOnClickListener(this);


        createColorNotation();


        if (savedInstanceState == null)
           quizDistriBution();

    }


    public void quizDistriBution() {

        radioBtnA.setBackgroundResource(0);
        radioBtnB.setBackgroundResource(0);
        radioBtnC.setBackgroundResource(0);
        radioBtnD.setBackgroundResource(0);
        if (Build.VERSION.SDK_INT >= 21) {
            radioBtnA.setButtonTintList(dfault);
            radioBtnC.setButtonTintList(dfault);
            radioBtnD.setButtonTintList(dfault);
            radioBtnB.setButtonTintList(dfault);
        }

        String optionA, optionB, optionC, optionD, qsnAns, qsnMainTxt;
        qsnMainTxt = quizModelList.get(position).getQuestion();
        qsnAns = quizModelList.get(position).getAns();
        optionA = quizModelList.get(position).getOptionA();
        optionB = quizModelList.get(position).getOptionB();
        optionC = quizModelList.get(position).getOptionC();
        optionD = quizModelList.get(position).getOptionD();


        int p = position + 1;
        toolbarTxt.setText("Question " + p + " of " + quizModelList.size());
        questionTxt.setText(qsnMainTxt);
        radioBtnA.setText(optionA);
        radioBtnB.setText(optionB);
        radioBtnC.setText(optionC);
        radioBtnD.setText(optionD);


        setColorBackground(qsnAns, optionA, optionB, optionC, optionD, true, false);

        if (reviewModelList.get(position).getQuestionNo() != null) {
            if (reviewModelList.get(position).getUserAns() != null) {
                if (reviewModelList.get(position).getUserAns().equals(quizModelList.get(position).getAns())) {
                    setColorBackground(reviewModelList.get(position).getUserAns(), optionA, optionB, optionC, optionD, false, true);
                    decionCorrectTxt.setVisibility(View.VISIBLE);
                    decisionWrongTxt.setVisibility(View.GONE);
                    decisionForget.setVisibility(View.GONE);
                    scoreRviewTxt.setText(new StringBuilder().append("Score : ").append(reviewModelList.get(position).getResult())
                            .append(" = ").append(reviewModelList.get(position).getPastResult()).append(" + ").append(increase).toString());

                } else if (reviewModelList.get(position).getUserAns().equals("empty choose")) {
                    Toast.makeText(this, "you don't selected any option but confirmed for this question", Toast.LENGTH_SHORT).show();
                    decionCorrectTxt.setVisibility(View.GONE);
                    decisionWrongTxt.setVisibility(View.VISIBLE);
                    decisionForget.setVisibility(View.GONE);
                    radioDeflt.setChecked(true);
                    scoreRviewTxt.setText(new StringBuilder().append("Score : ").append(reviewModelList.get(position).getResult())
                            .append(" = ").append(reviewModelList.get(position).getPastResult()).append(" - ").append(decrease).toString());
                } else {
                    setColorBackground(reviewModelList.get(position).getUserAns(), optionA, optionB, optionC, optionD, false, false);
                    decionCorrectTxt.setVisibility(View.GONE);
                    decisionWrongTxt.setVisibility(View.VISIBLE);
                    decisionForget.setVisibility(View.GONE);
                    scoreRviewTxt.setText(new StringBuilder().append("Score : ").append(reviewModelList.get(position).getResult())
                            .append(" = ").append(reviewModelList.get(position).getPastResult()).append(" - ").append(decrease).toString());
                }

            } else {

                if (isTeacher){
                    if (fromAllReview == null){
                        decionCorrectTxt.setVisibility(View.GONE);
                        decisionWrongTxt.setVisibility(View.GONE);
                        decisionForget.setVisibility(View.GONE);
                        scoreRviewTxt.setVisibility(View.GONE);
                    }else {
                        decionCorrectTxt.setVisibility(View.GONE);
                        decisionWrongTxt.setVisibility(View.GONE);
                        decisionForget.setVisibility(View.VISIBLE);
                        radioDeflt.setChecked(true);
                        scoreRviewTxt.setText(new StringBuilder().append("Score : ").append(reviewModelList.get(position).getPastResult())
                                .toString());
                    }
                }else {
                    decionCorrectTxt.setVisibility(View.GONE);
                    decisionWrongTxt.setVisibility(View.GONE);
                    decisionForget.setVisibility(View.VISIBLE);
                    radioDeflt.setChecked(true);
                    scoreRviewTxt.setText(new StringBuilder().append("Score : ").append(reviewModelList.get(position).getPastResult())
                            .toString());
                }
            }

        } else {
            Toast.makeText(this, "something went wrong. please Try again", Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nextBtnId) {
            position++;
            previousBtn.setVisibility(View.VISIBLE);
            if (position + 1 == quizModelList.size())
                nextBtn.setVisibility(View.GONE);
            quizDistriBution();
            return;
        } else if (v.getId() == R.id.previous_review) {
            position--;
            if (position == 0) {
                previousBtn.setVisibility(View.GONE);
            }
            nextBtn.setVisibility(View.VISIBLE);
            quizDistriBution();
            return;
        }
    }


    public void createColorNotation() {
        correctNotation = new ColorStateList(
                new int[][]{

                        new int[]{-android.R.attr.state_enabled}, //disabled
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[]{

                        getResources().getColor(R.color.green_personal)//disabled
                        , getResources().getColor(R.color.green_personal)//enabled

                }
        );
        dfault = new ColorStateList(
                new int[][]{

                        new int[]{-android.R.attr.state_enabled}, //disabled
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[]{

                        getResources().getColor(R.color.white_personal)//disabled
                        , getResources().getColor(R.color.white_personal)//enabled

                }
        );

        wrong = new ColorStateList(
                new int[][]{

                        new int[]{-android.R.attr.state_enabled}, //disabled
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[]{

                        getResources().getColor(R.color.red_personal)//disabled
                        , getResources().getColor(R.color.red_personal)//enabled

                }
        );
    }


    public void setColorBackground(String ans, String optionA, String optionB, String optionC, String optionD, boolean main, boolean setselectTint) {

        if (ans.equals(optionA)) {
            if (!main) {
                if (setselectTint) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        radioBtnA.setButtonTintList(correctNotation);
                    }

                } else {
                    if (Build.VERSION.SDK_INT >= 21) {
                        radioBtnA.setButtonTintList(wrong);
                    }
                    radioBtnA.setBackgroundColor(getResources().getColor(R.color.red_personal_bright));
                }

                radioBtnA.setChecked(true);


            } else {
                radioBtnA.setBackgroundColor(getResources().getColor(R.color.green_personal_bright));

            }
        } else if (ans.equals(optionB)) {
            if (!main) {
                if (setselectTint) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        radioBtnB.setButtonTintList(correctNotation);
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= 21) {
                        radioBtnB.setButtonTintList(wrong);
                    }
                    radioBtnB.setBackgroundColor(getResources().getColor(R.color.red_personal_bright));
                }
                radioBtnB.setChecked(true);

            } else {
                radioBtnB.setBackgroundColor(getResources().getColor(R.color.green_personal_bright));

            }
        } else if (ans.equals(optionC)) {
            if (!main) {
                if (setselectTint) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        radioBtnC.setButtonTintList(correctNotation);
                    }

                } else {
                    if (Build.VERSION.SDK_INT >= 21) {
                        radioBtnC.setButtonTintList(wrong);
                    }
                    radioBtnC.setBackgroundColor(getResources().getColor(R.color.red_personal_bright));
                }

                radioBtnC.setChecked(true);


            } else {
                radioBtnC.setBackgroundColor(getResources().getColor(R.color.green_personal_bright));

            }
        } else if (ans.equals(optionD)) {
            if (!main) {
                if (setselectTint) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        radioBtnD.setButtonTintList(correctNotation);
                    }

                } else {
                    if (Build.VERSION.SDK_INT >= 21) {
                        radioBtnD.setButtonTintList(wrong);
                    }
                    radioBtnD.setBackgroundColor(getResources().getColor(R.color.red_personal_bright));
                }
                radioBtnD.setChecked(true);
            } else {
                radioBtnD.setBackgroundColor(getResources().getColor(R.color.green_personal_bright));

            }
        }
    }


    @Override
    public void onBackPressed() {
         finish();
        super.onBackPressed();


    }



  /*  @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

       outState.putInt("position", position); // Saving the Variable theWord
       // outState.putStringArrayList("fiveDefns", fiveDefns); // Saving the ArrayList fiveDefns
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        Toast.makeText(this, ""+position, Toast.LENGTH_SHORT).show();
        position = savedInstanceState.getInt("position"); // Restoring theWord
       // fiveDefns = savedInstanceState.getStringArrayList("fiveDefns"); //Restoring fiveDefns
    }*/


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", position);

        outState.putInt("previous",previousBtn.getVisibility());
        outState.putInt("next",nextBtn.getVisibility());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position =  savedInstanceState.getInt("position");
        int p =  savedInstanceState.getInt("previous");
        previousBtn.setVisibility(p);
        nextBtn.setVisibility(savedInstanceState.getInt("next"));
        quizDistriBution();
    }
}
