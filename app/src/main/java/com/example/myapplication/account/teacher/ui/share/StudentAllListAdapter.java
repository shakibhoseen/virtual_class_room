package com.example.myapplication.account.teacher.ui.share;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.StudentDetailsActivity;
import com.example.myapplication.account.StudentModel;
import com.example.myapplication.account.assignment.AssignmentSubmitModel;
import com.example.myapplication.quiz.DateUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class StudentAllListAdapter extends RecyclerView.Adapter<StudentAllListAdapter.ViewHolder>  {

    private Context context;
    private List<StudentModel> models;




    public StudentAllListAdapter(Context context, List<StudentModel> models) {
        this.context = context;
        this.models = models;


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StudentAllListAdapter.ViewHolder( LayoutInflater.from(context).inflate(R.layout.all_student_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final StudentModel model = models.get(position);



        //this is another way to set information to get advantage searchview
        holder.username.setText(model.getUsername());
        holder.roll.setText(model.getRoll());
        holder.email.setText(model.getEmail());
        if (model.getImageUrl()!=null) {
            if (!model.getImageUrl().equals("") && !model.getImageUrl().equals("default"))
                Glide.with(context).load(model.getImageUrl()).into(holder.profileImg);
            else holder.profileImg.setImageResource(R.drawable.meena);
        }
        else holder.profileImg.setImageResource(R.drawable.meena);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StudentDetailsActivity.class);
                intent.putExtra("userId", model.getId());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username, email, roll;

        public CircleImageView profileImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.filename);
            roll = itemView.findViewById(R.id.rollnoId);
            profileImg = itemView.findViewById(R.id.profile_image);

        }
    }





}
