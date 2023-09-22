package com.example.myapplication.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.StartActivity;
import com.example.myapplication.account.teacher.RegisterTeacherActivity;

public class OptionDailog extends AppCompatDialogFragment {

    private Button studentOpbtn ,teacherOpbtn, adminOpbtn;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.option_user_dailog_layout,null);

        builder.setView(view);
          studentOpbtn = view.findViewById(R.id.stusentOpId);
          teacherOpbtn = view.findViewById(R.id.teacherOpId);


          studentOpbtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  startActivity(new Intent(getContext(), RegisterActivity.class));
                  dismiss();
              }
          });
          teacherOpbtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  startActivity(new Intent(getContext(), RegisterTeacherActivity.class));
                  dismiss();
              }
          });


        return builder.create();
    }
}
