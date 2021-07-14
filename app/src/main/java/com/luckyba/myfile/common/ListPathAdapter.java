package com.luckyba.myfile.common;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.R;

import java.util.ArrayList;
import java.util.List;

public class ListPathAdapter extends RecyclerView.Adapter<ListPathAdapter.PathsViewHolder> {

    private CommonListener.ClickListener mListener;
    private List<String> listPaths = new ArrayList<>();

    public ListPathAdapter(CommonListener.ClickListener listener) {
        mListener = listener;
    }

    public void setData (List<String> listPaths) {
        this.listPaths = listPaths;
    }

    @NonNull
    @Override
    public PathsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.path_list_item, parent, false);
        return new PathsViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull PathsViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> mListener.onClick(v, position));
        holder.tvPathName.setText(listPaths.get(position));
    }

    @Override
    public int getItemCount() {
        return listPaths.size();
    }

    public class PathsViewHolder extends RecyclerView.ViewHolder{
        TextView tvPathName;

        public PathsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPathName = itemView.findViewById(R.id.tv_path_name);
        }
    }
}
