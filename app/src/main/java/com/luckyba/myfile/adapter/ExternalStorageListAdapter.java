package com.luckyba.myfile.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.R;
import com.luckyba.myfile.model.ExternalStorageFilesModel;

import java.io.File;
import java.util.List;

public class ExternalStorageListAdapter extends RecyclerView.Adapter<ExternalStorageListAdapter.MyViewHolder> {
    private List<ExternalStorageFilesModel> externalStorageFilesModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblFileName;
        public ImageView imgItemIcon;
        public CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            lblFileName = (TextView) view.findViewById(R.id.file_name);
            imgItemIcon = (ImageView) view.findViewById(R.id.icon);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        }
    }

    public ExternalStorageListAdapter(List<ExternalStorageFilesModel> externalStorageFilesModelList) {
        this.externalStorageFilesModelList = externalStorageFilesModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.media_list_item_view, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ExternalStorageFilesModel externalStorageFilesModel = externalStorageFilesModelList.get(position);
        holder.lblFileName.setText(externalStorageFilesModel.getFileName());
        String fileExtension = externalStorageFilesModel.getFileName().substring(externalStorageFilesModel.getFileName().lastIndexOf(".") + 1);
        File file = new File(externalStorageFilesModel.getFilePath());
        if (file.isDirectory()) {//if list item folder the set icon
            holder.imgItemIcon.setImageResource(R.drawable.ic_outline_folder_24);
        } else if (fileExtension.equals("png") || fileExtension.equals("jpeg") || fileExtension.equals("jpg")) {//if list item any image then
            File imgFile = new File(externalStorageFilesModel.getFilePath());
            if (imgFile.exists()) {
                int THUMB_SIZE = 64;
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(externalStorageFilesModel.getFilePath()),
                        THUMB_SIZE, THUMB_SIZE);
                holder.imgItemIcon.setImageBitmap(ThumbImage);
            }
        } else if (fileExtension.equals("pdf")) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_pdf_file);
        } else if (fileExtension.equals("mp3")) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_baseline_audiotrack_24);
        } else if (fileExtension.equals("txt")) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_text_file);
        } else if (fileExtension.equals("zip") || fileExtension.equals("rar")) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_zip_folder);
        } else if (fileExtension.equals("html") || fileExtension.equals("xml")) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_html_file);
        } else if (fileExtension.equals("mp4") || fileExtension.equals("3gp") || fileExtension.equals("wmv") || fileExtension.equals("avi")) {
            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(externalStorageFilesModel.getFilePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
            holder.imgItemIcon.setImageBitmap(bMap);
        } else if (fileExtension.equals("apk")) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_apk);
        } else {
            holder.imgItemIcon.setImageResource(R.drawable.ic_un_supported_file);
        }
        if(externalStorageFilesModel.isCheckboxVisible()){
            holder.checkBox.setVisibility(View.VISIBLE);
        }else{
            holder.checkBox.setVisibility(View.GONE);
        }
        if(externalStorageFilesModel.isSelected()){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return externalStorageFilesModelList.size();
    }
}