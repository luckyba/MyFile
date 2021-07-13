package com.luckyba.myfile.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.R;
import com.luckyba.myfile.common.CommonListener;
import com.luckyba.myfile.data.model.MediaFileListModel;

import java.io.File;
import java.util.ArrayList;

public class ImagesListAdapter extends RecyclerView.Adapter<ImagesListViewHolder> {
    private ArrayList<MediaFileListModel> mediaFileListModels = new ArrayList<>();
    private CommonListener.ClickListener mListener;
    final int THUMB_SIZE = 64;

    public ImagesListAdapter(CommonListener.ClickListener listener) {
        mListener = listener;
    }

    public void setData(ArrayList<MediaFileListModel> data) {
        mediaFileListModels.clear();
        mediaFileListModels = data;
    }

    @Override
    public ImagesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.images_list_item_view, parent, false);

        return new ImagesListViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ImagesListViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(v->mListener.onClick(v, position));
        holder.itemView.setOnLongClickListener(v -> {mListener.onLongClick(v, position); return false;});

        MediaFileListModel mediaFileListModel = mediaFileListModels.get(position);
        holder.lblFileName.setText(mediaFileListModel.getFileName());
        holder.lblFileSize.setText(mediaFileListModel.getFileSize());
        holder.lblFileCreated.setText(mediaFileListModel.getFileCreatedTime().substring(0,19));
        File imgFile = new File(mediaFileListModel.getFilePath());
        if (imgFile.exists()) {
            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile( mediaFileListModel.getFilePath()),
                    THUMB_SIZE, THUMB_SIZE);
            holder.imgItemIcon.setImageBitmap(ThumbImage);
        }
    }

    @Override
    public int getItemCount() {
        return mediaFileListModels.size();
    }
}
