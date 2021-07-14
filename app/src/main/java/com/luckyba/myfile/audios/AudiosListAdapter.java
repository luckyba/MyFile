package com.luckyba.myfile.audios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.R;
import com.luckyba.myfile.common.CommonListener;
import com.luckyba.myfile.data.model.MediaFileListModel;

import java.util.ArrayList;

public class AudiosListAdapter extends RecyclerView.Adapter<AudiosListViewHolder> {
    private ArrayList<MediaFileListModel> mediaFileListModels = new ArrayList<>();
    private CommonListener.ClickListener mListener;

    public AudiosListAdapter(CommonListener.ClickListener listener) {
        mListener = listener;
    }

    public void setData(ArrayList<MediaFileListModel> data) {
        mediaFileListModels.clear();
        mediaFileListModels = data;
    }

    @Override
    public AudiosListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.audio_list_item_view, parent, false);

        return new AudiosListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AudiosListViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(v -> mListener.onClick(v, position));
        holder.itemView.setOnLongClickListener(v -> {
            mListener.onLongClick(v, position);
            return false;
        });

        MediaFileListModel mediaFileListModel = mediaFileListModels.get(position);
        holder.lblFileName.setText(mediaFileListModel.getFileName());
        holder.lblFileSize.setText(mediaFileListModel.getFileSize());
        holder.lblFileCreated.setText(mediaFileListModel.getFileCreatedTime().substring(0, 19));
        holder.imgItemIcon.setImageResource(R.drawable.ic_baseline_audiotrack_24);
    }

    @Override
    public int getItemCount() {
        return mediaFileListModels.size();
    }
}
