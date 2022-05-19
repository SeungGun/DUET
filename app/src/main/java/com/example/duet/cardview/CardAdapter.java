package com.example.duet.cardview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.RequiresApi;

import com.example.duet.R;

import java.util.ArrayList;

/**
 * @auther Me
 * @since 2022/05/06 10:36 오전
 Will change structure of Card layout
 **/

public class CardAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<CardData> sample;

    public CardAdapter(Context context, ArrayList<CardData> data){
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
        View mView = mLayoutInflater.inflate(R.layout.cardview_layout, null);

        ImageView imageView = (ImageView)mView.findViewById(R.id.imgView);
        TextView title = (TextView)mView.findViewById(R.id.title);
        TextView participate = (TextView)mView.findViewById(R.id.participate);
        TextView mentor = (TextView)mView.findViewById(R.id.mentor);

        ArrayList<String> testArrayList = sample.get(i).getGroup();
        String strAppend = String.join(" ", testArrayList);

        //imageView.setImageResource(sample.get(i).get());
        title.setText(sample.get(i).getTitle());
        participate.setText(strAppend);
        mentor.setText(sample.get(i).getMentor());

        return mView;
    }
}
