package com.luckyba.myfile.images;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.R;
import com.luckyba.myfile.app.MyApplication;
import com.luckyba.myfile.common.viewer.FullImageViewActivity;
import com.luckyba.myfile.data.model.MediaFileListModel;
import com.luckyba.myfile.helper.DividerItemDecoration;
import com.luckyba.myfile.utils.Constant;

import java.util.ArrayList;

public class ImagesListManager {

    private ArrayList<MediaFileListModel> imageListModelsArray;
    private ImagesListAdapter imagesListAdapter;
    private LinearLayout noMediaLayout;
    private RecyclerView recyclerView;
    private View mRootView;
    private ImagesListViewModel imagesListViewModel;
    private Activity activity;
    private LifecycleOwner lifecycleOwner;

    public ImagesListManager(View root, ImagesListViewModel viewModel, ImagesListAdapter adapter
            , Activity activity, LifecycleOwner owner) {
        mRootView = root;
        imagesListViewModel = viewModel;
        imagesListAdapter = adapter;
        this.activity = activity;
        lifecycleOwner = owner;
        init();
    }

    private void init() {
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view_images_list);
        noMediaLayout = (LinearLayout) mRootView.findViewById(R.id.noMediaLayout);
        imageListModelsArray = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyApplication.getInstance().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(MyApplication.getInstance().getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(imagesListAdapter);

        imagesListViewModel.getAllInternal(Constant.ImageDir);
        imagesListViewModel.getListMutableLiveData().observe(lifecycleOwner, this::updateListImage);

    }

    private void updateListImage (ArrayList<MediaFileListModel> mediaFileListModels) {
        if (mediaFileListModels.isEmpty()) {
            noMediaLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noMediaLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        imageListModelsArray = mediaFileListModels;
        imagesListAdapter.setData(imageListModelsArray);
        imagesListAdapter.notifyDataSetChanged();
    }

    public void onItemClick (View view, int pos) {
        Intent imageIntent = new Intent(MyApplication.getInstance(), FullImageViewActivity.class);
        imageIntent.putExtra("imagePath", imageListModelsArray.get(pos).getFilePath());
        activity.startActivity(imageIntent);
    }

    public void onItemLongClick (View view, int pos) {
        Toast.makeText(activity, " Long click item image pos "+ pos, Toast.LENGTH_LONG).show();
    }
}
