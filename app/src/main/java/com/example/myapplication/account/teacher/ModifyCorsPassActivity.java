package com.example.myapplication.account.teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.account.course.CourseModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ModifyCorsPassActivity extends AppCompatActivity {
   private LinearLayout linearLayout1, linearLayout2, linearLayout3;
   private TextView batchNameView;
   private CheckBox checkBoxPassword, checkBoxOther , checkBoxBatch;
    private TextInputLayout courseCode, courseName, newPass, oldPass;
    private Button savePassBtn, saveOtherBtn;
    private String spinTxt, crsId , dataPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_cors_pass);

        linearLayout1 = findViewById(R.id.line1);
        checkBoxPassword = findViewById(R.id.checkbox_passId);

        linearLayout2 = findViewById(R.id.line2);
        linearLayout3 = findViewById(R.id.line3);


        courseCode = findViewById(R.id.courseCodeId);
        courseName = findViewById(R.id.courseNameId);
        savePassBtn = findViewById(R.id.pass_chang_btn);
        saveOtherBtn = findViewById(R.id.changeOther_btn_Id) ;
        newPass = findViewById(R.id.new_pass_input_layout);
        oldPass = findViewById(R.id.old_pass_input_layout);
        checkBoxOther = findViewById(R.id.checkbox_otherId);
        checkBoxBatch = findViewById(R.id.checkbox_batchId);
        batchNameView = findViewById(R.id.batchNameViewId);

        crsId = getIntent().getStringExtra("crsId");


        Spinner spinner = findViewById(R.id.spinner_batch_id);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.batch_name_arry,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinTxt = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



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

        checkBoxBatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxBatch.isChecked()){
                    linearLayout3.setVisibility(View.VISIBLE);
                }else{
                    linearLayout3.setVisibility(View.GONE);
                }
            }
        });

        checkBoxOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxOther.isChecked()){
                    linearLayout2.setVisibility(View.VISIBLE);
                }else {
                    linearLayout2.setVisibility(View.GONE);
                }
            }
        });


        savePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validOldPass() | !validNewPass()){
                    return;
                }else {
                    String pass = newPass.getEditText().getText().toString();
                    savePassword(pass);
                }
            }
        });


        saveOtherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validCourseName() | !validCourseCode()){
                     return;
                }
                String courseNm = courseName.getEditText().getText().toString();
                String courseCd = courseCode.getEditText().getText().toString();


                if (checkBoxBatch.isChecked()){
                    saveOtherWithBatch(courseCd, courseNm, spinTxt);
                }else{
                  saveOther(courseCd, courseNm);
                }

            }
        });



        visbile();
        readData();

    }





    public boolean validCourseCode(){
        String s = courseCode.getEditText().getText().toString();
        if (s.isEmpty()){
            courseCode.setError("please fill the course code");
            return false;
        }else{
            courseCode.setError(null);
        }


        return true;
    }

    public boolean validCourseName(){

        String s = courseName.getEditText().getText().toString();
        if (s.isEmpty()){
            courseName.setError("please fill the course Name");
            return false;
        }else{
            courseName.setError(null);
        }


        return true;
    }

    public boolean validOldPass(){

        String s = oldPass.getEditText().getText().toString();
        if (s.isEmpty()){
            oldPass.setError("please enter your old password");
            return false;
        }else if(!s.equals(dataPassword)){
            oldPass.setError("Password don't match");
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
            newPass.setError("please enter new Password");
            return false;
        }else{
            newPass.setError(null);
        }


        return true;
    }



    public void readData(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("course").child(crsId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CourseModel model = dataSnapshot.getValue(CourseModel.class);
                courseCode.getEditText().setText(model.getCourseCode());
                courseName.getEditText().setText(model.getCourseName());
                dataPassword = model.getSecurityKey();
                batchNameView.setText(model.getBatchName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void savePassword(String newPass){
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("course").child(crsId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("securityKey", newPass);

        reference1.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ModifyCorsPassActivity.this, "Successfully changed Password", Toast.LENGTH_SHORT).show();
                    deleteWishlisht();
                }
            }
        });
    }

    public void saveOtherWithBatch(String courseCode, String courseName, String batchName){
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("course").child(crsId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("courseCode", courseCode);
        hashMap.put("courseName", courseName);
        hashMap.put("batchName", batchName);

        reference1.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ModifyCorsPassActivity.this, "Successfully changed Course details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void saveOther(String courseCode, String courseName){
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("course").child(crsId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("courseCode", courseCode);
        hashMap.put("courseName", courseName);

        reference1.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ModifyCorsPassActivity.this, "Successfully changed Course details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public boolean visbile(){
        linearLayout3.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.GONE);
        linearLayout1.setVisibility(View.GONE);
        return true;
    }

    public void deleteWishlisht(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("wishlist").child(crsId);
        databaseReference.removeValue();
    }
}
