package com.example.myapplication.account.studentwork;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.quiz.DateUtils;
import com.example.myapplication.quiz.QuizDetailsActivity;
import com.example.myapplication.quiz.QuizDetailsModel;
import com.example.myapplication.quiz.review.ReviewStartActivity;
import com.example.myapplication.quiz.temporaryquiz.QuizGivenListActivity;
import com.example.myapplication.quiz.temporaryquiz.QuizModificationActivity;
import com.example.myapplication.quiz.temporaryquiz.QuizStartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class QuizFontAdapter extends RecyclerView.Adapter<QuizFontAdapter.ViewHolder> {
    public static final int yellow_view = 1;
    public static final int red_view = 2;
    public static final int blue_view = 3;
    private Context context;
    private List<QuizDetailsModel> quizDetailsModels;

    DateUtils dateUtils;
    private String crsId;
    private String status;
    private boolean isRightTeacher;
    private boolean isTrans;

    public QuizFontAdapter(Context context, List<QuizDetailsModel> quizDetailsModels, String crsId, boolean isRightTeacher, String status, boolean isTrans) {
        this.context = context;
        this.quizDetailsModels = quizDetailsModels;
        this.crsId = crsId;
        this.isRightTeacher = isRightTeacher;
        this.status = status;
        this.isTrans = isTrans;
    }


   /* @Override
    public int getItemViewType(int position) {
        QuizDetailsModel model = quizDetailsModels.get(position);

        if (validExamck(model.getStrtTime(), model.getEndTime())){
            return blue_view;
        }else if (ckState(model.getEndTime())){
            return yellow_view;
        }else {
            return red_view;
        }
    }*/

    @NonNull
    @Override
    public QuizFontAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

      if (isTrans)
        view = LayoutInflater.from(context).inflate(R.layout.quiz_slide, parent, false);
      else view = LayoutInflater.from(context).inflate(R.layout.quiz_slide_white, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final QuizFontAdapter.ViewHolder holder, final int position) {
        final QuizDetailsModel model = quizDetailsModels.get(position);


        holder.rafSubmitTxt.setVisibility(View.GONE);
        holder.lineCountlayout.setVisibility(View.GONE);
        holder.erorImg.setVisibility(View.GONE);
        holder.cortnImg.setVisibility(View.GONE);
        holder.identifySubmit.setVisibility(View.GONE);
        holder.setPrepare.setVisibility(View.GONE);

        final boolean validExamOrNot = validExamck(model.getStrtTime(), model.getEndTime());


        // check you join early or not
        if (model.getPrepare().equals("1")) {
            holder.identifySubmit.setVisibility(View.VISIBLE);
            if (model.getSubmitCk() != null && status.equals("student")) {
                if (model.getSubmitCk().equals("1")) {
                    //  holder.erorImg.setVisibility(View.GONE);
                    holder.cortnImg.setVisibility(View.VISIBLE);
                } else {
                    //  holder.cortnImg.setVisibility(View.GONE);
                    holder.erorImg.setVisibility(View.VISIBLE);
                }
                holder.rafSubmitTxt.setVisibility(View.VISIBLE);
            }
            holder.lineCountlayout.setVisibility(View.VISIBLE);
            holder.countStudentTxt.setText("" + model.getCountStudent());
            holder.increseTxt.setText("(++" + model.getIncrease() + ") , ( --" + model.getDecrease() + " )");

        } else {
            holder.lineCountlayout.setVisibility(View.GONE);
            if (status.equals("teacher") && isRightTeacher) {
                holder.setPrepare.setVisibility(View.VISIBLE);
            }
        }


        // set color to identify the state of able to participated or not
        if (validExamOrNot) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Drawable col = context.getResources().getDrawable(R.drawable.custom_button);
                holder.identifySubmit.setBackground(col);

            } else {
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
            } else {
                int grn = context.getResources().getColor(R.color.orangeDark);
                holder.identifySubmit.setBackgroundColor(grn);
            }
        } else {
            Drawable col = context.getResources().getDrawable(R.drawable.state_red);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.identifySubmit.setBackground(col);
            } else {
                int grn = context.getResources().getColor(R.color.colorAccent);
                holder.identifySubmit.setBackgroundColor(grn);
            }
        }


        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
        long time = model.getPublish();
        dateUtils = new DateUtils();
        holder.publish.setText(closeCkDate(time));
        time = model.getStrtTime();
        holder.startTime.setText(dateUtils.dateFromLong(time));
        time = model.getEndTime();
        holder.endTime.setText(dateUtils.dateFromLong(time));
        holder.duration.setText(model.getDuration() + " s");
        holder.totalQsn.setText(model.getTotalQsn());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!model.getPrepare().equals("1")) {
                    Toast.makeText(context, "Its not prepared for exam", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(context, QuizGivenListActivity.class);
                intent.putExtra("crsId", crsId);
                intent.putExtra("iKey", model.getDataKey());
                double value = Double.parseDouble(model.getTotalQsn());
                if (model.getIncrease() != 0) {
                    value = value * model.getIncrease();
                }

                intent.putExtra("mark", value);
                intent.putExtra("total", model.getTotalQsn());
                intent.putExtra("title", model.getTitle());
                intent.putExtra("increase", model.getIncrease());
                intent.putExtra("decrease", model.getDecrease());
                intent.putExtra("decrease", model.getDecrease());
                intent.putExtra("seeReview", validExamOrNot);
                context.startActivity(intent);
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isRightTeacher || status.equals("student")) {
                    if ( isRightTeacher ||model.getPrepare().equals("1"))
                        showPopupMenu(holder.description, model.getDataKey(),
                                validExamOrNot, model.getDuration() * 1000, position);
                    else
                        Toast.makeText(context, "Its not prepared for exam", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Its a restriction for doing all operation when you are not a course teacher or a student", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        holder.setPrepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("details").child(model.getDataKey());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("prepare", "1");          ///prepare for access to quiz untill the qestion inserted
                reference1.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return quizDetailsModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, publish, increseTxt, startTime, endTime, duration, totalQsn, rafSubmitTxt, countStudentTxt;
        ImageView identifySubmit, erorImg, cortnImg, setPrepare;
        LinearLayout lineCountlayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.describtion);
            publish = itemView.findViewById(R.id.publish_date);
            startTime = itemView.findViewById(R.id.start_time);
            endTime = itemView.findViewById(R.id.end_time);
            duration = itemView.findViewById(R.id.duration);
            totalQsn = itemView.findViewById(R.id.amountQsn);

            identifySubmit = itemView.findViewById(R.id.identify_submit_img);
            cortnImg = itemView.findViewById(R.id.submit_correct);
            erorImg = itemView.findViewById(R.id.submit_error);
            rafSubmitTxt = itemView.findViewById(R.id.rafsubmit);
            countStudentTxt = itemView.findViewById(R.id.count_student_id);
            setPrepare = itemView.findViewById(R.id.prepare_for_exam);
            lineCountlayout = itemView.findViewById(R.id.line3);
            increseTxt = itemView.findViewById(R.id.increase_decrease_txt);
        }
    }


    public String closeCkDate(long times) {
        dateUtils = new DateUtils();
        Calendar calendar = Calendar.getInstance();
        long curentmilis = calendar.getTimeInMillis();
        long h = Math.abs(curentmilis - times);
        // if (curentmilis > times) {
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

     /*   } else {

            return dateUtils.dateFromLong(times);
        }*/


    }

    public boolean validExamck(long start, long end) {
        Calendar calender = Calendar.getInstance();
        long curentmilis = calender.getTimeInMillis();
        if (curentmilis > start && curentmilis < end) {
            return true;
        } else return false;
    }


    private void showPopupMenu(View view, String iKey, boolean validSubmit, long duration, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        if (status.equals("teacher")) {
            inflater.inflate(R.menu.quiz_title_tec_pop, popup.getMenu());
           /* if (quizDetailsModels.get(position).getPrepare().equals("1") && validSubmit){
                MenuItem edit = popup.getMenu().findItem(R.id.edit_popup);
                edit.setVisible(false);   // to handle the no edit when the period exam is running
            }*/
        } else {
            inflater.inflate(R.menu.quiz_title_stu_pop_menu, popup.getMenu());
            MenuItem start_exam = popup.getMenu().findItem(R.id.start_quiz_pop);
            //MenuItem show_review = popup.getMenu().findItem(R.id.action_show_review);

            if (!validSubmit || quizDetailsModels.get(position).getSubmitCk().equals("1") || quizDetailsModels.get(position).getSubmitCk() == null) {
                //disable the submit menu if time is over or not started
                start_exam.setVisible(false);
            }
           /* if (validSubmit){
                show_review.setVisible(false);
            }*/
        }


        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(iKey, duration, quizDetailsModels.get(position).getIncrease(),
                quizDetailsModels.get(position).getDecrease(), validSubmit));
        popup.show();
    }


    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        String iKey;
        long duration;
        double increase;
        double decrease;
        boolean seeReview;

        private MyMenuItemClickListener(String iKey, long duration, double increase, double decrease, boolean seeReview) {
            this.iKey = iKey;
            this.duration = duration;
            this.decrease = decrease;
            this.increase = increase;
            this.seeReview = seeReview;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.detail_popup: {

                    Intent intent = new Intent(context, QuizDetailsActivity.class);
                    intent.putExtra("crsId", crsId);
                    intent.putExtra("iKey", iKey);
                    context.startActivity(intent);
                    return true;
                }
                case R.id.delete_popup: {
                    ExampleAsyncTask task = new ExampleAsyncTask(crsId, iKey);
                    task.execute("do");
                    return true;
                }
                case R.id.edit_popup: {
                    Intent intent = new Intent(context, QuizModificationActivity.class);
                    intent.putExtra("crsId", crsId);
                    intent.putExtra("iKey", iKey);
                    context.startActivity(intent);
                    return true;
                }

                case R.id.action_show_review: {
                    if (seeReview) {
                        Toast.makeText(context, "Its only available when exam expired", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    Intent intent = new Intent(context, ReviewStartActivity.class);
                    intent.putExtra("iKey", iKey);
                    intent.putExtra("crsId", crsId);
                    intent.putExtra("increase", increase);
                    intent.putExtra("decrease", decrease);
                    context.startActivity(intent);
                    return true;

                }
                case R.id.start_quiz_pop: {
                    Intent intent = new Intent(context, QuizStartActivity.class);
                    intent.putExtra("iKey", iKey);
                    intent.putExtra("crsId", crsId);
                    intent.putExtra("increase", increase);
                    intent.putExtra("decrease", decrease);
                    intent.putExtra("duration", duration);
                    context.startActivity(intent);
                    return true;

                }
                case R.id.show_review_teacher: {

                    Intent intent = new Intent(context, ReviewStartActivity.class);
                    intent.putExtra("iKey", iKey);
                    intent.putExtra("crsId", crsId);
                    intent.putExtra("increase", increase);
                    intent.putExtra("decrease", decrease);
                    context.startActivity(intent);
                    return true;

                }

                default:
                    return false;
            }

        }
    }


    public boolean ckState(long times) {
        Calendar calendar = Calendar.getInstance();
        long curentmils = calendar.getTimeInMillis();
        if (curentmils < times) {
            return true;
        } else return false;
    }


    public static class ExampleAsyncTask extends AsyncTask<String, String, String> {
        String crsId, dataKey;

        public ExampleAsyncTask(String crsId, String dataKey) {
            this.crsId = crsId;
            this.dataKey = dataKey;
        }

        @Override
        protected String doInBackground(String... strings) {
            deleteItem();
            return null;
        }


        public void deleteItem() {
            final DatabaseReference reference0 = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("review").child(dataKey);
            reference0.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        reference0.removeValue();
                    } else {
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("collection").child(dataKey);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    reference.removeValue();
                                } else {
                                    final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("data").child(dataKey);
                                    reference1.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                reference1.removeValue();
                                            } else {
                                                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("quiz").child(crsId).child("details").child(dataKey);
                                                reference2.removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

}





















