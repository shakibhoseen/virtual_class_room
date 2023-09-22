package com.example.myapplication.account.studentwork;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.myapplication.quiz.QuizDetailsModel;
import com.example.myapplication.quiz.QuizFontActivity;
import com.example.myapplication.quiz.temporaryquiz.QuizSubmitModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class QuizFragment extends Fragment {
  private RecyclerView recyclerView;
  private String crsId;
  private List<QuizDetailsModel> quizDetailsModels;


    String id, teacherId, status;
    QuizFontAdapter quizFontAdapter;

    private boolean isRightTeacher, isTrans;

    public QuizFragment() {
    }

    public static QuizFragment newInstance(String crsId, String teacherId){
        QuizFragment fragment = new QuizFragment();
        Bundle bundle = new Bundle();
        bundle.putString("crsId", crsId);
        bundle.putString("teacherId", teacherId);
        fragment.setArguments(bundle);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_favorites, container, false);

        if (getArguments()!=null){
            crsId = getArguments().getString("crsId");
            teacherId = getArguments().getString("teacherId");
        }else{
            Toast.makeText(getContext(), "opps", Toast.LENGTH_SHORT).show();
            return view;
        }

        quizDetailsModels = new ArrayList<>();

        id = FirebaseAuth.getInstance().getUid();

        if (id.equals(teacherId)){
            isRightTeacher = true;
        }

        SharedPreferences preferences = getActivity().getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        status = preferences.getString("status", "");

        SharedPreferences trans_memory = getActivity().getSharedPreferences("transparency", MODE_PRIVATE);
        isTrans = trans_memory.getBoolean("trans_quiz",false);

        recyclerView = view.findViewById(R.id.recyclerId);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        quizFontAdapter =  new QuizFontAdapter(getContext(), quizDetailsModels, crsId, isRightTeacher, status, isTrans);
        recyclerView.setAdapter(quizFontAdapter);

        readQuizlist();

        return view;
    }



    public void readQuizlist(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("details");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 quizDetailsModels.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    QuizDetailsModel model = snapshot.getValue(QuizDetailsModel.class);

                    quizDetailsModels.add(model);
                }
               quizFontAdapter.notifyDataSetChanged();


                if (quizDetailsModels.size() > 0) {
                    for (int i = 0; i < quizDetailsModels.size(); i++) {
                        ckSubmit(quizDetailsModels.get(i).getDataKey(), i);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void ckSubmit(String iKey, final int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("collection").child(iKey);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String set = "0";
                int i= 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    QuizSubmitModel model = snapshot.getValue(QuizSubmitModel.class);
                    if (model.getId().equals(id)) {
                        set = "1";
                    }
                    i++;
                }

                quizDetailsModels.get(position).setSubmitCk(set);
                quizDetailsModels.get(position).setCountStudent(i);
                quizFontAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
