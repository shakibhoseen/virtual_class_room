package com.example.myapplication.account.teacher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.account.LogInActivity;

import com.example.myapplication.account.course.InfoDailog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterTeacherActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtGologinPage;
    private  MaterialEditText username,email,cnfrmpass,phone;
    private TextInputLayout passwordEdTxt, security ;
    private Button btn_register;
    private ImageButton  info;
    private ProgressDialog progressDialog;
    public final static String SHARED_PREFS = "sharedPrefs";
    public final static String STATUS = "status";
    public  String loadSecurity;

    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_teacher);



        txtGologinPage = findViewById(R.id.txtGologinPageId);
        username =findViewById(R.id.username);
        email =findViewById(R.id.email);
        passwordEdTxt =findViewById(R.id.passwordEdtTxtId);
        btn_register =findViewById(R.id.btn_register);
        cnfrmpass = findViewById(R.id.cnfrmPassword);
        phone = findViewById(R.id.rollnoId);
        security = findViewById(R.id.securityKeyId);
        info = findViewById(R.id.infoId);

        // firebase
        auth = FirebaseAuth.getInstance();
        loadsecurityKey();

        txtGologinPage.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        info.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.txtGologinPageId){
            txtGologinPage.setTextColor(getResources().getColor(R.color.colorPrimary));
            startActivity(new Intent(getApplicationContext(),LogInActivity.class));
            finish();
        }
        else if( v.getId()==R.id.btn_register){
            String txt_username,txt_email,txt_password,txt_cnfpass,txt_phone;
            txt_username =username.getText().toString();
            txt_email =email.getText().toString();
            txt_password =passwordEdTxt.getEditText().getText().toString();
            txt_cnfpass=cnfrmpass.getText().toString();
            txt_phone = phone.getText().toString();

            progressDialog = new ProgressDialog(RegisterTeacherActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Registering please wait..");
            if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email)
                    || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_cnfpass) ||
            TextUtils.isEmpty(txt_phone)){
                Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show();
            }else if(!txt_cnfpass.equals(txt_password)){
                Toast.makeText(this, "password don't match", Toast.LENGTH_SHORT).show();
                passwordEdTxt.setError("password don't match");
            }else if(!validSecurity()){
                passwordEdTxt.setError(null);
                return;
            }

            else{

                progressDialog.show();
               register(txt_username,txt_email,txt_password,txt_phone);
            }
        }
        else if(v.getId()==R.id.infoId){
            InfoDailog infoDailog = new InfoDailog("teacher");
            infoDailog.show(getSupportFragmentManager() , "dailog");
        }
    }


    private void register(final String username, String email, String password, final String phone){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser=auth.getCurrentUser();
                            assert firebaseUser!=null;
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("teacher").child(userid);

                            HashMap<String ,String> hashMap= new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("name", username);
                            hashMap.put("imageUrl","default");
                            hashMap.put("status","teacher");
                           // hashMap.put("search",username.toString().toLowerCase());
                            hashMap.put("phone",phone);

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString(STATUS,"teacher");
                                        editor.apply();

                                        progressDialog.dismiss();

                                        Intent intent= new Intent(RegisterTeacherActivity.this, MainTeacherActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });


                        }else{
                            Toast.makeText(RegisterTeacherActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    public boolean validSecurity(){
       String s = security.getEditText().getText().toString();
       if(s.equals("")){
           security.setError("provide the security key to join");
           return false;
       }else if(!s.equals(loadSecurity)){
           security.setError("wrong security key");
           return false;
       }
       return true;
    }

    public void loadsecurityKey(){




        reference = FirebaseDatabase.getInstance().getReference("security");
        final  ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Wait for server reply");
        pd.setCancelable(false);
        pd.show();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    SecureModel quizModel = snapshot.getValue(SecureModel.class);
                    loadSecurity=quizModel.getTeacher();

                }


                pd.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
