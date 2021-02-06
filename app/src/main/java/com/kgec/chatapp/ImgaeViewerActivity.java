package com.kgec.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImgaeViewerActivity extends AppCompatActivity {
    private ImageView imageview;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgae_viewer);

        imageview=findViewById(R.id.image_viewer);
        imageUrl=getIntent().getStringExtra("url");

        Picasso.get().load(imageUrl).into(imageview);



    }
}