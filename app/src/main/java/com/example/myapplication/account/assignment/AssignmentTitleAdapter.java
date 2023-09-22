package com.example.myapplication.account.assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.account.studentwork.ShowCourseDocument;
import com.example.myapplication.quiz.DateUtils;
import com.example.myapplication.quiz.QuizDetailsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AssignmentTitleAdapter extends RecyclerView.Adapter<AssignmentTitleAdapter.ViewHolder> {

    private Context context;
    private List<AssignmentTitileModel> assignmentTitileModells;

    DateUtils dateUtils;
    private String crsId, corseName, teacherId, uId;
    private String status;
    private boolean isRightTeacher;
    boolean isTrans;


    public AssignmentTitleAdapter(Context context, List<AssignmentTitileModel> assignmentTitileModells, String crsId, String corseName, boolean isRightTeacher, boolean isTrans) {
        this.context = context;
        this.assignmentTitileModells = assignmentTitileModells;
        this.crsId = crsId;
        this.corseName = corseName;
        this.isRightTeacher = isRightTeacher;
        uId = FirebaseAuth.getInstance().getUid();
        SharedPreferences preferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        status = preferences.getString("status", "");

        this.isTrans = isTrans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isTrans)
          view = LayoutInflater.from(context).inflate(R.layout.assignment_font_slide, parent, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.assignment_font_slide_white, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final AssignmentTitileModel model = assignmentTitileModells.get(position);

        holder.rafSubmitTxt.setVisibility(View.GONE);
        final boolean validExamOrNot =  validExamck(model.getStrtTime(), model.getEndTime());

        if ( status.equals("student") && model.getSubmitCk()!= null) {
            if (model.getSubmitCk().equals("1")) {
                holder.erorImg.setVisibility(View.GONE);
                holder.cortnImg.setVisibility(View.VISIBLE);
                holder.rafSubmitTxt.setVisibility(View.VISIBLE);
            } else {
                holder.cortnImg.setVisibility(View.GONE);
                holder.erorImg.setVisibility(View.VISIBLE);
                holder.rafSubmitTxt.setVisibility(View.VISIBLE);
            }

        }

        holder.countStudentTxt.setText(""+model.getCountStudent());

        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
        long time = model.getPublish();
        dateUtils = new DateUtils();
        holder.publish.setText(closeCkDate(time));
        time = model.getStrtTime();
        holder.startTime.setText(dateUtils.dateFromLong(time));
        time = model.getEndTime();
        holder.endTime.setText(dateUtils.dateFromLong(time));

        if (validExamOrNot) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Drawable col = context.getResources().getDrawable(R.drawable.custom_button);
                holder.identifySubmit.setBackground(col);
            }else{
                int grn = context.getResources().getColor(R.color.greenDark);
                holder.identifySubmit.setBackgroundColor(grn);
            }

        } else if (ckState(model.getEndTime())) {
            holder.rafSubmitTxt.setVisibility(View.GONE);
            holder.cortnImg.setVisibility(View.GONE);
            holder.erorImg.setVisibility(View.GONE);
            Drawable col = context.getResources().getDrawable(R.drawable.state_yellow);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.identifySubmit.setBackground(col);
            }else{
                  int grn = context.getResources().getColor(R.color.yellow);
                holder.identifySubmit.setBackgroundColor(grn);
            }
        } else {
            Drawable col = context.getResources().getDrawable(R.drawable.state_red);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.identifySubmit.setBackground(col);
            }else{
                  int grn = context.getResources().getColor(R.color.colorAccent);
                holder.identifySubmit.setBackgroundColor(grn);
            }
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (status.equals("teacher") && !isRightTeacher) {
                   Toast.makeText(context, "No permission to doing operation", Toast.LENGTH_SHORT).show();
                   return;
               }
                Intent intent = new Intent(context, AssignmentGivenListActivity.class);
                intent.putExtra("crsId", crsId);
                intent.putExtra("iKey", model.getDataKey());
                intent.putExtra("title",model.getTitle());
                context.startActivity(intent);

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                     if (isRightTeacher || status.equals("student")) {
                         showPopupMenu(holder.identifySubmit, status.equals("teacher"), model.getDataKey(),
                                 validExamOrNot, position);
                         holder.identifySubmit.animate().rotation(400);
                         holder.img.animate().rotationY(180);
                         return true;
                     }else {
                        Toast.makeText(context, "Its a restriction for doing all operation when you are not a course teacher or a student", Toast.LENGTH_SHORT).show();

                         return true;
                     }
            }
        });


    }

    @Override
    public int getItemCount() {
        return assignmentTitileModells.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, publish, startTime, endTime, rafSubmitTxt, countStudentTxt;
        ImageView identifySubmit, erorImg, cortnImg, img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.describtion);
            publish = itemView.findViewById(R.id.publish_date);
            startTime = itemView.findViewById(R.id.start_time);
            endTime = itemView.findViewById(R.id.end_time);
            identifySubmit = itemView.findViewById(R.id.identify_submit_img);
            cortnImg = itemView.findViewById(R.id.submit_correct);
            erorImg = itemView.findViewById(R.id.submit_error);
            img = itemView.findViewById(R.id.imageView);
            rafSubmitTxt = itemView.findViewById(R.id.rafsubmit);
            countStudentTxt = itemView.findViewById(R.id.count_student_id);

        }

    }


    public String closeCkDate(long times) {
        dateUtils = new DateUtils();
        Calendar calendar = Calendar.getInstance();
        long curentmilis = calendar.getTimeInMillis();
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

    public boolean validExamck(long start, long end) {
        Calendar calender = Calendar.getInstance();
        long curentmilis = calender.getTimeInMillis();
        if (curentmilis > start && curentmilis < end) {
            return true;
        } else return false;
    }

    public boolean ckState(long times) {
        Calendar calendar = Calendar.getInstance();
        long curentmils = calendar.getTimeInMillis();
        if (curentmils < times) {
            return true;
        } else return false;
    }


    private void showPopupMenu(View view, boolean x, String iKey, boolean validSubmit, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        if (x)
            inflater.inflate(R.menu.assing_title_tec_pop_menu, popup.getMenu());
        else {
            inflater.inflate(R.menu.assing_title_stu_pop_menu, popup.getMenu());
            MenuItem submit = popup.getMenu().findItem(R.id.submit_popup);

            if (!validSubmit || assignmentTitileModells.get(position).getSubmitCk()== null || assignmentTitileModells.get(position).getSubmitCk().equals("1")) {
                //disable the submit menu if time is over or not started
                submit.setVisible(false);
            }
        }


        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(iKey, position));
        popup.show();
    }


    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        String iKey; int position;

        public MyMenuItemClickListener(String iKey, int position) {
            this.iKey = iKey;
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.detail_popup: {
                    Intent intent = new Intent(context, AssignDetailsActivity.class);
                    intent.putExtra("crsId", crsId);
                    intent.putExtra("iKey", iKey);
                    context.startActivity(intent);
                    return true;
                   }
                case R.id.delete_popup:
                    ExampleAsyncTask task = new ExampleAsyncTask(crsId, iKey, context);
                    task.execute("do");
                    return true;
                case R.id.edit_popup: {
                    Intent intent = new Intent(context, AssignmentModifyActivity.class);
                    intent.putExtra("crsId", crsId);
                    intent.putExtra("iKey", iKey);
                    context.startActivity(intent);
                    return true;
                }

                case R.id.submit_popup: {

                    Intent intent = new Intent(context, AssignmentUpload.class);
                    intent.putExtra("crsId", crsId);
                    intent.putExtra("iKey", iKey);
                    intent.putExtra("title",assignmentTitileModells.get(position).getTitle());
                    intent.putExtra("crsNm",corseName);
                    context.startActivity(intent);

                }

                return true;

                default:
                    return false;
            }

        }
    }








    public static class ExampleAsyncTask extends AsyncTask<String, Integer, String>{
        private ProgressDialog progressDialog ;
        private int result =0;
        private  String crsId, iKey;
      private WeakReference<Context> weakReference;

        public ExampleAsyncTask(String crsId, String iKey, Context context ) {
            this.crsId = crsId;
            this.iKey = iKey;
            weakReference = new WeakReference<>(context);
            progressDialog = new ProgressDialog(context);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Context mcontext = weakReference.get();
            if (mcontext==null || mcontext.isRestricted()){
                return;
            }
            progressDialog.setMessage("Deleting");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            deleteItem();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Context mcontext = weakReference.get();
            if (mcontext==null || mcontext.isRestricted()){
                return;
            }
            if (values[0]==234){
                progressDialog.dismiss();
                Toast.makeText(mcontext, "Opps something were wrong. Try again", Toast.LENGTH_SHORT).show();
            }else if (values[0]==514){
                Toast.makeText(mcontext, "successfully deleted", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }else {
                progressDialog.setMessage(""+result+" deleting of ");
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }



        public  void deleteOnbyOn(final String snapKey, String url, final String dataKey){

            final   StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);


            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DatabaseReference s = FirebaseDatabase.getInstance().getReference("assignment").child(crsId).
                                    child("collection").child(dataKey).child(snapKey);
                            s.removeValue();
                            onProgressUpdate(++result);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                             onProgressUpdate(234);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    DatabaseReference s = FirebaseDatabase.getInstance().getReference("assignment").child(crsId).
                            child("collection").child(dataKey).child(snapKey);
                    s.removeValue();
                    onProgressUpdate(++result);
                }
            });

        }



        public void deleteItem(){
            final List<String> filelist = new ArrayList<>();
            final List<String> idlist = new ArrayList<>();

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("assignment").child(crsId)
                    .child("collection").child(iKey);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        filelist.clear();
                        idlist.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            AssignmentSubmitModel model = snapshot.getValue(AssignmentSubmitModel.class);
                            filelist.add(model.getFileUrl());
                            idlist.add(snapshot.getKey());
                        }

                        for (int i = 0; i < idlist.size(); i++) {
                            deleteOnbyOn(idlist.get(i), filelist.get(i), iKey);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }else {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("assignment").child(crsId)
                                .child("details").child(iKey);
                        reference.removeValue();
                        onProgressUpdate(514);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }




    }




}
