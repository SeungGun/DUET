package com.example.duet.cardview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;

import com.example.duet.ChattingRoomActivity;
import com.example.duet.R;
import com.example.duet.util.RealTimeDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BulletAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<BulletData> sample;
    DatabaseReference mRef = RealTimeDatabase.getDatabaseRef();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    //TODO Refactor for reusability

    public BulletAdapter(Context context, ArrayList<BulletData> data){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        sample = data;

    }

    @Override
    public int getCount(){
        return sample.size();
    }

    @Override
    public Object getItem(int i) {
        return sample.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        @SuppressLint({"ViewHolder", "InflateParams"})
        View mView = mLayoutInflater.inflate(R.layout.bullet_list_layout, null);

        TextView title = (TextView)mView.findViewById(R.id.bulletTitle);
        TextView content = (TextView)mView.findViewById(R.id.bulletContent);
        Button btn = (Button)mView.findViewById(R.id.makeGroupChat);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Map<String, Object> update = new HashMap<String, Object>();
                update.put(user.getUid(), true);
                mRef.child("members").child(sample.get(i).getConv_key()).updateChildren(update);
                mRef.child("chat_meta").child(sample.get(i).getConv_key()).child("members").updateChildren(update);
                Intent intent = new Intent(mContext, ChattingRoomActivity.class);
                intent.putExtra("conv_id", sample.get(i).getConv_key());
                intent.putExtra("uid", user.getUid());
                mContext.startActivity(intent);

            }
        });

        title.setText(sample.get(i).getTitle());
        content.setText(sample.get(i).getContent());



        return mView;
    }
}
