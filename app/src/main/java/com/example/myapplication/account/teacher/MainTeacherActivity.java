package com.example.myapplication.account.teacher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.SettngActivity;
import com.example.myapplication.StartActivity;
import com.example.myapplication.account.ProfilePicture;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.account.teacher.ui.AdminDialog;
import com.example.myapplication.group_chat.MessageActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainTeacherActivity extends AppCompatActivity {
       FirebaseAuth auth;
       FirebaseUser firebaseUser;
    public final static String SHARED_PREFS = "sharedPrefs";
    public final static String STATUS = "status";
    private AppBarConfiguration mAppBarConfiguration;
    private  NavigationView navigationView;
    private DatabaseReference teacrInfRef;
    private ValueEventListener teacherInfLsner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_teacher);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        setTeacherInform();





        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_teacher_gallery,  R.id.nav_teacher_home, R.id.nav_teacher_share, R.id.nav_teacher_slideshow  ,R.id.nav_action_create)
                .setDrawerLayout(drawer)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_teacher_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.getMenu().findItem(R.id.nav_action_admin).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AdminDialog adminDialog = new AdminDialog();
                adminDialog.show(getSupportFragmentManager(), "admin");
                return true;
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        if (teacherInfLsner!=null)
         teacrInfRef.removeEventListener(teacherInfLsner);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_teacher, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_teacher_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            case R.id.action_settings: {
               startActivity(new Intent(MainTeacherActivity.this, SettngActivity.class));

                return true;
            }
            case R.id.action_logout: {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(STATUS, "");
                editor.apply();

                startActivity( new Intent(MainTeacherActivity.this, StartActivity.class));
                finish();
                     return true;
            }
            case R.id.action_myprofile:{
                Intent intent =  new Intent(MainTeacherActivity.this, ProfilePicture.class);

                startActivity(intent);
                return  true;
            } case R.id.action_chat:{

                startActivity(new Intent(MainTeacherActivity.this, MessageActivity.class));

                return true;
            }
            default:  return super.onOptionsItemSelected(item);
        }

    }


    public  void setTeacherInform(){
        if(FirebaseAuth.getInstance()==null){
            return;
        }
        teacrInfRef = FirebaseDatabase.getInstance().getReference("teacher").child(auth.getUid());

         teacherInfLsner = teacrInfRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TeacherModel model =dataSnapshot.getValue(TeacherModel.class);

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                TextView navUsername = (TextView) headerView.findViewById(R.id.teachernameId);
                TextView navEmail = headerView.findViewById(R.id.teacherEmailId);
                CircleImageView imageView = headerView.findViewById(R.id.imageViewTeacherId);
                navEmail.setText(model.getPhone());
                navUsername.setText(model.getName());
                if(!model.getImageUrl().equals("")&& !model.getImageUrl().equals("default")){
                    Glide.with(MainTeacherActivity.this).load(model.getImageUrl()).into(imageView);
                }else{
                    imageView.setImageResource(R.drawable.meena);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
