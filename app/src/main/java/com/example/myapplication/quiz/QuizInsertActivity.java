package com.example.myapplication.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class QuizInsertActivity extends AppCompatActivity implements View.OnClickListener {
    private MaterialEditText questionEdTx, optionAEdTx, optionBEdTx, optionCEdTx, optionDEdtx;
    private Button questionDoneBtn, qsnPrevBtn, qsnNextBtn;
    private TextView questionPtrnNo, questionNoSrt;
    private TextInputLayout correctAns;
    DatabaseReference reference;
    String crsId, ikey;
    int variable;
    int ck = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_insert);
        /// this is data of quiz insert

        questionEdTx = findViewById(R.id.questionTxId);
        optionAEdTx = findViewById(R.id.optionTxAId);
        optionBEdTx = findViewById(R.id.optionTxBId);
        optionCEdTx = findViewById(R.id.optionTxCId);
        optionDEdtx = findViewById(R.id.optionTxDId);
        questionDoneBtn = findViewById(R.id.questionDoneId);
        questionPtrnNo = findViewById(R.id.questionPtrnNo);
        questionNoSrt = findViewById(R.id.question_NoId);
        qsnNextBtn = findViewById(R.id.qsnNxtBtn);
        qsnPrevBtn = findViewById(R.id.qsnPrevBtn);
        correctAns = findViewById(R.id.correctAns);

        String r = getIntent().getStringExtra("total");


        if (r != null) {
            variable = Integer.parseInt(r);
        } else
            Toast.makeText(this, "something went wrong try again later", Toast.LENGTH_SHORT).show();
        crsId = getIntent().getStringExtra("crsId");
        ikey = getIntent().getStringExtra("ikey");

        qsnNextBtn.setVisibility(View.GONE);
        qsnPrevBtn.setVisibility(View.GONE);

        questionDoneBtn.setOnClickListener(this);
        qsnPrevBtn.setOnClickListener(this);
        qsnNextBtn.setOnClickListener(this);

        questionPtrnNo.setText("Question for no: " + ck);
        questionNoSrt.setText("Q." + ck);

        refresh();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.questionDoneId) {


            if (ck <= variable) {
                insertData();

            } else {
                Toast.makeText(this, "you reach the maximum amount of your question", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.qsnNxtBtn) {
            if (ck < variable) {
                ck++;
                refresh();
            }


        } else if (v.getId() == R.id.qsnPrevBtn) {
            if (ck > 1) {
                ck--;
                refresh();
            }


        }

    }


    public void insertData() {
        String questionTx = questionEdTx.getText().toString();
        String optionATx = optionAEdTx.getText().toString();
        String optionBtx = optionBEdTx.getText().toString();
        String optionCTx = optionCEdTx.getText().toString();
        String optionDTx = optionDEdtx.getText().toString();
        String ans = correctAns.getEditText().getText().toString().trim().toLowerCase();


        if (TextUtils.isEmpty(questionTx) || TextUtils.isEmpty(optionATx) || TextUtils.isEmpty(optionBtx) || TextUtils.isEmpty(optionCTx) || TextUtils.isEmpty(optionDTx)) {
            Toast.makeText(QuizInsertActivity.this, "All fields are Required", Toast.LENGTH_SHORT).show();
        }else if (!validCorrectAns()){
            return;
        }
            else {
            String s = identifyText(ans);

            String Q = "Q" + ck;

            reference = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("data").child(ikey).child(Q);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("question", questionTx);
            hashMap.put("optionA", optionATx);
            hashMap.put("optionB", optionBtx);
            hashMap.put("optionC", optionCTx);
            hashMap.put("optionD", optionDTx);
            hashMap.put("ans", s);
            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(QuizInsertActivity.this, "success", Toast.LENGTH_SHORT).show();
                        if (ck != variable) {
                            ck++;
                            questionEdTx.setText("");
                            optionAEdTx.setText("");
                            optionBEdTx.setText("");
                            optionCEdTx.setText("");
                            optionDEdtx.setText("");
                            correctAns.getEditText().setText("");
                        }else{
                            finish();
                        }

                         refresh();
                        questionPtrnNo.setText("Question for no: " + ck);
                        questionNoSrt.setText("Q." + ck);
                        qsnPrevBtn.setVisibility(View.VISIBLE);

                    } else
                        Toast.makeText(QuizInsertActivity.this, "failed try again", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


    public void refresh() {
        questionEdTx.setText("");
        optionAEdTx.setText("");
        optionBEdTx.setText("");
        optionCEdTx.setText("");
        optionDEdtx.setText("");
        correctAns.getEditText().setText("");

        String input = "Q" + ck;
        questionPtrnNo.setText("Question for no: " + ck);
        questionNoSrt.setText("Q." + ck);
        DatabaseReference databaseReference = reference = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("data").child(ikey).child(input);
        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        QuizModel model = dataSnapshot.getValue(QuizModel.class);

                        questionEdTx.setText(model.getQuestion());
                        optionAEdTx.setText(model.getOptionA());
                        optionBEdTx.setText(model.getOptionB());
                        optionCEdTx.setText(model.getOptionC());
                        optionDEdtx.setText(model.getOptionD());
                        if (model.getOptionA().equals(model.getAns())){
                            correctAns.getEditText().setText("A");
                        }else  if (model.getOptionB().equals(model.getAns())){
                            correctAns.getEditText().setText("B");
                        }else  if (model.getOptionC().equals(model.getAns())){
                            correctAns.getEditText().setText("C");
                        }else
                          correctAns.getEditText().setText("D");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if (ck <= 1) {
            qsnPrevBtn.setVisibility(View.GONE);
        } else {
            qsnPrevBtn.setVisibility(View.VISIBLE);
        }
        if (ck >= variable) {
            qsnNextBtn.setVisibility(View.GONE);
        } else {
            qsnNextBtn.setVisibility(View.VISIBLE);
        }
    }


    public boolean validCorrectAns() {
        String s = correctAns.getEditText().getText().toString().trim().toLowerCase();
        if (s.equals("")) {
            correctAns.setError("please choose correct option");
            return false;
        } else if (!s.equals("a") && !s.equals("b") && !s.equals("c") && !s.equals("d")) {
            correctAns.setError("please choose correct option");
            return false;
        } else {
            correctAns.setError(null);
            return true;
        }

    }


    public String identifyText(String s1) {
        if (s1.equals("a")) {
            return optionAEdTx.getText().toString();
        } else if (s1.equals("b")) {
            return optionBEdTx.getText().toString();
        } else if (s1.equals("c")) {
            return optionCEdTx.getText().toString();
        } else
            return optionDEdtx.getText().toString();

    }
}
