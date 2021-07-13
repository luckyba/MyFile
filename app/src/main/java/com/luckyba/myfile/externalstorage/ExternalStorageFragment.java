package com.luckyba.myfile.externalstorage;

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
import com.luckyba.myfile.app.MyApplication;
import com.luckyba.myfile.common.CommonListener;

public class ExternalStorageFragment extends Fragment implements CommonListener.CommunicationActivity
        , CommonListener.ClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private ExternalStorageFragment.OnFragmentInteractionListener mListener;

    private ExternalStorageViewManager externalStorageViewManager;
    private ExternalStorageListAdapter externalStorageListAdapter;
    private ExternalStorageViewModel externalStorageViewModel;
    private View mRootView;

    public ExternalStorageFragment() {
        // Required empty public constructor
    }


    public static ExternalStorageFragment newInstance(String param1, String param2) {
        ExternalStorageFragment fragment = new ExternalStorageFragment();
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
        mRootView = inflater.inflate(R.layout.fragment_external_storage, container, false);
        MyApplication.getInstance().setButtonBackPressed(this);

        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        externalStorageListAdapter = new ExternalStorageListAdapter(this);
        externalStorageViewModel = new ViewModelProvider(this, FactoryViewModel.getInstance()).get(ExternalStorageViewModel.class);
        externalStorageViewManager = new ExternalStorageViewManager(mRootView, externalStorageViewModel, externalStorageListAdapter, getActivity());

    }

    @Override
    public void onBackPressed(int navItemIndex) {
        externalStorageViewManager.onBackPressed(navItemIndex);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("External storage fragment");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
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
        externalStorageViewModel = null;
        externalStorageListAdapter = null;
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void createNewFolder() {
        externalStorageViewManager.createNewFolder();
    }

    public void createNewFile() {
        externalStorageViewManager.createNewFile();
    }

    @Override
    public void onClick(View view, int position) {
        externalStorageViewManager.onItemClick(view, position);
    }

    @Override
    public void onLongClick(View view, int position) {
        externalStorageViewManager.onItemLongClick(view, position);
    }
}
