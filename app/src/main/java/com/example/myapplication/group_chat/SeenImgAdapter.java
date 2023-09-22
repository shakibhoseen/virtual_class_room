package com.example.myapplication.group_chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.quiz.DateUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SeenImgAdapter extends RecyclerView.Adapter<SeenImgAdapter.ViewHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context mcontext;
    private List<SeenList> seenLists;
    private String imgUrl;
    private String firebaseUId;
    private DateUtils dateUtils;


    public SeenImgAdapter(Context mcontext, List<SeenList> seenLists) {
        firebaseUId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.mcontext = mcontext;
        this.seenLists = seenLists;
        this.imgUrl = imgUrl;


    }

      /*  @Override
    public int getItemViewType(int position) {

        if (firebaseUId.equals(seenLists.get(position).getSender())) {
            return MSG_TYPE_RIGHT;
        } else
            return MSG_TYPE_LEFT;
    }*/

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        /* if (i == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left, viewGroup, false);
            return new SeenImgAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right, viewGroup, false);
            return new SeenImgAdapter.ViewHolder(view);
        }*/
        View view = LayoutInflater.from(mcontext).inflate(R.layout.seen_list_img_item, viewGroup, false);
        return new SeenImgAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final SeenList seen = seenLists.get(i);


         if (seen.getOwnImg().equals("default") || seen.getOwnImg()== null)
             viewHolder.circleImageView.setImageResource(R.mipmap.ic_launcher);
         else
        Glide.with(mcontext.getApplicationContext()).load(seen.getOwnImg()).into(viewHolder.circleImageView);




    }

    @Override
    public int getItemCount() {
        return seenLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           circleImageView = itemView.findViewById(R.id.seen_img);

        }
    }



}
