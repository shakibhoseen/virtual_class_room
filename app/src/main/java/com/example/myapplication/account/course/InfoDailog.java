package com.example.myapplication.account.course;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.account.teacher.RegisterTeacherActivity;

public class InfoDailog extends AppCompatDialogFragment {

    private Button infoBtn;
    private TextView infoTxt;

    private String type;

    public InfoDailog(String type) {
        this.type = type;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.info_dialog,null);
          infoBtn = view.findViewById(R.id.infoOkId);
          infoTxt = view.findViewById(R.id.info_text_id);
        builder.setView(view);

         if (type.equals("student")){
             infoTxt.setText(R.string.info_text_student);
         }else if(type.equals("teacher")){
             infoTxt.setText(R.string.info_text_teacher);
         }else{
             infoTxt.setText(R.string.info_text);
         }


        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });


        return builder.create();
    }
}
