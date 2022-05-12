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

public class TestReplyAdapter extends RecyclerView.Adapter<TestReplyAdapter.ViewHolder> {
    private final ArrayList<ReplyData> replyDataArrayList;
    private final Context context;

    public TestReplyAdapter(ArrayList<ReplyData> dataArrayList, Context context) {
        this.replyDataArrayList = dataArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.post_reply_item, parent, false);
        TestReplyAdapter.ViewHolder viewHolder = new TestReplyAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TestReplyAdapter.ViewHolder holder, int position) {
        holder.writerNickname.setText(replyDataArrayList.get(position).getWriter().getNickname());
        holder.body.setText((replyDataArrayList.get(position).getBody()));
        holder.writeDate.setText(replyDataArrayList.get(position).getReplyDate().toString());
    }

    @Override
    public int getItemCount() {
        return replyDataArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView writerNickname;
        public TextView body;
        public TextView writeDate;

        public ViewHolder(View itemView) {
            super(itemView);
            writerNickname = itemView.findViewById(R.id.reply_item_writer);
            body = itemView.findViewById(R.id.reply_item_body);
            writeDate = itemView.findViewById(R.id.reply_item_date);
        }
    }
}
