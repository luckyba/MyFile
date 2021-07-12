package com.luckyba.myfile.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.luckyba.myfile.R;
import com.luckyba.myfile.ui.helper.TouchImageView;
import com.luckyba.myfile.data.model.MediaFileListModel;

import java.io.File;
import java.util.ArrayList;


public class FullScreenImageAdapter extends PagerAdapter {
    private Activity _activity;
    private ArrayList<MediaFileListModel> mediaFileListModelArrayList;
    // constructor
    public FullScreenImageAdapter(Activity activity, ArrayList<MediaFileListModel> mediaFileListModelArrayList) {
        this._activity = activity;
        this.mediaFileListModelArrayList=mediaFileListModelArrayList;
    }

    @Override
    public int getCount() {
        return this.mediaFileListModelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,false);
        TouchImageView imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        MediaFileListModel mediaFileListModel=mediaFileListModelArrayList.get(position);
        File imgFile = new File(mediaFileListModel.getFilePath());
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgDisplay.setImageBitmap(myBitmap);
        }
        ((ViewPager) container).addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}