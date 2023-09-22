package com.example.myapplication.account.studentwork;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.account.assignment.AssignmentSubmitModel;
import com.example.myapplication.account.assignment.AssignmentTitileModel;
import com.example.myapplication.account.assignment.AssignmentTitleAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AssignmentFragment extends Fragment {
    private RecyclerView recyclerView;
    private String crsId, crsNm;
    private List<AssignmentTitileModel> titileModelList;

    String id, teacherId;
    AssignmentTitleAdapter titleAdapter;
    boolean isRightTeacher = false;

    public AssignmentFragment(){

    }

    public static AssignmentFragment newInstance(String crsId, String crsNm, String teacherId){
        AssignmentFragment fragment = new AssignmentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("crsId",crsId);
        bundle.putString("crsNm",crsNm);
        bundle.putString("teacherId",teacherId);
        fragment.setArguments(bundle);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        if (getArguments()!=null){
            crsId = getArguments().getString("crsId");
            crsNm = getArguments().getString("crsNm");
            teacherId = getArguments().getString("teacherId");
        }else{
            Toast.makeText(getContext(), "Opps someting wrong", Toast.LENGTH_SHORT).show();
            return view;
        }

        recyclerView = view.findViewById(R.id.recyclerId);
        recyclerView.setHasFixedSize(true);

        SharedPreferences trans_memory = getActivity().getSharedPreferences("transparency", MODE_PRIVATE);
        boolean isTrans = trans_memory.getBoolean("trans_assign",false);
        //recyclerView.setBackgroundResource(R.drawable.meena);

        id = FirebaseAuth.getInstance().getUid();
        titileModelList = new ArrayList<>();

        if (id.equals(teacherId)){
            isRightTeacher = true;
        }





        readAssignmentlist();
        registerForContextMenu(recyclerView);

        titleAdapter = new AssignmentTitleAdapter(getContext(), titileModelList, crsId, crsNm, isRightTeacher, isTrans);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(titleAdapter);


        return view;
    }

    public void readAssignmentlist() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("assignment").child(crsId).child("details");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                titileModelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AssignmentTitileModel model = snapshot.getValue(AssignmentTitileModel.class);

                    titileModelList.add(model);
                }
                titleAdapter.notifyDataSetChanged();
                int quiz_size = titileModelList.size();
                if (quiz_size > 0) {
                    for (int i = 0; i < quiz_size; i++) {
                        ckSubmit(titileModelList.get(i).getDataKey(), i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void ckSubmit(String iKey, final int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("assignment").child(crsId).child("collection").child(iKey);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (titileModelList.size()<=position)
                    return;
                String set = "0";
                int i=0;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AssignmentSubmitModel model = snapshot.getValue(AssignmentSubmitModel.class);
                        if (model.getId().equals(id)) {
                            set = "1";
                        }
                      i++;
                    }
                    titileModelList.get(position).setCountStudent(i);
                    titileModelList.get(position).setSubmitCk(set);
                    titleAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }


}
