package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.account.StudentModel;
import com.example.myapplication.extra.AllreviewListActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentDetailsActivity extends AppCompatActivity {
  private CircleImageView profileImg;
  private TextView nameTxt, emailTxt, rollTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        profileImg = findViewById(R.id.profile_image);
        nameTxt = findViewById(R.id.username);
        rollTxt = findViewById(R.id.rollnoId);
        emailTxt = findViewById(R.id.email);

        final String userId = getIntent().getStringExtra("userId");

        readStudentProfile(userId);
    }


    public void readStudentProfile(String uId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StudentModel studentModel = dataSnapshot.getValue(StudentModel.class);

                if (!studentModel.getImageUrl().equals("") && !studentModel.getImageUrl().equals("default")) {
                    Glide.with(StudentDetailsActivity.this).load(studentModel.getImageUrl()).into(profileImg);
                } else {
                    profileImg.setImageResource(R.drawable.meena);
                }
                nameTxt.setText(studentModel.getUsername());
                rollTxt.setText(studentModel.getRoll());
                emailTxt.setText(studentModel.getEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
