package com.luckyba.myfile.ui.activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.luckyba.myfile.R;
import com.luckyba.myfile.ui.adapter.FullScreenImageAdapter;
import com.luckyba.myfile.app.MyApplication;


public class ImageViewActivity extends AppCompatActivity {
    private ImageView imgBackArrow;
    int imagePosition;
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        imgBackArrow= (ImageView) findViewById(R.id.id_back_arrow);
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new FullScreenImageAdapter(ImageViewActivity.this, MyApplication.getInstance().getMediaFileListModeLArray());
        Intent intent = getIntent();
        imagePosition=intent.getIntExtra("imagePosition",0);
        viewPager.setAdapter(adapter);
        // displaying selected image first
        viewPager.setCurrentItem(imagePosition);
        imgBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
