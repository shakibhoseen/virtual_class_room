package com.example.myapplication.ui.allcourse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
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

public class AllCourseStFragment extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;
    private List<CourseModel> courseModelList, courseSearchlist;
    private CorseCreateAdp createAdp;
    private SearchView searchView;
    private LinearLayout linearLayoutSearch;
    private boolean ckLinerVisible = false;
    private FloatingActionButton fab;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_student_home, container, false);
        linearLayoutSearch = root.findViewById(R.id.line1);
        searchView = root.findViewById(R.id.searchId);
        fab = root.findViewById(R.id.fab);

        recyclerView = root.findViewById(R.id.recyclerId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        courseModelList = new ArrayList<>();
        courseSearchlist = new ArrayList<>();
        courseModelList.clear();
        courseSearchlist.clear();
        readCourse();

        FragmentManager fragmentManager = getFragmentManager();
        createAdp = new CorseCreateAdp(getContext(), courseSearchlist, fragmentManager);
        recyclerView.setAdapter(createAdp);


        fab.setOnClickListener(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                   searchCourse(newText.toLowerCase());
                return false;
            }
        });

        linearLayoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               searchView.onActionViewExpanded();// wait to check
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
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CourseModel user = snapshot.getValue(CourseModel.class);
                    assert firebaseUser != null;
                    assert user != null;

                    courseModelList.add(user);
                    courseSearchlist.add(user);

                }
                createAdp.notifyDataSetChanged();
                for (int i = 0; i < courseModelList.size(); i++) {
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

                courseSearchlist.get(position).setImageUrl(teacherModel.getImageUrl());
                courseSearchlist.get(position).setTeacherName(teacherModel.getName());
                createAdp.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab: {
                if (!ckLinerVisible) {
                    linearLayoutSearch.setVisibility(View.VISIBLE);
                    ckLinerVisible = true;
                } else {
                    linearLayoutSearch.setVisibility(View.GONE);
                    searchView.onActionViewCollapsed();
                    ckLinerVisible = false;
                    searchCourse("");
                }
                break;
            }
        }
    }


    public void searchCourse(String newText) {
        courseSearchlist.clear();
        if (newText.equals("")) {
            courseSearchlist.addAll(courseModelList);
        } else {
            for (int i = 0; i < courseModelList.size(); i++) {
                if (courseModelList.get(i).getCourseName().toLowerCase().contains(newText) || courseModelList.get(i).getCourseCode().toLowerCase().contains(newText))
                    courseSearchlist.add(courseModelList.get(i));
            }
        }
        createAdp.notifyDataSetChanged();
    }
}