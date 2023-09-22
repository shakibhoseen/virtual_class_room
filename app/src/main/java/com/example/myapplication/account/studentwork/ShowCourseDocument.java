package com.example.myapplication.account.studentwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.account.StudentModel;
import com.example.myapplication.account.assignment.AssignmentCreateFontActivity;
import com.example.myapplication.account.teacher.MainTeacherActivity;
import com.example.myapplication.account.teacher.ModifyCorsPassActivity;
import com.example.myapplication.quiz.QuizFontActivity;
import com.example.myapplication.slide.SlideCreateActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ShowCourseDocument extends AppCompatActivity {
    private String crsId, crsNm, teacherId;
    private TextView toolbarTxt;
    private String status;
    URL serverUrl;
    String roll, imageUrl, userName;
    Fragment selectedFragment = null;
    boolean isSlide = true, isQiz = false, isAgn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_course_document);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTxt = findViewById(R.id.toolbar_txt);

        crsId = getIntent().getStringExtra("crsId");
        crsNm = getIntent().getStringExtra("crsNm");
        teacherId = getIntent().getStringExtra("teacherId");




        SharedPreferences preferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        status = preferences.getString("status", "");
        if (status.equals("student")) {
            readUser(FirebaseAuth.getInstance().getUid());
        }


        try {
            serverUrl = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOption =
                    new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverUrl)
                            .setWelcomePageEnabled(false)
                            .build();
            JitsiMeet.setDefaultConferenceOptions(defaultOption);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        // getSupportActionBar().setTitle(crsNm);
        toolbarTxt.setText(crsNm);
        if (savedInstanceState == null) {
             selectedFragment = SlideFragment.newInstance(crsId, teacherId);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, selectedFragment, "s").
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();

        }



    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.nav_slide:
                            isAgn = false;
                            isQiz = false;
                            isSlide = true;
                            selectedFragment = SlideFragment.newInstance(crsId, teacherId);
                            //  getSupportActionBar().setTitle("Note");
                            break;
                        case R.id.nav_quiz:
                            isAgn = false;
                            isQiz = true;
                            isSlide = false;
                            selectedFragment =  QuizFragment.newInstance( crsId, teacherId);
                            //  getSupportActionBar().setTitle("Favorite");
                            break;
                        case R.id.nav_assign:
                            isAgn = true;
                            isQiz = false;
                            isSlide = false;
                            selectedFragment =  AssignmentFragment.newInstance(crsId, crsNm, teacherId);
                            //  getSupportActionBar().setTitle("Search");
                            break;
                    }

                    openFragment();

                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        if (status.equals("teacher")) {
            if (!FirebaseAuth.getInstance().getUid().equals(teacherId))
                return false;
            getMenuInflater().inflate(R.menu.dc_teacher_menu, menu);

            //you can hide anything from option menu
            MenuItem createQuizMenu = menu.findItem(R.id.action_create);
        } else {
            getMenuInflater().inflate(R.menu.dc_student_menu, menu);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                Intent intent = new Intent(ShowCourseDocument.this, QuizFontActivity.class);
                intent.putExtra("crsId", crsId);
                startActivity(intent);
                break;
            case R.id.action_assignment: {
                Intent p = new Intent(ShowCourseDocument.this, AssignmentCreateFontActivity.class);
                p.putExtra("crsId", crsId);
                startActivity(p);
                break;
            }
            case R.id.action_add_wish: {
                addWishList();
                break;
            }
            case R.id.action_slide: {
                Intent intent1 = new Intent(ShowCourseDocument.this, SlideCreateActivity.class);
                intent1.putExtra("crsId", crsId);
                intent1.putExtra("crsNm", crsNm);
                startActivity(intent1);
                break;
            }
            case R.id.action_edit_course: {
                Intent intent2 = new Intent(ShowCourseDocument.this, ModifyCorsPassActivity.class);
                intent2.putExtra("crsId", crsId);
                startActivity(intent2);
                break;
            }
            case R.id.action_video_conf: {
                conferenceJoin();
                break;
            }


        }
        return true;
    }


    public void addWishList() {
        String userid = FirebaseAuth.getInstance().getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("wishlist").child(crsId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", userName);
        hashMap.put("roll", roll);
        hashMap.put("imageUrl", imageUrl);
        hashMap.put("id", userid);
//        hashMap.put("publish", new Date().getTime());

        reference.child(userid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ShowCourseDocument.this, "added successfully. now you can access without password", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void readUser(String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StudentModel user = dataSnapshot.getValue(StudentModel.class);
                roll = user.getRoll();
                userName = user.getUsername();
                imageUrl = user.getImageUrl();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {


        if (status.equals("student")) {
            Intent intent = new Intent(ShowCourseDocument.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (status.equals("teacher")) {
            Intent intent = new Intent(ShowCourseDocument.this, MainTeacherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Something went wrong. please Restart", Toast.LENGTH_SHORT).show();
            finish();
        }


        super.onBackPressed();
    }


    private void conferenceJoin() {

        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                .setRoom("1234shakib")
                .setWelcomePageEnabled(false)
                .build();
        JitsiMeetActivity.launch(ShowCourseDocument.this, options);
    }


    public void openFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

       outState.putBoolean("Agn", isAgn );
       outState.putBoolean("Qiz", isQiz );
       outState.putBoolean("Sld", isSlide );
       
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
       isAgn = savedInstanceState.getBoolean("Agn");
       isQiz= savedInstanceState.getBoolean("Qiz");
       isSlide= savedInstanceState.getBoolean("Sld");
       if (isAgn){
           selectedFragment =    AssignmentFragment.newInstance(crsId, crsNm, teacherId);
       }

       else if (isQiz) {
           selectedFragment =  QuizFragment.newInstance( crsId, teacherId);
       }

       else {
           selectedFragment =  SlideFragment.newInstance(crsId, teacherId);
       }
     //  openFragment();
    }



    
}
