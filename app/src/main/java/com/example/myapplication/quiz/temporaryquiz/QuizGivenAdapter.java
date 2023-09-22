package com.example.myapplication.quiz.temporaryquiz;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
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


import com.example.myapplication.extra.AllreviewListActivity;
import com.example.myapplication.quiz.DateUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class QuizGivenAdapter extends RecyclerView.Adapter<QuizGivenAdapter.ViewHolder> {

    private Context context;
    private List<QuizSubmitModel> models;
    private String crsId, iKey, title;

    private DateUtils dateUtils;
    private double markTotal, increase, decrease;
    private boolean seeReview;



    public QuizGivenAdapter(Context context, List<QuizSubmitModel> models, double markTotal, String crsId
                           , String iKey, String title, double increase, double decrease, boolean seeReview) {
        this.context = context;
        this.models = models;
        this.markTotal = markTotal;
        this.crsId = crsId;
        this.iKey = iKey;
        this.title = title;
        this.increase = increase;
        this.decrease = decrease;
        this.seeReview = seeReview;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_submit_item, parent, false);
        return new QuizGivenAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final QuizSubmitModel model = models.get(position);

       /* if (studentModelList.size() == models.size()) {
            StudentModel studentModel = studentModelList.get(position);
            holder.username.setText(studentModel.getUsername());
            holder.roll.setText(studentModel.getRoll());
            Glide.with(context).load(studentModel.getImageUrl()).into(holder.profileImg);
        }*/

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

        holder.result.setText("" + model.getResult() + " / " + markTotal);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(context, AllreviewListActivity.class);
                    intent.putExtra("crsId", crsId);
                    intent.putExtra("dataKey", iKey);
                    intent.putExtra("title", title);
                    intent.putExtra("userId", model.getId());
                    intent.putExtra("increase",increase);
                    intent.putExtra("decrease", decrease);
                    intent.putExtra("seeReview", seeReview);
                    context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username, result, roll, publish;

        public CircleImageView profileImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            roll = itemView.findViewById(R.id.rollnoId);
            profileImg = itemView.findViewById(R.id.profile_image);
            publish = itemView.findViewById(R.id.publish_date);
            result = itemView.findViewById(R.id.result);
        }
    }


}
