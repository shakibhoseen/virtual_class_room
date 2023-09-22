package com.example.myapplication.account.teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminMActivity extends AppCompatActivity {
    private LinearLayout linearLayout1, linearLayout2, linearLayout3;

    private CheckBox checkBoxPassword, checkBoxStuSecure, checkBoxTecSecure ;
    private TextInputLayout stuSecurity, tecSecurity, newPass, oldPass;
    private Button savePassBtn, saveStuSecBtn, saveTecSecBtn;
    private TextView stuTxtView, tecTxtView;
    private String  dataPassword, oldPassTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        linearLayout1 = findViewById(R.id.line1);
        checkBoxPassword = findViewById(R.id.checkbox_passId);

        linearLayout2 = findViewById(R.id.line2);
        linearLayout3 = findViewById(R.id.line3);


        stuSecurity = findViewById(R.id.student_sec_key_id);
        tecSecurity = findViewById(R.id.teacher_sec_key_id);
        savePassBtn = findViewById(R.id.pass_chang_btn);
        saveStuSecBtn = findViewById(R.id.change_student_btn_Id) ;
        saveTecSecBtn = findViewById(R.id.change_teacher_btn_Id);
        newPass = findViewById(R.id.new_pass_input_layout);
        oldPass = findViewById(R.id.old_pass_input_layout);
        checkBoxStuSecure = findViewById(R.id.checkbox_studentId);
        checkBoxTecSecure = findViewById(R.id.checkbox_teacherId);

        stuTxtView = findViewById(R.id.student_sec_txt_key_id);
        tecTxtView = findViewById(R.id.teacher_sec_txt_key_id);



        checkBoxPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxPassword.isChecked()){
                    linearLayout1.setVisibility(View.VISIBLE);
                }else {
                    linearLayout1.setVisibility(View.GONE);
                }
            }
        });



        checkBoxStuSecure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxStuSecure.isChecked()){
                    linearLayout2.setVisibility(View.VISIBLE);
                }else {
                    linearLayout2.setVisibility(View.GONE);
                }
            }
        });

        checkBoxTecSecure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxTecSecure.isChecked()){
                    linearLayout3.setVisibility(View.VISIBLE);
                }else {
                    linearLayout3.setVisibility(View.GONE);
                }
            }
        });


        savePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(AdminActivity.this, "Its disabled Right now, please contact with shakib", Toast.LENGTH_SHORT).show();
                if (!validOldPass() | !validNewPass()){
                    return;
                }else {
                    String pass = newPass.getEditText().getText().toString();
                   changePass("","", pass);
                }

            }
        });


        saveStuSecBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validStudentSecure()){
                    return;
                }
                String stuSecTxt = stuSecurity.getEditText().getText().toString();
                changePass(stuSecTxt,"", "");
            }
        });


        saveTecSecBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validTecherSecure()){
                    return;
                }
                String tecSecTxt = tecSecurity.getEditText().getText().toString();
                changePass("",tecSecTxt, "");

            }
        });

        linearLayout1.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.GONE);
        linearLayout3.setVisibility(View.GONE);

        loadsecurityKey();

    }




    public void loadsecurityKey(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("security");

        reference.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    SecureModel quizModel = snapshot.getValue(SecureModel.class);
                    stuTxtView.setText(quizModel.getStudent());
                    tecTxtView.setText(quizModel.getTeacher());
                    oldPassTxt = quizModel.getAdmin();
                    dataPassword = snapshot.getKey();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }





    public boolean validStudentSecure(){
        String s = stuSecurity.getEditText().getText().toString();
        if (s.isEmpty()){
            stuSecurity.setError("please fill student security code");
            return false;
        }else{
            stuSecurity.setError(null);
        }


        return true;
    }

    public boolean validTecherSecure(){

        String s = tecSecurity.getEditText().getText().toString();
        if (s.isEmpty()){
            tecSecurity.setError("please fill teacher security code");
            return false;
        }else{
            tecSecurity.setError(null);
        }


        return true;
    }

    public boolean validOldPass(){

        String s = oldPass.getEditText().getText().toString();
        if (s.isEmpty()){
            oldPass.setError("please fill the Old PassWord");
            return false;
        }else if(!s.equals(oldPassTxt)){
            oldPass.setError("Password dont match");
            return false;
        }
        else{
            oldPass.setError(null);
        }


        return true;
    }

    public boolean validNewPass(){

        String s = newPass.getEditText().getText().toString();
        if (s.isEmpty()){
            newPass.setError("please fill the new Password");
            return false;
        }else{
            newPass.setError(null);
        }


        return true;
    }


    public void changePass(String studentKey, String techerKey, String adminPass){
         if (dataPassword==null){
             Toast.makeText(this, "reference not found contact with shakib", Toast.LENGTH_SHORT).show();
             return;
         }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("security").child(dataPassword);

        HashMap<String, Object> hashMap = new HashMap<>();
        if (!studentKey.equals(""))
        hashMap.put("student", studentKey);
        if (!techerKey.equals(""))
        hashMap.put("teacher", techerKey);
        if (!adminPass.equals(""))
        hashMap.put("admin", adminPass);

        reference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AdminMActivity.this, "change successfully", Toast.LENGTH_SHORT).show();
                loadsecurityKey();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminMActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
