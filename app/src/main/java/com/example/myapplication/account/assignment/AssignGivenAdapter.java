package com.example.myapplication.account.assignment;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.account.StudentModel;
import com.example.myapplication.quiz.DateUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class AssignGivenAdapter extends RecyclerView.Adapter<AssignGivenAdapter.ViewHolder>  {

    private Context context;
    private List<AssignmentSubmitModel> models;
    private String crsId;
    private String iKey;

    public ProgressDialog pDialog;
    DateUtils dateUtils;

    private String status ;
    private String fireUserId = FirebaseAuth.getInstance().getUid().toString();

    OnItemOneDeleteClickListener oneDeleteClickListener;
    OnDownloadClickListener downloadClickListener;
    public interface OnItemOneDeleteClickListener{
        void OnItemOneDelete(int position);
    }

    public void setOnItemOneDeleteClickListener(OnItemOneDeleteClickListener listener){
          oneDeleteClickListener = listener;
    }


    public interface OnDownloadClickListener{
        void OnDownLoadClick(int position);
    }

    public void setOnDownloadClickListener(OnDownloadClickListener listener){
        downloadClickListener = listener;
    }


    public AssignGivenAdapter(Context context, List<AssignmentSubmitModel> models, String crsId, String iKey) {
        this.context = context;
        this.models = models;
        this.crsId = crsId;
        this.iKey = iKey;
        SharedPreferences preferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        status = preferences.getString("status", "");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AssignGivenAdapter.ViewHolder( LayoutInflater.from(context).inflate(R.layout.assignment_submit_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final AssignmentSubmitModel model = models.get(position);
        holder.filename.setText(model.getFileName());



        if (!status.equals("") && status.equals("teacher") || model.getId().equals(fireUserId)){
            holder.downloadBtn.setVisibility(View.VISIBLE);
        }else {
            holder.downloadBtn.setVisibility(View.GONE);
        }

        //this is another way to set information to get advantage searchview
        holder.username.setText(model.getUsername());
        holder.roll.setText(model.getRoll());
        if (model.getImageUrl()!=null) {
            if (!model.getImageUrl().equals("") && !model.getImageUrl().equals("default"))
                Glide.with(context).load(model.getImageUrl()).into(holder.profileImg);
            else holder.profileImg.setImageResource(R.drawable.meena);
        }
        else holder.profileImg.setImageResource(R.drawable.meena);

        dateUtils = new DateUtils();
        long time = model.getPublish();
        holder.publish.setText(dateUtils.dateFromLong(time));


      /*  holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, "nice", Toast.LENGTH_SHORT).show();
              *//*  Intent intent = new Intent(context, DownloadActivity.class);
                intent.putExtra("url",model.getFileUrl());
                intent.putExtra("fileName",model.getFileName());
                context.startActivity(intent);*//*
                // download(model.getFileUrl(), model.getFileName());
                if (haveStoragePermission()) {
                    downloadFile(model.getFileName(), "SD card/vartual class/", model.getFileUrl());
                } else {
                    Toast.makeText(context, "Allow the permission", Toast.LENGTH_SHORT).show();
                }

            }
        });*/


       /* holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (status.equals("teacher")){
                showPopupMenu(holder.filename ,model.getSnapKey(), model.getFileUrl() );
                    return true;
                }
                return false;
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return models.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username, filename, roll, publish;
        public ImageButton downloadBtn;
        public CircleImageView profileImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            filename = itemView.findViewById(R.id.filename);
            roll = itemView.findViewById(R.id.rollnoId);
            downloadBtn = itemView.findViewById(R.id.downloadBtnId);
            profileImg = itemView.findViewById(R.id.profile_image);
            publish = itemView.findViewById(R.id.publish_date);

            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (downloadClickListener!=null){
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            downloadClickListener.OnDownLoadClick(position);
                        }
                    }
                }
            });

           itemView.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                   int position = getAdapterPosition();
                   if (position!=RecyclerView.NO_POSITION){
                       AssignmentSubmitModel model = models.get(position);
                       if (status.equals("teacher")){
                           showPopupMenu(filename ,model.getSnapKey(), model.getFileUrl() );
                           return true;
                       }
                   }

                   return false;

               }
           });





        }
    }






    public  void deleteOne(final String snapKey, String url) {

        final StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);


        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DatabaseReference s = FirebaseDatabase.getInstance().getReference("assignment").child(crsId).
                                child("collection").child(iKey).child(snapKey);
                        s.removeValue();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                DatabaseReference s = FirebaseDatabase.getInstance().getReference("assignment").child(crsId).
                        child("collection").child(iKey).child(snapKey);
                s.removeValue();
            }
        });
    }





    private void showPopupMenu(View view, String snapKey, String url) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        if (status.equals("teacher"))
            inflater.inflate(R.menu.assign_given_menu, popup.getMenu());
        else {

        }


        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(snapKey, url));
        popup.show();
    }


    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        String snapKey,  url;

        public MyMenuItemClickListener(String snapKey,  String url) {
            this.snapKey = snapKey;
            this.url = url;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.delete_popup:
                    deleteOne(snapKey, url);
                    return true;

                default:
                    return false;
            }

        }
    }

}
