package com.luckyba.myfile.videos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.luckyba.myfile.FactoryViewModel;
import com.luckyba.myfile.R;
import com.luckyba.myfile.common.CommonListener;

public class VideosListFragment extends Fragment implements CommonListener.ClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private View mRootView;
    private VideosViewModel videosViewModel;
    private VideosListAdapter videosListAdapter;
    private VideosViewManager videosViewManager;

    private OnFragmentInteractionListener mListener;

    public VideosListFragment() {
        // Required empty public constructor
    }

    public static VideosListFragment newInstance(String param1, String param2) {
        VideosListFragment fragment = new VideosListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView =inflater.inflate(R.layout.fragment_videos_list, container, false);

        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        videosListAdapter = new VideosListAdapter(this);
        videosViewModel = new ViewModelProvider(this, FactoryViewModel.getInstance()).get(VideosViewModel.class);
        videosViewManager = new VideosViewManager(mRootView, videosViewModel, videosListAdapter, getActivity(), this);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        videosViewModel = null;
        videosListAdapter = null;
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View view, int position) {
        videosViewManager.onItemClick(view, position);
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}
