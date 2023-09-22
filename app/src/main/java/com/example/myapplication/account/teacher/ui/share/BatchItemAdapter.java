package com.example.myapplication.account.teacher.ui.share;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.account.StudentModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BatchItemAdapter extends RecyclerView.Adapter<BatchItemAdapter.ViewHolder>  {

    private Context context;
    private List<CombineModel> models;





    public BatchItemAdapter(Context context, List<CombineModel> models) {
        this.context = context;
        this.models = models;


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BatchItemAdapter.ViewHolder( LayoutInflater.from(context).inflate(R.layout.batch_item_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
       final CombineModel model = models.get(position);


        holder.batchname.setText(model.getBatchName());

        StudentAllListAdapter listAdapter = new StudentAllListAdapter(context, model.getModelList());
        holder.recyclerViewH.setAdapter(listAdapter);





    }

    @Override
    public int getItemCount() {
        return models.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
       public  RecyclerView recyclerViewH;
       public  TextView batchname;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerViewH = itemView.findViewById(R.id.recycler_two_Id);
            recyclerViewH.setHasFixedSize(true);
            recyclerViewH.setLayoutManager( new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            batchname = itemView.findViewById(R.id.bacth_name_txt);

        }
    }





}
