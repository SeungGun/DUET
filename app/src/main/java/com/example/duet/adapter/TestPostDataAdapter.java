package com.example.duet.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.duet.R;
import com.example.duet.model.PostData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TestPostDataAdapter extends RecyclerView.Adapter<TestPostDataAdapter.ViewHolder> {
    private final ArrayList<PostData> postDataArrayList;
    private final Context context;
    private OnItemClickListener itemClickListener;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView title;
        public TextView body;
        public TextView date;

        public ViewHolder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.item_img);
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            date = itemView.findViewById(R.id.date);
        }

    }
    public TestPostDataAdapter(ArrayList<PostData> data, Context context){
        postDataArrayList = data;
        this.context = context;
    }
    public interface OnItemClickListener{
        void onItemClicked(int position, PostData data);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        itemClickListener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.bulletin_post_item, parent, false);
        TestPostDataAdapter.ViewHolder viewHolder = new TestPostDataAdapter.ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostData data = null;
                int position = viewHolder.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    data = postDataArrayList.get(position);
                }
                itemClickListener.onItemClicked(position, data);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TestPostDataAdapter.ViewHolder holder, int position) {
        Glide.with(context)
                .load(postDataArrayList.get(position).getPostImageUrls().get(0))
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .override(500, 500)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
        holder.title.setText(postDataArrayList.get(position).getTitle());
        holder.body.setText(postDataArrayList.get(position).getBody());
        holder.date.setText(postDataArrayList.get(position).getWriteDate().toString());
    }

    @Override
    public int getItemCount() {
        return postDataArrayList.size();
    }
}
