package com.luckyba.myfile.images;

import android.content.Context;
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

import static androidx.core.view.ViewCompat.setTransitionName;

public class ImagesListAdapter extends RecyclerView.Adapter<ImagesListViewHolder> {
    private ArrayList<MediaFileListModel> mediaFileListModels = new ArrayList<>();
    private CommonListener.ClickListener mListener;
    final int THUMB_SIZE = 64;
    private Context mContext;

    public ImagesListAdapter(CommonListener.ClickListener listener, Context context) {
        mListener = listener;
        mContext = context;
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
        // using glide to load image
        Glide.with(MyApplication.getInstance().getApplicationContext())
                .load(new File(mediaFileListModel.getFilePath()))
                .apply(new RequestOptions().centerCrop())
                .into(holder.imgItemIcon);
        setTransitionName(holder.imgItemIcon, String.valueOf(position) + "_image");

//        File imgFile = new File(mediaFileListModel.getFilePath());
//        if (imgFile.exists()) {
//            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile( mediaFileListModel.getFilePath()),
//                    THUMB_SIZE, THUMB_SIZE);
//            holder.imgItemIcon.setImageBitmap(ThumbImage);
//        }
    }

    @Override
    public int getItemCount() {
        return mediaFileListModels.size();
    }
}
