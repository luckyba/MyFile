package com.luckyba.myfile.images;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luckyba.myfile.R;
import com.luckyba.myfile.Utils.Constant;
import com.luckyba.myfile.app.MyApplication;
import com.luckyba.myfile.data.model.MediaFileListModel;
import com.luckyba.myfile.helper.DividerItemDecoration;

import java.util.ArrayList;

public class ImagesListManager {

    private ArrayList<MediaFileListModel> imageListModelsArray;
    private ImagesListAdapter imagesListAdapter;
    private LinearLayout noMediaLayout;
    private RecyclerView recyclerView;
    private View mRootView;
    private ImagesListViewModel imagesListViewModel;
    private Activity activity;

    public ImagesListManager(View root, ImagesListViewModel viewModel, ImagesListAdapter adapter, Activity activity) {
        mRootView = root;
        imagesListViewModel = viewModel;
        imagesListAdapter = adapter;
        this.activity = activity;
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

        getImagesList();

    }

    private void getImagesList() {
        imageListModelsArray = imagesListViewModel.getAllInternal(Constant.ImageDir);
        if (imageListModelsArray.isEmpty()) {
            noMediaLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noMediaLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        Log.d("fdsafsaf1", imageListModelsArray.toString());
        imagesListAdapter.setData(imageListModelsArray);
        imagesListAdapter.notifyDataSetChanged();
    }

    public void onItemClick (View view, int pos) {

    }

    public void onItemLongClick (View view, int pos) {

    }
}
