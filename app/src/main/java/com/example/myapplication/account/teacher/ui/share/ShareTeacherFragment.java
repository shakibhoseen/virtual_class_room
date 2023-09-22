package com.example.myapplication.account.teacher.ui.share;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.account.StudentModel;

import java.util.ArrayList;
import java.util.List;

public class ShareTeacherFragment extends Fragment {

    private ShareTeacherViewModel shareTeacherViewModel;
    private RecyclerView recyclerView;
    private List<StudentModel> studentSearchlist, studentModelListAll, tempList;
    private List<CombineModel> combineModelList, combineSearchList;
    private BatchItemAdapter adapter;
    private SearchView searchView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        shareTeacherViewModel =
                ViewModelProviders.of(this).get(ShareTeacherViewModel.class);
        View root = inflater.inflate(R.layout.fragment_teacher_share, container, false);

        studentSearchlist = new ArrayList<>();
        studentModelListAll = new ArrayList<>();
        searchView = root.findViewById(R.id.searchId);


        recyclerView = root.findViewById(R.id.recyclerId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        combineModelList = new ArrayList<>();
        combineSearchList = new ArrayList<>();

        shareTeacherViewModel.getModels().observe(this, new Observer<List<StudentModel>>() {
            @Override
            public void onChanged(List<StudentModel> studentModels) {
                studentSearchlist.clear();
                studentModelListAll.clear();

                studentSearchlist.addAll(studentModels);
                studentModelListAll.addAll(studentModels);
                readFinalCombine();
                //adapter.notifyDataSetChanged();
            }
        });




        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchStudent(newText);
                return false;
            }
        });

         adapter = new BatchItemAdapter(getContext(), combineSearchList );
         recyclerView.setAdapter(adapter);

        return root;
    }




    public void searchStudent(String newText) {
        combineSearchList.clear();
        studentSearchlist.clear();
        if (newText.equals("")) {
            combineSearchList.addAll(combineModelList);
            adapter.notifyDataSetChanged();
        } else {
            for (int i = 0; i < studentModelListAll.size(); i++) {
                if (studentModelListAll.get(i).getUsername().toLowerCase().contains(newText) || studentModelListAll.get(i).getRoll().toLowerCase().contains(newText))
                    studentSearchlist.add(studentModelListAll.get(i));
            }
            readCombine();
        }

    }



    public void readFinalCombine() {
        combineSearchList.clear();
        combineModelList.clear();
        for (int i = 0; i < studentModelListAll.size(); i++) {
            StudentModel studentModel = studentModelListAll.get(i);
            tempList = new ArrayList<>();
            String batchHold = studentModel.getBatch();
            tempList.add(studentModel);
            if (i + 1 < studentModelListAll.size()) {
                while (studentModelListAll.get(i + 1).getBatch().equals(studentModel.getBatch())) {
                      tempList.add(studentModelListAll.get(i+1));
                      i++;
                      if (i+1<studentModelListAll.size()){

                      }else {
                          break;
                      }
                }
            }
            CombineModel combineModel = new CombineModel();
            combineModel.setBatchName(batchHold);
            combineModel.setModelList(tempList);
            combineModelList.add(combineModel);
        }
        combineSearchList.addAll(combineModelList);
        adapter.notifyDataSetChanged();

    }

    public void readCombine() {
        combineSearchList.clear();

        for (int i = 0; i < studentSearchlist.size(); i++) {
            StudentModel studentModel = studentSearchlist.get(i);
            tempList = new ArrayList<>();
            String batchHold = studentModel.getBatch();
            tempList.add(studentModel);
            if (i + 1 < studentSearchlist.size()) {
                while (studentSearchlist.get(i + 1).getBatch().equals(studentModel.getBatch())) {
                    tempList.add(studentSearchlist.get(i+1));
                    i++;
                    if (i+1<studentSearchlist.size()){

                    }else {
                        break;
                    }
                }
            }
            CombineModel combineModel = new CombineModel();
            combineModel.setBatchName(batchHold);
            combineModel.setModelList(tempList);
            combineSearchList.add(combineModel);
        }
        adapter.notifyDataSetChanged();
    }



}