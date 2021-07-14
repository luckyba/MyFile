package com.luckyba.myfile.internalstorage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.R;
import com.luckyba.myfile.common.CommonListener;
import com.luckyba.myfile.data.model.InternalStorageFilesModel;
import com.luckyba.myfile.utils.Constant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class InternalStorageListAdapter extends RecyclerView.Adapter<InternalStorageViewHolder> {
    private ArrayList<InternalStorageFilesModel> internalStorageFilesModels = new ArrayList<>();
    private CommonListener.ClickListener mListener;

    public InternalStorageListAdapter(CommonListener.ClickListener listener) {
        mListener = listener;
    }

    public void setData(ArrayList<InternalStorageFilesModel> data) {
        internalStorageFilesModels.clear();
        internalStorageFilesModels = data;
    }

    @Override
    public InternalStorageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.media_list_item_view, parent, false);

        return new InternalStorageViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(InternalStorageViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(v -> mListener.onClick(v, position));
        holder.itemView.setOnLongClickListener(v -> {
            mListener.onLongClick(v, position);
            return false;
        });

        InternalStorageFilesModel internalStorageFilesModel = internalStorageFilesModels.get(position);
        holder.lblFileName.setText(internalStorageFilesModel.getFileName());
        File file = new File(internalStorageFilesModel.getFilePath());
        if (file.isDirectory()) {//if list item folder the set icon
            holder.imgItemIcon.setImageResource(R.drawable.ic_outline_folder_24);
        } else if (internalStorageFilesModel.getType() == Constant.IMAGE_TYPE) {//if list item any image then
            File imgFile = new File(internalStorageFilesModel.getFilePath());
            if (imgFile.exists()) {
                int THUMB_SIZE = 64;
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(internalStorageFilesModel.getFilePath()),
                        THUMB_SIZE, THUMB_SIZE);
                holder.imgItemIcon.setImageBitmap(ThumbImage);
            }
        } else if (internalStorageFilesModel.getType() == Constant.PDF_TYPE) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_pdf_file);
        } else if (internalStorageFilesModel.getType() == Constant.AUDIO_TYPE) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_baseline_audiotrack_24);
        } else if (internalStorageFilesModel.getType() == Constant.TXT_TYPE) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_text_file);
        } else if (internalStorageFilesModel.getType() == Constant.EXTRACT_TYPE) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_zip_folder);
        } else if (internalStorageFilesModel.getType() == Constant.DOCUMENT_TYPE) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_html_file);
        } else if (internalStorageFilesModel.getType() == Constant.VIDEO_TYPE) {
            Bitmap bMap = null;
            try {
                bMap = ThumbnailUtils.createVideoThumbnail(new File(internalStorageFilesModel.getFilePath()), new Size(50, 50), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            holder.imgItemIcon.setImageBitmap(bMap);
        } else if (internalStorageFilesModel.getType() == Constant.INSTALL_TYPE) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_apk);
        } else {
            holder.imgItemIcon.setImageResource(R.drawable.ic_un_supported_file);
        }
        if (internalStorageFilesModel.isCheckboxVisible()) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
        if (internalStorageFilesModel.isSelected()) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return internalStorageFilesModels.size();
    }


}