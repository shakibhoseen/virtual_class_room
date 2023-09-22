package com.example.myapplication.group_chat;

import android.app.Activity;
import android.content.Context;


import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.quiz.DateUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context mcontext, sensitive;
    private List<Chat> mchat;
    private List<SeenList> list_seen;
    private String imgUrl;
    private String firebaseUId;
    private DateUtils dateUtils;


    public MessageAdapter(Context mcontext, List<Chat> chats, String imgUrl) {
        firebaseUId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.mcontext = mcontext;
        this.mchat = chats;
        this.imgUrl = imgUrl;
        dateUtils = new DateUtils();


    }

    @Override
    public int getItemViewType(int position) {

        if (firebaseUId.equals(mchat.get(position).getSender())) {
            return MSG_TYPE_RIGHT;
        } else
            return MSG_TYPE_LEFT;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        sensitive = viewGroup.getContext();
        if (i == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(sensitive).inflate(R.layout.chat_item_left, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(sensitive).inflate(R.layout.chat_item_right, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Chat chat = mchat.get(i);
        int n = mchat.size();
        String click = null;
        if (i != 0) {
            Chat beChat = mchat.get(i - 1);

            String disBeMsg = ShowTimeOrNot(beChat.getPublish(), chat.getPublish());
            click = disBeMsg;

            if (disBeMsg.equals("tny")) {

                viewHolder.recent_time.setText(TimeFormat(chat.getPublish()));
                viewHolder.recent_time.setVisibility(View.GONE);
                viewHolder.past_time.setVisibility(View.GONE);

                if (i < n - 1) {

                    Chat afChat = mchat.get(i + 1);
                    String disAfMsg = ShowTimeOrNot(chat.getPublish(), afChat.getPublish());
                    if (afChat.getSender().equals(chat.getSender()) && !disAfMsg.equals("big")) {     // matching two text sender is same
                        viewHolder.profile_image.setVisibility(View.INVISIBLE);

                    } else {
                        viewHolder.profile_image.setVisibility(View.VISIBLE);
                    }

                } else {
                    viewHolder.profile_image.setVisibility(View.VISIBLE);
                }

                if (beChat.getSender().equals(chat.getSender())) {
                    viewHolder.sender_name.setVisibility(View.GONE);
                } else {
                    viewHolder.sender_name.setVisibility(View.VISIBLE);
                }

            } else if (disBeMsg.equals("mid")) {
                viewHolder.recent_time.setText(TimeFormat(chat.getPublish()));
                viewHolder.recent_time.setVisibility(View.VISIBLE);
                viewHolder.past_time.setVisibility(View.GONE);
                if (i < n - 1) {
                    Chat afChat = mchat.get(i + 1);
                    String disAfMsg = ShowTimeOrNot(chat.getPublish(), afChat.getPublish());
                    if (afChat.getSender().equals(chat.getSender()) && !disAfMsg.equals("big")) {     // matching two text sender is same
                        viewHolder.profile_image.setVisibility(View.INVISIBLE);

                    } else {
                        viewHolder.profile_image.setVisibility(View.VISIBLE);
                    }

                } else {
                    viewHolder.profile_image.setVisibility(View.VISIBLE);
                }

                if (beChat.getSender().equals(chat.getSender())) {
                    viewHolder.sender_name.setVisibility(View.GONE);
                } else {
                    viewHolder.sender_name.setVisibility(View.VISIBLE);
                }


            } else {
                viewHolder.sender_name.setVisibility(View.VISIBLE);
                viewHolder.past_time.setVisibility(View.VISIBLE);
                viewHolder.recent_time.setVisibility(View.GONE);
                viewHolder.past_time.setText(TimeFormat(chat.getPublish()));
                viewHolder.profile_image.setVisibility(View.VISIBLE);

                if (i < n - 1) {
                    Chat afChat = mchat.get(i + 1);
                    String disAfMsg = ShowTimeOrNot(chat.getPublish(), afChat.getPublish());
                    if (afChat.getSender().equals(chat.getSender()) && !disAfMsg.equals("big")) {     // matching two text sender is same
                        viewHolder.profile_image.setVisibility(View.INVISIBLE);
                    }
                }

            }
            //  set one pic and one title for one man


        } else {
            viewHolder.sender_name.setVisibility(View.VISIBLE);
            viewHolder.past_time.setVisibility(View.VISIBLE);
            viewHolder.recent_time.setVisibility(View.GONE);
            viewHolder.past_time.setText(TimeFormat(chat.getPublish()));
            viewHolder.profile_image.setVisibility(View.VISIBLE);
            if (i < n - 1) {
                Chat afChat = mchat.get(i + 1);
                if (afChat.getSender().equals(chat.getSender())) {
                    viewHolder.profile_image.setVisibility(View.INVISIBLE);
                }
            }

        }


        if (!chat.getMessage().equals("")) {
            viewHolder.show_message.setText(chat.getMessage());
            viewHolder.show_message.setVisibility(View.VISIBLE);
        }
        /*  if (chat.getImageUrl()!=null){
              viewHolder.show_imgMsg.setVisibility(View.VISIBLE);
              Glide.with(mcontext).load(chat.getImageUrl()).into(viewHolder.show_imgMsg);

          }*/

        if (chat.getSenderImg() == null || chat.getSenderImg().equals("default")) {
            viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mcontext.getApplicationContext()).load(chat.getSenderImg()).into(viewHolder.profile_image);
        }


        if (chat.getSenderNm() != null) {
            viewHolder.sender_name.setText(chat.getSenderNm());
        }


        if (chat.getSeenlist().size() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("Seen by ");
            for (SeenList list : chat.getSeenlist()) {
                builder.append(list.getName());
                builder.append(", ");
            }
            viewHolder.txt_seen.setText(builder);

        } else {
            viewHolder.txt_seen.setText("Delivered");
        }


       /* if (i == mchat.size() - 1) {
            viewHolder.txt_seen.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txt_seen.setVisibility(View.GONE);
        }*/


        if (chat.getSender().equals(firebaseUId)) {
            viewHolder.profile_image.setVisibility(View.INVISIBLE);
        }


        final String finalClick = click;
        final int indx = i;

        viewHolder.show_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (indx > 0) {


                    if (finalClick.equals("tny")) {
                        if (!chat.isClickOne()) {
                            viewHolder.recent_time.setVisibility(View.VISIBLE);
                            viewHolder.txt_seen.setVisibility(View.VISIBLE);
                            mchat.get(indx).setClickOne(true);
                        } else {
                            viewHolder.recent_time.setVisibility(View.GONE);
                            viewHolder.txt_seen.setVisibility(View.GONE);
                            mchat.get(indx).setClickOne(false);
                        }

                    } else {
                        if (!chat.isClickOne()) {
                            viewHolder.txt_seen.setVisibility(View.VISIBLE);
                            mchat.get(indx).setClickOne(true);
                        } else {
                            viewHolder.txt_seen.setVisibility(View.GONE);
                            mchat.get(indx).setClickOne(false);
                        }

                    }
                } else {

                    if (!chat.isClickOne()) {
                        viewHolder.txt_seen.setVisibility(View.VISIBLE);
                        mchat.get(indx).setClickOne(true);
                    } else {
                        viewHolder.txt_seen.setVisibility(View.GONE);
                        mchat.get(indx).setClickOne(false);
                    }

                        /*
                        if (finalClick.equals("tny")) {
                            if (!chat.isClickOne()) {
                                viewHolder.recent_time.setVisibility(View.VISIBLE);
                                //viewHolder.txt_seen.setVisibility(View.VISIBLE);
                                mchat.get(indx).setClickOne(true);
                            } else {
                                viewHolder.recent_time.setVisibility(View.GONE);
                                mchat.get(indx).setClickOne(false);
                            }

                        } else {
                            if (!chat.isClickOne()) {
                                viewHolder.txt_seen.setVisibility(View.VISIBLE);
                                mchat.get(indx).setClickOne(true);
                            } else {

                                mchat.get(indx).setClickOne(false);
                            }

                        }

                        *////
                }


            }
        });

        viewHolder.recyclerSeen.setVisibility(View.GONE);
        list_seen = new ArrayList<>();
        if (i < mchat.size() - 1) {

            Chat afchat = mchat.get(i + 1);
            int afsize = afchat.getSeenlist().size();
            int nowsize = chat.getSeenlist().size();
            if (afsize != 0 && nowsize != 0) {

                for (int p = 0; p < nowsize; p++) {
                    boolean have = false;
                    String url = chat.getSeenlist().get(p).getName();
                    for (int q = 0; q < afsize; q++) {
                        if (url.equals(afchat.getSeenlist().get(q).getName())) {
                            have = true;
                            break;
                        }
                    }
                    if (!have) {
                        list_seen.add(chat.getSeenlist().get(p));
                    }
                }

                if (list_seen.size() > 0) {
                    SeenImgAdapter seenImgAdapter = new SeenImgAdapter(mcontext, list_seen);
                    viewHolder.recyclerSeen.setAdapter(seenImgAdapter);
                    viewHolder.recyclerSeen.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.recyclerSeen.setVisibility(View.GONE);
                }

            }
        } else {
            if (chat.getSeenlist().size() > 0) {
                SeenImgAdapter seenImgAdapter = new SeenImgAdapter(mcontext, chat.getSeenlist());
                viewHolder.recyclerSeen.setAdapter(seenImgAdapter);
                viewHolder.recyclerSeen.setVisibility(View.VISIBLE);
            } else {
                viewHolder.recyclerSeen.setVisibility(View.GONE);
            }
        }


    }

    @Override
    public int getItemCount() {
        return mchat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message, sender_name, past_time, recent_time;
        public TextView txt_seen;
        public ImageView profile_image;
        public ImageView show_imgMsg;
        public RecyclerView recyclerSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            past_time = itemView.findViewById(R.id.past_time_id);
            recent_time = itemView.findViewById(R.id.recent_time_id);
            sender_name = itemView.findViewById(R.id.senderId);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            show_imgMsg = itemView.findViewById(R.id.show_imgMsg);

            recyclerSeen = itemView.findViewById(R.id.recy_seen_img);
            recyclerSeen.setHasFixedSize(true);
            recyclerSeen.setLayoutManager(new LinearLayoutManager(mcontext, LinearLayoutManager.HORIZONTAL, false));
        }
    }


    public String TimeFormat(long times) {
        dateUtils = new DateUtils();
        Calendar calendar = Calendar.getInstance();
        long curentmilis = calendar.getTimeInMillis();
        long h = Math.abs(curentmilis - times);

        if (h < 604800000) {  //use to update 7 days
            if (h < 86400000) {//convert hour

                return dateUtils.dayIsYesterday(times);
            } else {
                return dateUtils.WeekFromLong(times);
            }
        } else {  //greatter 7 days
            return dateUtils.dateFromLong(times);
        }

    }


    public String ShowTimeOrNot(long before, long after) {
        dateUtils = new DateUtils();
        Calendar calendar = Calendar.getInstance();
        long curentmilis = calendar.getTimeInMillis();
        long h = Math.abs(before - after);

        //use to update 7 days
        if (h < 3600000) {//convert hour

            if (h < 600000) {//convert hour

                return "tny"; //tiny
            } else {
                return "mid";
            }

        } else {
            return "big";
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing())
                return false;
        }
        return true;
    }

}
