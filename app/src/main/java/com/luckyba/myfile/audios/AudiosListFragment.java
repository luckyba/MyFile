package com.luckyba.myfile.audios;

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


public class AudiosListFragment extends Fragment implements CommonListener.ClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View mRootView;
    private OnFragmentInteractionListener mListener;
    private AudiosViewManager audiosViewManager;
    private AudiosListAdapter audiosListAdapter;
    private AudiosViewModel audiosViewModel;

    public AudiosListFragment() {
        // Required empty public constructor
    }

    public static AudiosListFragment newInstance(String param1, String param2) {
        AudiosListFragment fragment = new AudiosListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_audios_list, container, false);

        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        audiosViewModel = new ViewModelProvider(this, FactoryViewModel.getInstance()).get(AudiosViewModel.class);
        audiosListAdapter = new AudiosListAdapter(this);
        audiosViewManager = new AudiosViewManager(mRootView, audiosViewModel, audiosListAdapter, getActivity(), this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        audiosListAdapter = null;
        audiosViewModel = null;
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("Audios List Fragment");
    }

    @Override
    public void onClick(View view, int position) {
        audiosViewManager.onItemClick(view, position);
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}
