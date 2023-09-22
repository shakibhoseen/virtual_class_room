package com.example.myapplication.account.studentwork;

import android.Manifest;
import android.app.Activity;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.slide.SlideModel;
import com.example.myapplication.slide.SlideTitleAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class SlideFragment extends Fragment {

    private RecyclerView recyclerView;
    private  List<SlideModel> slideModelList;
    private SlideTitleAdapter slideTitleAdapter;
    String crsId, teacherId;
    private boolean isTrnSlide;
    int position = 0;
    LinearLayoutManager linearLayoutManager;

    public SlideFragment() {
    }


    public static SlideFragment newInstance(String crsId, String teacherId){
        SlideFragment  fragment = new SlideFragment();
        Bundle bundle= new Bundle();
        bundle.putString("crsId", crsId);
        bundle.putString("teacherId", teacherId);
        fragment.setArguments(bundle);
        return fragment;
    }

  /*public SlideFragment(String crsId, String teacherId) {
        this.crsId = crsId;
        this.teacherId = teacherId;
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);


        if (getArguments()!=null){
            crsId = getArguments().getString("crsId");
            teacherId = getArguments().getString("teacherId");
        }else{
            Toast.makeText(getContext(), "opps", Toast.LENGTH_SHORT).show();
            return view;
        }


        recyclerView = view.findViewById(R.id.recyclerId);
        recyclerView.setHasFixedSize(true);

        linearLayoutManager =new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        SharedPreferences trans_memory = getActivity().getSharedPreferences("transparency", MODE_PRIVATE);
        isTrnSlide = trans_memory.getBoolean("trans_slide",false);

        slideModelList = new ArrayList<>();


        slideTitleAdapter = new SlideTitleAdapter(getContext(),slideModelList, crsId, teacherId, isTrnSlide);
        recyclerView.setAdapter(slideTitleAdapter);



        readSlidCollection();

        return view;
    }



    public void readSlidCollection(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("slide").child(crsId).child("collection");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     slideModelList.clear();
                     for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                         SlideModel slideModel = snapshot.getValue(SlideModel.class);
                         slideModel.setDataKey( snapshot.getKey());
                         slideModelList.add(slideModel);
                     }

                     slideTitleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }




}
