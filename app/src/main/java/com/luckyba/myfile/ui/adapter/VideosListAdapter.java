package com.luckyba.myfile.ui.adapter;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.R;
import com.luckyba.myfile.data.model.MediaFileListModel;

import java.util.List;



public class VideosListAdapter extends RecyclerView.Adapter<VideosListAdapter.MyViewHolder> {
    private List<MediaFileListModel> mediaFileListModels;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblFileName;
        public ImageView imgItemIcon;

        public MyViewHolder(View view) {
            super(view);
            lblFileName = (TextView) view.findViewById(R.id.file_name);
            imgItemIcon = (ImageView) view.findViewById(R.id.icon);
        }
    }

    public VideosListAdapter(List<MediaFileListModel> mediaFileListModels) {
        this.mediaFileListModels = mediaFileListModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_item_view, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        MediaFileListModel mediaFileListModel = mediaFileListModels.get(position);
        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(mediaFileListModel.getFilePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
        holder.lblFileName.setText(mediaFileListModel.getFileName());
        holder.imgItemIcon.setImageBitmap(bMap);
}

    @Override
    public int getItemCount() {
        return mediaFileListModels.size();
    }
}
