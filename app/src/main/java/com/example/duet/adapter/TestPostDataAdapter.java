package com.example.duet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.duet.R;
import com.example.duet.model.PostData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TestPostDataAdapter extends RecyclerView.Adapter<TestPostDataAdapter.ViewHolder> {
    private final ArrayList<PostData> postDataArrayList;
    private final Context context;
    private OnItemClickListener itemClickListener;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView userProfileImage;
        public ImageView mainImage;
        public TextView nickname;
        public TextView title;
        public TextView date;

        public ViewHolder(View itemView){
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.bulletin_profile_photo);
            mainImage = itemView.findViewById(R.id.bulletin_main_image);
            nickname = itemView.findViewById(R.id.bulletin_profile_nickname);
            title = itemView.findViewById(R.id.bulletin_main_title);
            date = itemView.findViewById(R.id.bulletin_post_date);
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

        View view = inflater.inflate(R.layout.bulletin_list_item, parent, false);
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
                .error(R.drawable.logo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mainImage);
        Glide.with(context)
                .load(postDataArrayList.get(position).getWriter().getProfileUrl())
                .error(R.drawable.ic_profile_foreground)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.userProfileImage);
        holder.title.setText(postDataArrayList.get(position).getTitle());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd a HH:mm:ss");
        holder.date.setText(simpleDateFormat.format(postDataArrayList.get(position).getWriteDate()));
        holder.nickname.setText(postDataArrayList.get(position).getWriter().getNickname());
    }

    @Override
    public int getItemCount() {
        return postDataArrayList.size();
    }
}
