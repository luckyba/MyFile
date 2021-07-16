package com.luckyba.myfile.videos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luckyba.myfile.R;
import com.luckyba.myfile.app.MyApplication;
import com.luckyba.myfile.common.CommonListener;
import com.luckyba.myfile.data.model.MediaFileListModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideosListAdapter extends RecyclerView.Adapter<VideoListViewHolder> {
    private List<MediaFileListModel> mediaFileListModels = new ArrayList<>();
    private CommonListener.ClickListener mListener;

    public VideosListAdapter(CommonListener.ClickListener listener) {
        mListener = listener;
    }

    public void setData(ArrayList<MediaFileListModel> data) {
        mediaFileListModels.clear();
        mediaFileListModels = data;
    }

    @Override
    public VideoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_item_view, parent, false);

        return new VideoListViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(VideoListViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(v -> mListener.onClick(v, position));
        holder.itemView.setOnLongClickListener(v -> {
            mListener.onLongClick(v, position);
            return false;
        });

        MediaFileListModel mediaFileListModel = mediaFileListModels.get(position);

        int positionInMillis = 0;
        long interval = positionInMillis * 1000;;
        RequestOptions options = new RequestOptions().frame(interval);
        Glide.with(MyApplication.getInstance().getApplicationContext())
                .asBitmap()
                .load(new File(mediaFileListModel.getFilePath()))
                .override(50,50)// Example
                .apply(options)
                .into(holder.imgItemIcon);

//        Bitmap bMap = null;
//        try {
//            bMap = ThumbnailUtils.createVideoThumbnail(new File(mediaFileListModel.getFilePath()), new Size(50,50), null);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        holder.tvFileName.setText(mediaFileListModel.getFileName());
    }

    @Override
    public int getItemCount() {
        return mediaFileListModels.size();
    }
}
