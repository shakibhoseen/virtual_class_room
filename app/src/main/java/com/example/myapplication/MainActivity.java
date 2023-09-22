package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.myapplication.account.ProfilePicture;
import com.example.myapplication.account.StudentModel;
import com.example.myapplication.account.teacher.MainTeacherActivity;
import com.example.myapplication.account.teacher.TeacherModel;
import com.example.myapplication.group_chat.MessageActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    public final static String SHARED_PREFS = "sharedPrefs";
    public final static String STATUS = "status";
    private String ownImgUrl;
    private String ownName;
    FirebaseAuth auth;
    private ValueEventListener stuInfmLisener;
    private DatabaseReference studentInfmRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        auth = FirebaseAuth.getInstance();

        setStudentInform();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_gallery, R.id.nav_home, R.id.nav_share_student_list, R.id.nav_slideshow,
                R.id.nav_tools)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

       /* MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // searchUsers(newText);
               // searchStuentPer(newText);
                return true;
            }
        });*/


        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(stuInfmLisener!=null)
         studentInfmRef.removeEventListener(stuInfmLisener);


    }

    @Override
    protected void onResume() {
        super.onResume();
       /* if(stuInfmLisener!=null)
            studentInfmRef.addValueEventListener(stuInfmLisener);*/



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_myprofile: {
                Intent intent = new Intent(MainActivity.this, ProfilePicture.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_settings: {
                startActivity(new Intent(MainActivity.this, SettngActivity.class));

                return true;
            }
            case R.id.action_chat: {

                startActivity(new Intent(MainActivity.this, MessageActivity.class));

                return true;
            }
            case R.id.action_logout: {
                FirebaseAuth.getInstance().signOut();
                if (FirebaseAuth.getInstance().getCurrentUser() != null)
                    return true;
                SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(STATUS, "");
                editor.apply();

                startActivity(new Intent(MainActivity.this, StartActivity.class));
                finish();
                return true;

            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void setStudentInform() {
        if (FirebaseAuth.getInstance() == null) {
            return;
        }
         studentInfmRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getUid());

         stuInfmLisener = studentInfmRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StudentModel model = dataSnapshot.getValue(StudentModel.class);

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                // headerView.setBackgroundResource(R.drawable.jannat);
                TextView navUsername = (TextView) headerView.findViewById(R.id.studentName);
                TextView navEmail = headerView.findViewById(R.id.email);
                CircleImageView imageView = headerView.findViewById(R.id.imageView);
                navEmail.setText(model.getRoll());
                navUsername.setText(model.getUsername());
                ownImgUrl = model.getImageUrl();
                ownName = model.getUsername();
                if (!model.getImageUrl().equals("") && !model.getImageUrl().equals("default")) {
                    Glide.with(MainActivity.this).load(ownImgUrl).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.meena);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


}
