package com.luckyba.myfile.images;

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


public class ImagesListFragment extends Fragment implements CommonListener.ClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private View mRootView;
    private ImagesListViewModel imagesListViewModel;
    private ImagesListAdapter imagesListAdapter;
    private ImagesListManager imagesListManager;

    private OnFragmentInteractionListener mListener;


    public ImagesListFragment() {
        // Required empty public constructor
    }

    public static ImagesListFragment newInstance(String param1, String param2) {
        ImagesListFragment fragment = new ImagesListFragment();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_images_list, container, false);

        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        imagesListViewModel = new ViewModelProvider(this, FactoryViewModel.getInstance()).get(ImagesListViewModel.class);
        imagesListAdapter = new ImagesListAdapter(this, getContext());
        imagesListManager = new ImagesListManager(mRootView, imagesListViewModel, imagesListAdapter, getActivity(), this);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        imagesListAdapter = null;
        imagesListViewModel = null;
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View view, int position) {
        imagesListManager.onItemClick(view, position);
    }

    @Override
    public void onLongClick(View view, int position) {
        imagesListManager.onItemLongClick(view, position);
    }
}
