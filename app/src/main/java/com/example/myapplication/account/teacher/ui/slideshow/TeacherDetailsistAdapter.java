package com.example.myapplication.account.teacher.ui.slideshow;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.StudentDetailsActivity;
import com.example.myapplication.account.StudentModel;
import com.example.myapplication.account.teacher.TeacherDetailsActivity;
import com.example.myapplication.account.teacher.TeacherModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherDetailsistAdapter extends RecyclerView.Adapter<TeacherDetailsistAdapter.ViewHolder>  {

    private Context context;
    private List<TeacherModel> models;




    public TeacherDetailsistAdapter(Context context, List<TeacherModel> models) {
        this.context = context;
        this.models = models;


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TeacherDetailsistAdapter.ViewHolder( LayoutInflater.from(context).inflate(R.layout.teacher_show_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final TeacherModel model = models.get(position);



        //this is another way to set information to get advantage searchview
        holder.username.setText(model.getName());
        holder.phone.setText(model.getPhone());
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
                 Intent intent = new Intent(context, TeacherDetailsActivity.class);
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
        public TextView username, email, phone;

        public CircleImageView profileImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone_id);
            profileImg = itemView.findViewById(R.id.profile_image);

        }
    }





}
