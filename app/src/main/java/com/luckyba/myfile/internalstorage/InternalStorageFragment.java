package com.luckyba.myfile.internalstorage;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.luckyba.myfile.FactoryViewModel;
import com.luckyba.myfile.R;
import com.luckyba.myfile.app.MyApplication;
import com.luckyba.myfile.common.CommonListener;
import com.luckyba.myfile.common.ListPathAdapter;
import com.luckyba.myfile.data.model.MyObserver;

import java.io.File;

import static com.luckyba.myfile.utils.Constant.SCAN_DATA_CALLBACK;


public class InternalStorageFragment extends Fragment implements CommonListener.CommunicationActivity, CommonListener.ClickListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private InternalStorageListAdapter internalStorageListAdapter;
    private ListPathAdapter listPathAdapter;

    private InternalStorageViewManager internalStorageViewManager;
    private InternalStorageViewModel internalStorageViewModel;
    private View mRootView;

    public InternalStorageFragment() {
        // Required empty public constructor
    }

    public static InternalStorageFragment newInstance(String param1, String param2) {
        InternalStorageFragment fragment = new InternalStorageFragment();
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
        mRootView = inflater.inflate(R.layout.fragment_internal_storage, container, false);
        MyApplication.getInstance().setButtonBackPressed(this);

        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        listPathAdapter = new ListPathAdapter(this);
        internalStorageListAdapter = new InternalStorageListAdapter(this);
        internalStorageViewModel = new ViewModelProvider(this, FactoryViewModel.getInstance()).get(InternalStorageViewModel.class);
        internalStorageViewManager = new InternalStorageViewManager(mRootView, internalStorageViewModel, internalStorageListAdapter
                , listPathAdapter, getActivity());

//        MyApplication.getInstance()
//                .getContentResolver()
//                .registerContentObserver(
//                        Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath())), true,
//                        new MyObserver(mHandler));

    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == SCAN_DATA_CALLBACK) {
                Toast.makeText(getActivity(), " " + msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    });

    @Override
    public void onBackPressed(int navItemIndex) {
        internalStorageViewManager.onBackPressed(navItemIndex, getActivity());
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
        internalStorageViewModel = null;
        internalStorageListAdapter = null;
        super.onDestroy();
    }

    public void createNewFile() {
        internalStorageViewManager.createNewFile();
    }

    public void createNewFolder() {
        internalStorageViewManager.createNewFolder();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View view, int position) {
        internalStorageViewManager.onItemClick(view, position);
    }

    @Override
    public void onLongClick(View view, int position) {
        internalStorageViewManager.onItemLongClick(view, position);
    }
}
