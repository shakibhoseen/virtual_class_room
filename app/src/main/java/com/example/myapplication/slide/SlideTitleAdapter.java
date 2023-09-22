package com.example.myapplication.slide;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.account.assignment.AssignmentGivenListActivity;
import com.example.myapplication.account.assignment.AssignmentModifyActivity;
import com.example.myapplication.account.assignment.AssignmentTitileModel;
import com.example.myapplication.account.assignment.AssignmentUpload;
import com.example.myapplication.quiz.DateUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.transformation.TransformationChildCard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SlideTitleAdapter extends RecyclerView.Adapter<SlideTitleAdapter.ViewHolder> {

    private Context context;
    private List<SlideModel> slideModelList;

    DateUtils dateUtils;

   private String status, crsId, uId, teacherId;
   private boolean isTrans;


    public SlideTitleAdapter(Context context, List<SlideModel> slideModelList, String crsId, String teacherId, boolean isTrans) {
        this.context = context;
        this.slideModelList = slideModelList;
        this.crsId = crsId;
        this.teacherId = teacherId;
        this.isTrans = isTrans;
        SharedPreferences preferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        status  = preferences.getString("status", "");
        uId = FirebaseAuth.getInstance().getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isTrans)
            view = LayoutInflater.from(context).inflate(R.layout.slide_item_title, parent, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.slide_item_white, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final SlideModel model = slideModelList.get(position);


        holder.dropDown.setVisibility(View.VISIBLE);
        holder.linearDownLoad.setVisibility(View.GONE);

       holder.title.setText(model.getFileName());
       holder.description.setText(model.getDescription());
        long time = model.getPublish();
        dateUtils = new DateUtils();
        holder.publish.setText(closeCkDate(time));

        holder.dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.dropDown.setVisibility(View.GONE);
                holder.dropUp.setVisibility(View.VISIBLE);
                holder.linearDownLoad.setVisibility(View.VISIBLE);
            }
        });
        holder.dropUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.dropDown.setVisibility(View.VISIBLE);
                holder.dropUp.setVisibility(View.GONE);
                holder.linearDownLoad.setVisibility(View.GONE);
            }
        });

        holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveStoragePermission())
                downloadFile(model.getFileName(),"",model.getFileUrl());
                else Toast.makeText(context, "Allow the permission and try again", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (status.equals("teacher") && uId.equals(teacherId)) {
                    showPopupMenu(holder.description, model.getDataKey(), position);
                    return true;
                }
                return false;
            }
        });

        String iconHold = getIconByExtension(model.getFileName()).toLowerCase();
        if (iconHold != ""){
            if(iconHold.equals(".doc") || iconHold.equals(".docx")){
                holder.fileIcon.setImageResource(R.drawable.docs_icon);
            }else if(iconHold.equals(".zip") || iconHold.equals(".rar")){
                holder.fileIcon.setImageResource(R.drawable.zip_icon);
            }else if(iconHold.equals(".pdf") ){
                holder.fileIcon.setImageResource(R.drawable.pdf_icon);
            }else if(iconHold.equals(".mp4") || iconHold.equals(".mkv") || iconHold.equals(".avi")){
                holder.fileIcon.setImageResource(R.drawable.mp4_icon);
            }else if(iconHold.equals(".excel") ){
                holder.fileIcon.setImageResource(R.drawable.excel_icon);
            }
        }



    }

    @Override
    public int getItemCount() {
        return slideModelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, publish, startTime, endTime, rafSubmitTxt;
        ImageView dropDown,dropUp, fileIcon;
        LinearLayout linearDownLoad;
        Button downloadBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.describtion);
            publish = itemView.findViewById(R.id.publish_date);
            downloadBtn = itemView.findViewById(R.id.download_btn_id);
            linearDownLoad = itemView.findViewById(R.id.line1);
            dropDown = itemView.findViewById(R.id.dropdown_id);
            dropUp = itemView.findViewById(R.id.dropup_id);
            fileIcon = itemView.findViewById(R.id.file_icon_id);

        }

    }


    public String closeCkDate(long times) {
        dateUtils = new DateUtils();
        Calendar calendar = Calendar.getInstance();
        long curentmilis = calendar.getTimeInMillis();
        long day = 86400000;
        long fnal;
        long h = Math.abs(curentmilis - times);

            if (h < 172800000) {  //use to update 2 days
                if (h < 86400000) {//convert hour
                    if (h < 3600000) {//minute
                        if (h < 60000) {
                            return "" + h / 1000 + " seconds ago";
                        } else
                            return "" + h / 60000 + " minutes ago";
                    } else {
                        return "" + h / 3600000 + " hour ago";
                    }
                } else {
                    return "1 day ago";
                }
            } else {  //greatter 2 days
                return dateUtils.dateFromLong(times);
            }

    }



    private void showPopupMenu(View view, String iKey, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();

        inflater.inflate(R.menu.assing_title_tec_pop_menu, popup.getMenu());


        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(iKey, position));
        popup.show();
    }


    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        String iKey;
        int position;

        public MyMenuItemClickListener(String iKey, int position) {
            this.iKey = iKey;
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.detail_popup:
                    Toast.makeText(context, "details", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.delete_popup:
                  deleteItem(iKey, slideModelList.get(position).getFileUrl() );

                    return true;
                case R.id.edit_popup: {

                    return true;
                }

                default:
                    return false;
            }

        }
    }


    public void downloadFile(String filename, String distinationDirectory, String url) {
        //create file
        // File file = new File(Environment.getExternalStorageDirectory(),"mypdf.pdf");
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(filename);
        //.setDestinationUri(Uri.fromFile(file));
        // request.setDestinationInExternalFilesDir(context,distinationDirectory,filename+fileExtention);

        request.allowScanningByMediaScanner();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        downloadManager.enqueue(request);
    }


    public boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error", "You have permission");
                return true;
            } else {

                Log.e("Permission error", "You have asked for permission");
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error", "You already have the permission");
            return true;
        }
    }



    public void deleteItem(final String dataKey, String url){

       final StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("slide").child(crsId).child("collection")
                                .child(dataKey);
                        reference.removeValue();

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
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("slide").child(crsId).child("collection")
                        .child(dataKey);
                reference.removeValue();
            }
        });

    }


    public String getIconByExtension(String path){
        String extension = path.substring(path.lastIndexOf("."));
        return extension;
    }


}
