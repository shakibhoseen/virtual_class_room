package com.example.myapplication.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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


import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.account.course.InfoDailog;
import com.example.myapplication.account.teacher.SecureModel;
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

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    public static final Pattern ROLL_PATTERN = Pattern.compile("^"+"(?=\\S+$)"+"[0-9]"+"[0-9]"+"[a-z]"+"[a-z]"+"[a-z]"+"[0-9]"+"[0-9]"+"[0-9]"+"$");
    private TextView txtGologinPage;
    private  MaterialEditText username,email,cnfrmpass;
    private TextInputLayout password, security, roll;
    private Button btn_register;
    private ImageButton info;
    private ProgressDialog progressDialog;
    public final static String SHARED_PREFS = "sharedPrefs";
    public final static String STATUS = "status";
    String loadSecurity;
    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        txtGologinPage = findViewById(R.id.txtGologinPageId);
        username =findViewById(R.id.username);
        email =findViewById(R.id.email);
        password =findViewById(R.id.password);
        btn_register =findViewById(R.id.btn_register);
        cnfrmpass = findViewById(R.id.cnfrmPassword);
        roll = findViewById(R.id.rollnoId);
        security = findViewById(R.id.securityKeyId);
        info = findViewById(R.id.infoId);

        // firebase
        auth = FirebaseAuth.getInstance();

        txtGologinPage.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        info.setOnClickListener(this);

        loadsecurityKey();

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.txtGologinPageId){
            txtGologinPage.setTextColor(getResources().getColor(R.color.colorPrimary));
            startActivity(new Intent(getApplicationContext(),LogInActivity.class));
            finish();
        }
        else if( v.getId()==R.id.btn_register){
            String txt_username,txt_email,txt_password,txt_cnfpass,txt_roll;
            txt_username =username.getText().toString();
            txt_email =email.getText().toString();
            txt_password =password.getEditText().getText().toString();
            txt_cnfpass=cnfrmpass.getText().toString();
            txt_roll = roll.getEditText().getText().toString();



            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Registering please wait..");
            if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email)
                    || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_cnfpass) ||
            TextUtils.isEmpty(txt_roll)){
                Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show();
            }else if(!txt_cnfpass.equals(txt_password)){
                Toast.makeText(this, "password don't match", Toast.LENGTH_SHORT).show();
            }else if(!validSecurity() | !validRoll()){
                return;
            }

            else{
                String sub = txt_roll.substring(0,2);

                int temp = Integer.parseInt(sub) - 10;

                sub = "CSE "+temp+"th Batch"; //CSE 6th Batch
                progressDialog.show();
                register(txt_username,txt_email,txt_password,txt_roll, sub);
            }
        }else if(v.getId()==R.id.infoId){
            InfoDailog infoDailog = new InfoDailog("student");
            infoDailog.show(getSupportFragmentManager() , "dailog");
        }
    }


    private void register(final String username, final String email, String password, final String roll, final String batchName){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser=auth.getCurrentUser();
                            assert firebaseUser!=null;
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String ,String> hashMap= new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("imageUrl","default");
                            hashMap.put("status","student");
                            hashMap.put("batch",batchName);
                            hashMap.put("email",email);
                            hashMap.put("roll",roll);

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString(STATUS,"student");
                                        editor.apply();

                                        progressDialog.dismiss();
                                        Intent intent= new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(RegisterActivity.this, "failed to add database", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }






             /// find out security key to join as a student
    public void loadsecurityKey(){

        reference = FirebaseDatabase.getInstance().getReference("security");
        final  ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("waiting for server reply..");
        pd.setCancelable(false);
        pd.show();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    SecureModel quizModel = snapshot.getValue(SecureModel.class);
                    loadSecurity=quizModel.getStudent();

                }

                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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


    public boolean validRoll(){
        String s = roll.getEditText().getText().toString();
        if(s.equals("")){
            roll.setError("provide the security key to join");
            return false;
        }else if(!ROLL_PATTERN.matcher(s).matches()){
            roll.setError("wrong roll number");
            return false;
        }else{
          roll.setError(null);
        }
        return true;
    }



}
