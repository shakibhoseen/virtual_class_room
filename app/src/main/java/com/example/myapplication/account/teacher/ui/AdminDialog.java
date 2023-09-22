package com.example.myapplication.account.teacher.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.account.teacher.AdminMActivity;
import com.example.myapplication.account.teacher.SecureModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDialog extends AppCompatDialogFragment {
    TextInputLayout inputLayout;
    Button btnEnter;
    String adminKey;

    TextView title;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.admin_secure_dialog,null);

        inputLayout = view.findViewById(R.id.securityKeyId);
        btnEnter = view.findViewById(R.id.enter_btnId);
        title = view.findViewById(R.id.title);
        title.setText("Admin Panel");


       builder.setView(view);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = inputLayout.getEditText().getText().toString();

                if (adminKey == null){
                    inputLayout.setError("please check your network connection");
                    return;
                }

                if(s.equals("")){
                    inputLayout.setError("please enter key");
                } else if(s.equals(adminKey)&& !adminKey.equals("")){
                    Intent intent = new Intent(getActivity(), AdminMActivity.class);

                    getActivity().startActivity(intent);
                    dismiss();
                }else{
                    inputLayout.setError("wrong keyword");
                }
            }
        });

        loadAdminPass();
        return builder.create();
    }


    private void loadAdminPass(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("security");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()){
                    SecureModel model = child.getValue(SecureModel.class);
                    adminKey = model.getAdmin();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
