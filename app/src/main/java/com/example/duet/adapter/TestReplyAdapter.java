package com.example.duet.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.duet.R;
import com.example.duet.model.ReplyData;
import com.example.duet.util.Firestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TestReplyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ReplyData> replyDataArrayList;
    private final Context context;

    public TestReplyAdapter(ArrayList<ReplyData> dataArrayList, Context context) {
        this.replyDataArrayList = dataArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (viewType == 0) {
            view = inflater.inflate(R.layout.post_reply_item, parent, false);
            return new NormalViewHolder(view);
        } else if (viewType == 1) {
            view = inflater.inflate(R.layout.reply_waiting_layout, parent, false);
            return new TestReplyAdapter.WaitingViewHolder(view);
        }
        view = inflater.inflate(R.layout.post_reply_item, parent, false);
        return new NormalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int currentPos = position;
        if (holder instanceof NormalViewHolder) {
            if (replyDataArrayList.get(currentPos).isSelected()) {
                ((NormalViewHolder) holder).writerNickname.setTextColor(Color.parseColor("#00ff00"));
            } else {
                ((NormalViewHolder) holder).writerNickname.setTextColor(Color.parseColor("#000000"));
            }
            ((NormalViewHolder) holder).writerNickname.setText(replyDataArrayList.get(position).getWriter().getNickname());
            ((NormalViewHolder) holder).body.setText((replyDataArrayList.get(position).getBody()));
            ((NormalViewHolder) holder).writeDate.setText(replyDataArrayList.get(position).getReplyDate().toString());
            Glide.with(context)
                    .load(replyDataArrayList.get(position).getWriter().getProfileUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_profile_foreground)
                    .into(((NormalViewHolder) holder).profileImage);
        } else if (holder instanceof WaitingViewHolder) {
            ((WaitingViewHolder) holder).writerNickname.setText(replyDataArrayList.get(position).getWriter().getNickname());
            ((WaitingViewHolder) holder).writeDate.setText(replyDataArrayList.get(position).getReplyDate().toString());
            ((WaitingViewHolder) holder).reliability.setText(replyDataArrayList.get(position).getWriter().getReliability() + "");
            ((WaitingViewHolder) holder).level.setText(replyDataArrayList.get(position).getWriter().getLevel() + "");
            ((WaitingViewHolder) holder).allowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReplyData currentData = replyDataArrayList.get(currentPos);
                    currentData.setViewType(0);
                    replyDataArrayList.set(currentPos, currentData);
                    notifyDataSetChanged();
                    Firestore.updateReplyWaitingState(replyDataArrayList.get(currentPos).getReplyID())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("update waiting state", "success");
                                    } else {
                                        Log.d("update waiting state", "failure");
                                    }
                                }
                            });
                }
            });

            ((WaitingViewHolder) holder).denyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Firestore.removeReplyOnPostByOwner(replyDataArrayList.get(currentPos).getReplyID()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("remove waiting reply", "success");
                            } else {
                                Log.d("remove waiting reply", "failure");
                            }
                        }
                    });
                    removeItem(currentPos);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return replyDataArrayList.size();
    }

    public void removeItem(int position) {
        replyDataArrayList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void setItem(int position, ReplyData replyData) {
        replyDataArrayList.set(position, replyData);
        notifyItemChanged(position);
        notifyDataSetChanged();
    }

    public void addItem(ReplyData replyData) {
        replyDataArrayList.add(replyData);
    }

    @Override
    public int getItemViewType(int position) {
        return replyDataArrayList.get(position).getViewType();
    }

    public static class NormalViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView writerNickname;
        public TextView body;
        public TextView writeDate;
        public ImageView profileImage;

        public NormalViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.reply_profile_image);
            writerNickname = itemView.findViewById(R.id.reply_item_writer);
            body = itemView.findViewById(R.id.reply_item_body);
            writeDate = itemView.findViewById(R.id.reply_item_date);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), R.id.action_adopt, 0, "채택하기");
            menu.add(this.getAdapterPosition(), R.id.action_delete, 1, "삭제하기");
            menu.add(this.getAdapterPosition(), R.id.action_report, 2, "신고하기");
        }
    }

    public static class WaitingViewHolder extends RecyclerView.ViewHolder {
        public TextView writeDate;
        public TextView writerNickname;
        public TextView reliability;
        public TextView level;
        public Button allowButton;
        public Button denyButton;

        public WaitingViewHolder(View itemView) {
            super(itemView);
            writeDate = itemView.findViewById(R.id.waiting_reply_date);
            writerNickname = itemView.findViewById(R.id.waiting_reply_nickname);
            reliability = itemView.findViewById(R.id.waiting_reply_reliability);
            level = itemView.findViewById(R.id.waiting_reply_level);
            allowButton = itemView.findViewById(R.id.waiting_reply_allow_btn);
            denyButton = itemView.findViewById(R.id.waiting_reply_deny_btn);
        }
    }
}
