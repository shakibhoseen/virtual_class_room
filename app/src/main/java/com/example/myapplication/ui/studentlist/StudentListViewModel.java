package com.example.myapplication.ui.studentlist;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.account.StudentModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentListViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<List<StudentModel>> listMutableLiveData;
    private List<StudentModel> studentModels;


    public StudentListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is share fragment");

        listMutableLiveData = new MutableLiveData<>();
        studentModels = new ArrayList<>();

        Query reference = FirebaseDatabase.getInstance().getReference("Users").orderByChild("roll").startAt("").endAt("\uf8ff");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentModels.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    StudentModel model = snapshot.getValue(StudentModel.class);
                    studentModels.add(model);
                }
                listMutableLiveData.setValue(studentModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<StudentModel>> getModels(){
        return listMutableLiveData;
    }
}