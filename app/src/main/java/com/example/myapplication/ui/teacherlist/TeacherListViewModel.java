package com.example.myapplication.ui.teacherlist;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.account.teacher.TeacherModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeacherListViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<List<TeacherModel>> listMutableLiveData;
    private List<TeacherModel> teacherModels;

    public TeacherListViewModel() {
        mText = new MutableLiveData<>();
        listMutableLiveData = new MutableLiveData<>();
        teacherModels = new ArrayList<>();
        mText.setValue("This is teacher list fragment");

        Query reference = FirebaseDatabase.getInstance().getReference("teacher").orderByChild("name").startAt("").endAt("\uf8ff");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teacherModels.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    TeacherModel model = snapshot.getValue(TeacherModel.class);
                    teacherModels.add(model);
                }
                listMutableLiveData.setValue(teacherModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<TeacherModel>> getModels(){
        return listMutableLiveData;
    }
}