package com.example.myapplication.account.teacher.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.account.teacher.TeacherModel;
import com.example.myapplication.ui.teacherlist.TeacherListViewModel;

import java.util.ArrayList;
import java.util.List;

public class SlideshowTeacherFragment extends Fragment {

    private TeacherListViewModel slideshowViewModel;
    private RecyclerView recyclerView;
    private List<TeacherModel> teacherSearchlist, teacherModelListAll;

    private SearchView searchView;
    private TeacherDetailsistAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(TeacherListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_teacher_slideshow, container, false);


        teacherModelListAll = new ArrayList<>();
        teacherSearchlist = new ArrayList<>();
        searchView = root.findViewById(R.id.searchId);


        recyclerView = root.findViewById(R.id.recyclerId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        slideshowViewModel.getModels().observe(this, new Observer<List<TeacherModel>>() {
            @Override
            public void onChanged(List<TeacherModel> teacherModels) {
                teacherModelListAll.clear();
                teacherSearchlist.clear();

                teacherModelListAll.addAll(teacherModels);
                teacherSearchlist.addAll(teacherModels);

                adapter.notifyDataSetChanged();
            }
        });



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchStudent(newText.toLowerCase());
                return false;
            }
        });

        adapter = new TeacherDetailsistAdapter(getContext(), teacherSearchlist);
        recyclerView.setAdapter(adapter);



        return root;
    }



    public void searchStudent(String newText) {
        teacherSearchlist.clear();

        if (newText.equals("")) {
            teacherSearchlist.addAll(teacherModelListAll);
            adapter.notifyDataSetChanged();
        } else {
            for (int i = 0; i < teacherModelListAll.size(); i++) {
                if (teacherModelListAll.get(i).getName().toLowerCase().contains(newText) || teacherModelListAll.get(i).getPhone().toLowerCase().contains(newText))
                    teacherSearchlist.add(teacherModelListAll.get(i));
            }
            adapter.notifyDataSetChanged();
        }

    }
}