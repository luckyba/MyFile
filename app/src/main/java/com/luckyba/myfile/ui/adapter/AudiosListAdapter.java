package com.luckyba.myfile.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.R;
import com.luckyba.myfile.data.model.MediaFileListModel;

import java.util.List;



public class AudiosListAdapter extends RecyclerView.Adapter<AudiosListAdapter.MyViewHolder> {
    private List<MediaFileListModel> mediaFileListModels;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblFileName,lblFileSize,lblFileCreated;
        public ImageView imgItemIcon;

        public MyViewHolder(View view) {
            super(view);
            lblFileName = (TextView) view.findViewById(R.id.file_name);
            lblFileCreated= (TextView) view.findViewById(R.id.file_created);
            imgItemIcon = (ImageView) view.findViewById(R.id.icon);
            lblFileSize= (TextView) view.findViewById(R.id.file_size);
        }
    }

    public AudiosListAdapter(List<MediaFileListModel> mediaFileListModels) {
        this.mediaFileListModels = mediaFileListModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.audio_list_item_view, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        MediaFileListModel mediaFileListModel = mediaFileListModels.get(position);
        holder.lblFileName.setText(mediaFileListModel.getFileName());
        holder.lblFileSize.setText(mediaFileListModel.getFileSize());
        holder.lblFileCreated.setText(mediaFileListModel.getFileCreatedTime().substring(0,19));
        holder.imgItemIcon.setImageResource(R.drawable.ic_baseline_audiotrack_24);
    }

    @Override
    public int getItemCount() {
        return mediaFileListModels.size();
    }
}
