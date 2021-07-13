package com.luckyba.myfile.videos;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.R;

public class VideoListViewHolder extends RecyclerView.ViewHolder {
    public TextView tvFileName;
    public ImageView imgItemIcon;

    public VideoListViewHolder(View view) {
        super(view);
        tvFileName = (TextView) view.findViewById(R.id.file_name);
        imgItemIcon = (ImageView) view.findViewById(R.id.icon);
    }
}