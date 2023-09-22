package com.example.myapplication.account.teacher.ui.send;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;
import com.example.myapplication.account.course.InfoDailog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SendTeacherFragment extends Fragment {

    private TextInputLayout courseCode, courseName, security;
    private ImageButton info;
    private Button create;
    String spinTxt;
    DatabaseReference reference;
    FirebaseUser user;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_teacher_send, container, false);

        courseCode = root.findViewById(R.id.courseCodeId);
        courseName = root.findViewById(R.id.courseNameId);

        security = root.findViewById(R.id.securityKeyId);
        info = root.findViewById(R.id.infoId);
        create = root.findViewById(R.id.creteId);

        user = FirebaseAuth.getInstance().getCurrentUser();
        Spinner spinner = root.findViewById(R.id.spinner_batch_id);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.batch_name_arry,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinTxt = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoDailog infoDailog = new InfoDailog("create");
                infoDailog.show(getActivity().getSupportFragmentManager() , "dailog");
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String coursCode = courseCode.getEditText().getText().toString();
                String coursName = courseName.getEditText().getText().toString();

                String securityKey = security.getEditText().getText().toString();

                if( !validCourseCode() | !validCourseName() | !validSecurityKey()){
                    return;
                }else {
                    uploadCourse(coursCode,coursName, spinTxt,securityKey);

                }
            }
        });



        return root;
    }



    public boolean validCourseCode(){
        String s = courseCode.getEditText().getText().toString();
        if (s.isEmpty()){
            courseCode.setError("please fill the course code");
            return false;
        }else{
            courseCode.setError(null);
        }


        return true;
    }

    public boolean validCourseName(){

        String s = courseName.getEditText().getText().toString();
        if (s.isEmpty()){
            courseName.setError("please fill the course Name");
            return false;
        }else{
            courseName.setError(null);
        }


        return true;
    }

    public boolean validSecurityKey(){

        String s = security.getEditText().getText().toString();
        if (s.isEmpty()){
            security.setError("please fill security key");
            return false;
        }else{
            security.setError(null);
        }


        return true;
    }




    public void uploadCourse(String courseCode, String courseName, String batchName, String securityKey){

        reference = FirebaseDatabase.getInstance().getReference("course");

        HashMap<String , String> hashMap = new HashMap<>();
        hashMap.put("courseCode", courseCode);
        hashMap.put("courseName", courseName);
        hashMap.put("batchName", batchName);
        hashMap.put("teacherId", user.getUid());
        hashMap.put("imageUrl", "default");

        hashMap.put("securityKey", securityKey);
        String crsId = reference.push().getKey();
        hashMap.put("courseId",crsId);
        reference.child(crsId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "virtual class is created", Toast.LENGTH_SHORT).show();
                    create.setEnabled(false);
                }
            }
        });


    }




}