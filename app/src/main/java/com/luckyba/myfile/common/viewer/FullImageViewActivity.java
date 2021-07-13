package com.luckyba.myfile.common.viewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.luckyba.myfile.R;
import com.luckyba.myfile.helper.TouchImageView;

import java.io.File;


public class FullImageViewActivity extends AppCompatActivity {
    private ImageView imgBackArrow;
    private TouchImageView imageView;
    String  imagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_imageview);
        imageView = (TouchImageView) findViewById(R.id.imageView);
        imgBackArrow= (ImageView) findViewById(R.id.id_back_arrow);
        Intent intent = getIntent();
        imagePath = intent.getStringExtra("imagePath");
        imgBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
    }
}
