package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.myapplication.R;

public class SettngActivity extends AppCompatActivity {
  private Switch trnswitchAssign,  trnswitchQuiz, trnswitchSlide;
  private boolean isTrnAssign, isTrnQuiz, isTrnSlide;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settng);

        trnswitchAssign = findViewById(R.id.switch_trans_assignment);
        trnswitchQuiz = findViewById(R.id.switch_trans_quiz);
        trnswitchSlide = findViewById(R.id.switch_trans_slide);

        SharedPreferences trans_memory = getSharedPreferences("transparency", MODE_PRIVATE);
         isTrnAssign = trans_memory.getBoolean("trans_assign",false);
         isTrnQuiz = trans_memory.getBoolean("trans_quiz",false);
         isTrnSlide = trans_memory.getBoolean("trans_slide",false);







        trnswitchAssign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 SharedPreferences trans_memory = getSharedPreferences("transparency", MODE_PRIVATE);
                 SharedPreferences.Editor trans_editor = trans_memory.edit();
                 if (isChecked){
                     trans_editor.putBoolean("trans_assign",true);
                 }else{
                     trans_editor.putBoolean("trans_assign",false);
                 }
                 trans_editor.apply();
             }
         });


        trnswitchQuiz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences trans_memory = getSharedPreferences("transparency", MODE_PRIVATE);
                SharedPreferences.Editor trans_editor = trans_memory.edit();
                if (isChecked){
                    trans_editor.putBoolean("trans_quiz",true);
                }else{
                    trans_editor.putBoolean("trans_quiz",false);
                }
                trans_editor.apply();
            }
        });



        trnswitchSlide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences trans_memory = getSharedPreferences("transparency", MODE_PRIVATE);
                SharedPreferences.Editor trans_editor = trans_memory.edit();
                if (isChecked){
                    trans_editor.putBoolean("trans_slide",true);
                }else{
                    trans_editor.putBoolean("trans_slide",false);
                }
                trans_editor.apply();
            }
        });





       setSwitch();

    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }



    void setSwitch(){

        if (isTrnAssign){
            trnswitchAssign.setChecked(true);
        }else{
            trnswitchAssign.setChecked(false);
        }
        // slide active or not
        if (isTrnSlide){
            trnswitchSlide.setChecked(true);
        }else{
            trnswitchSlide.setChecked(false);
        }

        // quiz active or not
        if (isTrnQuiz){
            trnswitchQuiz.setChecked(true);
        }else{
            trnswitchQuiz.setChecked(false);
        }

    }
}
