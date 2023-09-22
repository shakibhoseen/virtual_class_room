package com.example.myapplication.account.teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.account.ProfilePicture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherDetailsActivity extends AppCompatActivity {
    private CircleImageView profileImg;
    private TextView nameTxt, emailTxt, phoneTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_details);

        profileImg = findViewById(R.id.profile_image);
        nameTxt = findViewById(R.id.username);
        phoneTxt = findViewById(R.id.phone_id);
        emailTxt = findViewById(R.id.email);
        final String userId = getIntent().getStringExtra("userId");
        readTeacherProfile(userId);

    }


    private void readTeacherProfile(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("teacher").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TeacherModel teacherModel = dataSnapshot.getValue(TeacherModel.class);

                nameTxt.setText(teacherModel.getName());
                phoneTxt.setText(teacherModel.getPhone());
                emailTxt.setText(teacherModel.getEmail());

                if (!teacherModel.getImageUrl().equals("") && !teacherModel.getImageUrl().equals("default")){
                    Picasso.with(TeacherDetailsActivity.this).load(teacherModel.getImageUrl()).into(profileImg);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
