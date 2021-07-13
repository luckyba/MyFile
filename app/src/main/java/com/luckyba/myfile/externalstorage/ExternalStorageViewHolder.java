package com.luckyba.myfile.externalstorage;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.R;

public class ExternalStorageViewHolder extends RecyclerView.ViewHolder {
    public TextView lblFileName;
    public ImageView imgItemIcon;
    public CheckBox checkBox;

    public ExternalStorageViewHolder(View view) {
        super(view);
        lblFileName = (TextView) view.findViewById(R.id.file_name);
        imgItemIcon = (ImageView) view.findViewById(R.id.icon);
        checkBox = (CheckBox) view.findViewById(R.id.checkBox);
    }
}