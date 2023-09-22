package com.example.myapplication.account.teacher.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.R;
import com.example.myapplication.account.course.CorseCreateAdp;
import com.example.myapplication.account.course.CourseModel;
import com.example.myapplication.account.teacher.TeacherModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeTeacherFragment extends Fragment {

   private RecyclerView recyclerView;
   private List<CourseModel> courseModelList;
   private CorseCreateAdp createAdp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_teacher_home, container, false);

        recyclerView = root.findViewById(R.id.recyclerId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
         courseModelList = new ArrayList<>();
            courseModelList.clear();


         readCourse();

        FragmentManager fragmentManager = getFragmentManager();
         createAdp = new CorseCreateAdp(getContext(), courseModelList, fragmentManager);
        recyclerView.setAdapter(createAdp);

        return root;
    }


    public void readCourse(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("course");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       courseModelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CourseModel user = snapshot.getValue(CourseModel.class);
                    assert firebaseUser != null;
                    assert user != null;

                    courseModelList.add(user);

                }
                createAdp.notifyDataSetChanged();

                for (int i=0; i<courseModelList.size(); i++){
                    readTeacherProfile(i);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }



    private void readTeacherProfile(final int position) {
        String teacherId = courseModelList.get(position).getTeacherId();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("teacher").child(teacherId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TeacherModel teacherModel = dataSnapshot.getValue(TeacherModel.class);

                courseModelList.get(position).setImageUrl(teacherModel.getImageUrl());
                courseModelList.get(position).setTeacherName(teacherModel.getName());
                createAdp.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}