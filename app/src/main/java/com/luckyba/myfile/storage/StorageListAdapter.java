package com.luckyba.myfile.storage;

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
import com.luckyba.myfile.data.model.StorageFilesModel;
import com.luckyba.myfile.utils.Constant;

import java.io.File;
import java.util.ArrayList;


public class StorageListAdapter extends RecyclerView.Adapter<StorageViewHolder> {
    private ArrayList<StorageFilesModel> storageFilesModels = new ArrayList<>();
    private Context mContext;
    private CommonListener.ClickListener mListener;

    public StorageListAdapter(Context context, CommonListener.ClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setData(ArrayList<StorageFilesModel> data) {
        storageFilesModels.clear();
        storageFilesModels = data;
    }

    @Override
    public StorageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.media_list_item_view, parent, false);

        return new StorageViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(StorageViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(v -> mListener.onClick(v, position));
        holder.itemView.setOnLongClickListener(v -> {
            mListener.onLongClick(v, position);
            return false;
        });

        StorageFilesModel storageFilesModel = storageFilesModels.get(position);
        holder.lblFileName.setText(storageFilesModel.getFileName());
        File file = new File(storageFilesModel.getFilePath());
        if (file.isDirectory()) {//if list item folder the set icon
            holder.imgItemIcon.setImageResource(R.drawable.ic_outline_folder_24);
        } else if (storageFilesModel.getType() == Constant.IMAGE_TYPE) {//if list item any image then
            Glide.with(mContext).load(new File(storageFilesModel.getFilePath()))
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.imgItemIcon);

//            File imgFile = new File(storageFilesModel.getFilePath());
//            if (imgFile.exists()) {
//                int THUMB_SIZE = 64;
//                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(storageFilesModel.getFilePath()),
//                        THUMB_SIZE, THUMB_SIZE);
//                holder.imgItemIcon.setImageBitmap(ThumbImage);
//            }
        } else if (storageFilesModel.getType() == Constant.PDF_TYPE) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_pdf_file);
        } else if (storageFilesModel.getType() == Constant.AUDIO_TYPE) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_baseline_audiotrack_24);
        } else if (storageFilesModel.getType() == Constant.TXT_TYPE) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_text_file);
        } else if (storageFilesModel.getType() == Constant.EXTRACT_TYPE) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_zip_folder);
        } else if (storageFilesModel.getType() == Constant.DOCUMENT_TYPE) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_html_file);
        } else if (storageFilesModel.getType() == Constant.VIDEO_TYPE) {
            int positionInMillis = 0;
            long interval = positionInMillis * 1000;;
            RequestOptions options = new RequestOptions().frame(interval);
            Glide.with(MyApplication.getInstance().getApplicationContext())
                    .asBitmap()
                    .load(new File(storageFilesModel.getFilePath()))
                    .override(50,50)// Example
                    .apply(options)
                    .into(holder.imgItemIcon);

//            Bitmap bMap = null;
//            try {
//                bMap = ThumbnailUtils.createVideoThumbnail(new File(storageFilesModel.getFilePath()), new Size(50, 50), null);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            holder.imgItemIcon.setImageBitmap(bMap);
        } else if (storageFilesModel.getType() == Constant.INSTALL_TYPE) {
            holder.imgItemIcon.setImageResource(R.drawable.ic_apk);
        } else {
            holder.imgItemIcon.setImageResource(R.drawable.ic_un_supported_file);
        }
        if (storageFilesModel.isCheckboxVisible()) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
        if (storageFilesModel.isSelected()) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return storageFilesModels.size();
    }


}