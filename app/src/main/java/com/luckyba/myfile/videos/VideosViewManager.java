package com.luckyba.myfile.videos;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.R;
import com.luckyba.myfile.app.MyApplication;
import com.luckyba.myfile.data.model.MediaFileListModel;
import com.luckyba.myfile.helper.DividerItemDecoration;
import com.luckyba.myfile.utils.Constant;

import java.io.File;
import java.util.ArrayList;

public class VideosViewManager {
    private View mRootView;
    private VideosViewModel videosViewModel;
    private VideosListAdapter videosListAdapter;
    private Activity activity;
    private RecyclerView recyclerView;
    private ArrayList<MediaFileListModel> mediaFileListModelArrayList;
    private LinearLayout noMediaLayout;
    private LifecycleOwner lifecycleOwner;

    public VideosViewManager(View mRootView, VideosViewModel videosViewModel
            , VideosListAdapter videosListAdapter, Activity activity, LifecycleOwner lifecycleOwner) {
        this.mRootView = mRootView;
        this.videosViewModel = videosViewModel;
        this.videosListAdapter = videosListAdapter;
        this.activity = activity;
        this.lifecycleOwner = lifecycleOwner;
        init();
    }

    private void init() {
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view_videos_list);
        noMediaLayout = (LinearLayout) mRootView.findViewById(R.id.noMediaLayout);

        mediaFileListModelArrayList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyApplication.getInstance().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(MyApplication.getInstance().getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(videosListAdapter);
        videosViewModel.getAllVideos(Constant.VideoDir);

        videosViewModel.getListMutableLiveData().observe(lifecycleOwner, this::updateListVideo);
    }


    private void updateListVideo(ArrayList<MediaFileListModel> mediaFileListModels) {
        if (mediaFileListModels.isEmpty()) {
            noMediaLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noMediaLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        mediaFileListModelArrayList = mediaFileListModels;
        videosListAdapter.setData(mediaFileListModelArrayList);
        videosListAdapter.notifyDataSetChanged();
    }


    public void onItemClick(View view, int pos) {
        MediaFileListModel mediaFileListModel = mediaFileListModelArrayList.get(pos);
        Uri photoURI = FileProvider.getUriForFile(activity, MyApplication.getInstance().getPackageName() + ".provider", new File(mediaFileListModel.getFilePath()));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(photoURI, "video/mp4");
        activity.startActivity(intent);
    }

    public void onItemLongClick(View view, int pos) {

    }
}
