package com.example.myapplication.account.teacher.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TeacherGalleryFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<CourseModel> courseModelList;
    private  CorseCreateAdp createAdp;
    String imgUrl;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_teacher_gallery, container, false);



        recyclerView = root.findViewById(R.id.recyclerId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        courseModelList = new ArrayList<>();
        courseModelList.clear();

        createAdp = new CorseCreateAdp(getContext(), courseModelList);
        recyclerView.setAdapter(createAdp);


        readPersonalCourse();
        return root;
    }



    public void readPersonalCourse(){

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
                    if(user.getTeacherId().equals(firebaseUser.getUid())) {

                        courseModelList.add(user);
                    }
                }
                readTeacherProfile();
                createAdp.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }


    private void readTeacherProfile() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("teacher").child(FirebaseAuth.getInstance().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TeacherModel teacherModel = dataSnapshot.getValue(TeacherModel.class);
                imgUrl=teacherModel.getImageUrl();



                for (int i=0; i< courseModelList.size(); i++){
                    courseModelList.get(i).setImageUrl(imgUrl);
                    courseModelList.get(i).setTeacherName(teacherModel.getName());
                }
                createAdp.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}