package com.example.myapplication.ui.mycourse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.account.StudentModel;
import com.example.myapplication.account.course.CorseCreateAdp;
import com.example.myapplication.account.course.CourseModel;
import com.example.myapplication.account.teacher.TeacherModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyCourseStFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyCourseViewModel myCourseViewModel;
    private List<CourseModel> courseModelList, myList;
     public  CorseCreateAdp createAdp;
    String userId;
    TextView textView;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myCourseViewModel =
                ViewModelProviders.of(this).get(MyCourseViewModel.class);
        View root = inflater.inflate(R.layout.fragment_student_gallery, container, false);
         textView = root.findViewById(R.id.text_gallery);

        userId = FirebaseAuth.getInstance().getUid();

        recyclerView = root.findViewById(R.id.recyclerId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        courseModelList = new ArrayList<>();
        courseModelList.clear();
        myList = new ArrayList<>();
        myList.clear();




        readCourse();



         createAdp  = new CorseCreateAdp(getContext(), myList, "myCourse");


        recyclerView.setAdapter(createAdp);


        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               readCourse();
            }
        });

        return root;


    }




    public void readCourse() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("course");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                courseModelList.clear();
                myList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CourseModel user = snapshot.getValue(CourseModel.class);
                    assert firebaseUser != null;
                    assert user != null;

                    courseModelList.add(user);

                }
                myList.clear();
                for (int i=0; i<courseModelList.size(); i++){
                    ckMyCourse(courseModelList.get(i));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }


    public void ckMyCourse( final CourseModel courseModel){
        String ikey = courseModel.getCourseId();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("wishlist").child(ikey);//here ikey is crs id actually becareful

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              if (dataSnapshot.exists()){
                  for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                      StudentModel model = snapshot.getValue(StudentModel.class);
                      if (model.getId().equals(userId)){

                          if (matchBag(courseModel, myList)){
                              myList.add(courseModel);

                          }

                          textView.setVisibility(View.GONE);
                          break;
                      }
                  }

                  for (int i=0; i<myList.size(); i++){
                      readTeacherProfile(i);
                  }

                  createAdp.notifyDataSetChanged();
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public boolean matchBag(CourseModel courseModel, List<CourseModel> list){

        if (list.size()!=0){
            for (int i=0;i<list.size();i++){
                if (list.get(i).getCourseId().equals(courseModel.getCourseId())){
                    return false;
                }
            }

        }else {
            return true;
        }
        return true;
    }



    private void readTeacherProfile(final int position) {
        String teacherId = myList.get(position).getTeacherId();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("teacher").child(teacherId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TeacherModel teacherModel = dataSnapshot.getValue(TeacherModel.class);

                myList.get(position).setImageUrl(teacherModel.getImageUrl());
                myList.get(position).setTeacherName(teacherModel.getName());
                createAdp.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}