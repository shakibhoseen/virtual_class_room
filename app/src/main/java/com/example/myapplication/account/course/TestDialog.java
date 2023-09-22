package com.example.myapplication.account.course;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.account.studentwork.ShowCourseDocument;

import com.google.android.material.textfield.TextInputLayout;

public class TestDialog extends AppCompatDialogFragment {
    TextInputLayout inputLayout;
    Button btnEnter;
    String key;
    String courseName;
    TextView title;
    String courseCode ;
    String crsId;//uniq

    public TestDialog(String key , String courseName, String crsId ) {
        this.key = key;
        this.courseName = courseName;
        this.crsId = crsId;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.test_secure_dialog,null);

        inputLayout = view.findViewById(R.id.securityKeyId);
        btnEnter = view.findViewById(R.id.enter_btnId);
        title = view.findViewById(R.id.title);
        title.setText(courseName);


       builder.setView(view);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = inputLayout.getEditText().getText().toString();

                if(s.equals("")){
                    inputLayout.setError("please enter key");
                } else if(s.equals(key)&& !key.equals("")){
                    Intent intent = new Intent(getActivity(), ShowCourseDocument.class);
                    intent.putExtra("crsId",crsId);
                    intent.putExtra("crsNm",courseName);
                    getActivity().startActivity(intent);
                    dismiss();
                }else{
                    inputLayout.setError("wrong keyword");
                }
            }
        });


        return builder.create();
    }
}
