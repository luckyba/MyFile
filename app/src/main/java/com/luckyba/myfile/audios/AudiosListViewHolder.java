package com.luckyba.myfile.audios;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.R;

public class AudiosListViewHolder extends RecyclerView.ViewHolder {
    public TextView lblFileName,lblFileSize,lblFileCreated;
    public ImageView imgItemIcon;

    public AudiosListViewHolder(View view) {
        super(view);
        lblFileName = (TextView) view.findViewById(R.id.file_name);
        lblFileCreated= (TextView) view.findViewById(R.id.file_created);
        imgItemIcon = (ImageView) view.findViewById(R.id.icon);
        lblFileSize= (TextView) view.findViewById(R.id.file_size);
    }

}