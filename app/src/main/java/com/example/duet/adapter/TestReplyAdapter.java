package com.example.duet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.duet.R;
import com.example.duet.model.ReplyData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TestReplyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<ReplyData> replyDataArrayList;
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
        if (holder instanceof NormalViewHolder) {
            ((NormalViewHolder) holder).writerNickname.setText(replyDataArrayList.get(position).getWriter().getNickname());
            ((NormalViewHolder) holder).body.setText((replyDataArrayList.get(position).getBody()));
            ((NormalViewHolder) holder).writeDate.setText(replyDataArrayList.get(position).getReplyDate().toString());
        } else if (holder instanceof WaitingViewHolder) {
            ((WaitingViewHolder) holder).writerNickname.setText(replyDataArrayList.get(position).getWriter().getNickname());
            ((WaitingViewHolder) holder).writeDate.setText(replyDataArrayList.get(position).getReplyDate().toString());
            ((WaitingViewHolder) holder).reliability.setText(replyDataArrayList.get(position).getWriter().getReliability()+"");
        }


    }

    @Override
    public int getItemCount() {
        return replyDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return replyDataArrayList.get(position).getViewType();
    }

    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        public TextView writerNickname;
        public TextView body;
        public TextView writeDate;

        public NormalViewHolder(View itemView) {
            super(itemView);
            writerNickname = itemView.findViewById(R.id.reply_item_writer);
            body = itemView.findViewById(R.id.reply_item_body);
            writeDate = itemView.findViewById(R.id.reply_item_date);
        }
    }

    public static class WaitingViewHolder extends RecyclerView.ViewHolder {
        public TextView writeDate;
        public TextView writerNickname;
        public TextView reliability;

        public WaitingViewHolder(View itemView) {
            super(itemView);
            writeDate = itemView.findViewById(R.id.waiting_reply_date);
            writerNickname = itemView.findViewById(R.id.waiting_reply_nickname);
            reliability = itemView.findViewById(R.id.waiting_reply_reliability);
        }
    }
}
