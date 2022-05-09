package com.example.duet.board;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.duet.R;
import com.example.duet.model.PostData;

public class PostContentActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private TextView postIDTextView;
    private TextView writerIDTextView;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView bodyTextView;
    private PostData data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_content);
        Intent intent = getIntent();
        data = (PostData) intent.getSerializableExtra("data");
        linearLayout = findViewById(R.id.content_container);
        postIDTextView = findViewById(R.id.content_post_id);
        writerIDTextView = findViewById(R.id.content_writer_id);
        titleTextView = findViewById(R.id.content_title);
        dateTextView = findViewById(R.id.content_write_date);
        bodyTextView = findViewById(R.id.content_body);

        postIDTextView.setText(data.getPostID());
        writerIDTextView.setText(data.getWriterID());
        titleTextView.setText(data.getTitle());
        dateTextView.setText(data.getWriteDate().toString());
        bodyTextView.setText(data.getBody());

        for(int i=0; i<data.getPostImageUrls().size(); ++i){
            ImageView imageView = new ImageView(getApplicationContext());
            linearLayout.addView(imageView);
            Glide.with(getApplicationContext())
                    .load(data.getPostImageUrls().get(i))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into((ImageView) linearLayout.getChildAt(i));
        }
    }
}