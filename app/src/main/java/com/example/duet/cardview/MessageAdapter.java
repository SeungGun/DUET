package com.example.duet.cardview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duet.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ItemViewHolder> {
    ArrayList<MessageData> models = new ArrayList<>();
    Context mContext;
    LayoutInflater mLayoutInflater;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(models.get(position));

    }

    @Override
    public int getItemCount() {
        return models.size();

    }

    public void addItem(MessageData data) {
        models.add(data);
    }



    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView content;
        private TextView author;

        public ItemViewHolder(View view) {
            super(view);

            content = view.findViewById(R.id.messageTextView);
            author = view.findViewById(R.id.messengerTextView);
        }

        public void onBind(MessageData messageData) {
            content.setText(messageData.getText());
            author.setText(messageData.getName());
        }
    }
}
