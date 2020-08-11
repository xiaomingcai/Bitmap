package com.example.bitmap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.bitmap.view.LargeImageView;

import java.io.IOException;
import java.io.InputStream;

public class BigPicActivity extends AppCompatActivity {
    private LargeImageView largeImageView;
    private ImageView ivpc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_pic);
        try {
            InputStream inputStream = getAssets().open("qmsht.jpg");
            largeImageView = findViewById(R.id.ivLargeImageView);
            largeImageView.getImageInputStream(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
