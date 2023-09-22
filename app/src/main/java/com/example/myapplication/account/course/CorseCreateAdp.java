package com.example.myapplication.account.course;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.R;

import com.example.myapplication.account.studentwork.ShowCourseDocument;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CorseCreateAdp extends RecyclerView.Adapter<CorseCreateAdp.ViewHolder> {


    private Context context;
    private List<CourseModel> courseModelList;
    private FragmentManager fragmentManager;
    private String myid = FirebaseAuth.getInstance().getUid();
    private String status;
    private String fromActivity;
    private String uId;


    public CorseCreateAdp(Context context, List<CourseModel> courseModelList) {
        this.context = context;
        this.courseModelList = courseModelList;
        uId =FirebaseAuth.getInstance().getUid();
        SharedPreferences preferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        status = preferences.getString("status", "");
    }

    public CorseCreateAdp(Context context, List<CourseModel> courseModelList, String fromActivity) {
        this.context = context;
        this.courseModelList = courseModelList;
        this.fromActivity = fromActivity;/// point out its student self class to show the popup
        uId =FirebaseAuth.getInstance().getUid();
        SharedPreferences preferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        status = preferences.getString("status", "");
    }

    public CorseCreateAdp(Context context, List<CourseModel> courseModelList, FragmentManager fragmentManager) {
        this.context = context;
        this.courseModelList = courseModelList;
        this.fragmentManager = fragmentManager;
        uId =FirebaseAuth.getInstance().getUid();
        SharedPreferences preferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        status = preferences.getString("status", "");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.bach_item2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final CourseModel model = courseModelList.get(position);
        holder.courseName.setText(model.getCourseName());
        holder.batchName.setText(model.getBatchName());
        holder.courseCode.setText(model.getCourseCode());
        holder.teacherNm.setText(model.getTeacherName());

        if (model.getImageUrl()!=null){
            Picasso.with(context).load(model.getImageUrl()).into(holder.img);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentManager != null) {
                    if (myid.equals(model.getTeacherId())) {
                        Intent intent = new Intent(context, ShowCourseDocument.class);
                        intent.putExtra("crsId", model.getCourseId());
                        intent.putExtra("crsNm", model.getCourseName());
                        intent.putExtra("teacherId", model.getTeacherId());
                        context.startActivity(intent);
                    } else {
                    // if (status.equals("student")) {
                         TestDialog testDialog = new TestDialog(model.getSecurityKey(), model.getCourseName(), model.getCourseId());
                         testDialog.show(fragmentManager, "dialog");
                     /*}else {
                         Toast.makeText(context, "you have no permission until you are not a student or not a owner of the course", Toast.LENGTH_SHORT).show();
                     }*/

                    }
                } else {
                    Intent intent = new Intent(context, ShowCourseDocument.class);
                    intent.putExtra("crsId", model.getCourseId());
                    intent.putExtra("crsNm", model.getCourseName());
                    intent.putExtra("teacherId", model.getTeacherId());
                    context.startActivity(intent);
                }
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (fromActivity != null)
                    if (status.equals("student") && fromActivity.equals("myCourse")) {
                        showPopupMenu(holder.batchName, false, model.getCourseId());

                    }
                else if (status.equals("teacher")&& model.getTeacherId().equals(uId)){
                       showPopupMenu(holder.teacherNm, true, model.getCourseId());
                    }

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView courseName, batchName, courseCode ,teacherNm;
        private ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.courseNameId);
            batchName = itemView.findViewById(R.id.batchNameId);
            courseCode = itemView.findViewById(R.id.courseCodeId);
            img = itemView.findViewById(R.id.profile_image);
            teacherNm = itemView.findViewById(R.id.teachernameId);
        }
    }


    private void showPopupMenu(View view, boolean status, String crsId) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        if (status){
            //inflater.inflate(R.menu.my_corse_teacher_menu, popup.getMenu());// this work not actually is futuyre not mandatory
        }
        else {
            inflater.inflate(R.menu.mycourse_student_pop_menu, popup.getMenu());

        }

        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(crsId));
        popup.show();
    }


    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        String crsId;

        public MyMenuItemClickListener(String crsId) {
            this.crsId = crsId;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.remove_popup: {

                    removeCourse(crsId);
                    return true;
                }
               /* case R.id.deleteId: {
                    deleteCrs(crsId);
                }*/

                default:
                    return false;
            }

        }
    }


    public void removeCourse(String crsId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("wishlist").child(crsId);

        reference.child(uId).removeValue();
    }


    public  void deleteCrs(final String crsId){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("wishlist").child(crsId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    reference.removeValue();
                    deleteFromCrs(crsId);
                }else {
                    deleteFromCrs(crsId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

  public void deleteFromCrs(String id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("course")
                .child(id);
        reference.removeValue();
  }




}
